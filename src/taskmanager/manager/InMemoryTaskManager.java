package taskmanager.manager;

import taskmanager.utiltask.Epic;
import taskmanager.utiltask.Status;
import taskmanager.utiltask.Subtask;
import taskmanager.utiltask.Task;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

public class InMemoryTaskManager implements TaskManager {

    protected final Map<Integer, Task> tasks;
    protected final Map<Integer, Subtask> subtasks;
    protected final Map<Integer, Epic> epics;
    protected int nextId;
    protected final HistoryManager historyManager;

    protected final Set<Task> prioritizedTasks = new TreeSet<>(Comparator
            .comparing(Task::getStartTime, Comparator.nullsLast(Comparator.naturalOrder()))
            .thenComparing(Task::getId));

    public InMemoryTaskManager() {
        tasks = new HashMap<>();
        subtasks = new HashMap<>();
        epics = new HashMap<>();
        nextId = 1;
        historyManager = Manager.getDefaultHistory();
    }

    // Получение следующего идентификатора
    private int getNextId() {
        return nextId++;
    }

    protected void updateNextId(int id) {
        if (this.nextId <= id) {
            this.nextId = id + 1;
        }
    }

    public List<Task> getPrioritizedTasks() {
        return new ArrayList<>(prioritizedTasks);
    }

    public static boolean isOverlapping(Task a, Task b) {
        if (a == null || b == null) return false;
        LocalDateTime aStart = a.getStartTime();
        LocalDateTime aEnd = a.getEndTime();
        LocalDateTime bStart = b.getStartTime();
        LocalDateTime bEnd = b.getEndTime();
        if (aStart == null || aEnd == null || bStart == null || bEnd == null) return false;
        return !aEnd.isBefore(bStart) && !bEnd.isBefore(aStart);
    }

    public boolean hasIntersection(Task task) {
        if (task == null || task.getStartTime() == null || task.getEndTime() == null) return false;
        return prioritizedTasks.stream()
                .filter(t -> t.getId() != task.getId())
                .anyMatch(t -> isOverlapping(task, t));
    }

    // Получение задачи по идентификатору
    @Override
    public Task getTaskById(int id) {
        Task task = tasks.get(id);
        historyManager.add(task);
        return task;
    }

    // Получение подзадачи по идентификатору
    @Override
    public Subtask getSubtaskById(int id) {
        Subtask subtask = subtasks.get(id);
        historyManager.add(subtask);
        return subtask;
    }

    // Получение эпика по идентификатору
    @Override
    public Epic getEpicById(int id) {
        Epic epic = epics.get(id);
        historyManager.add(epic);
        return epic;
    }

    // Получение истории
    @Override
    public List<Task> getHistory() {
        return historyManager.getHistory();
    }

    // Создание задачи
    @Override
    public void createTask(Task task) {
        if (task.getStartTime() != null && hasIntersection(task)) {
            throw new IllegalArgumentException("Новая задача пересекается по времени с существующей!");
        }
        task.setId(getNextId());
        tasks.put(task.getId(), task);
        if (task.getStartTime() != null) {
            prioritizedTasks.add(task);
        }
    }

    // Создание подзадачи
    @Override
    public void createSubtask(Subtask subtask) {
        if (subtask.getEpicID() == subtask.getId()) {
            throw new IllegalArgumentException("Эпик не может ссылаться на самого себя как подзадача.");
        }
        if (subtask.getStartTime() != null && hasIntersection(subtask)) {
            throw new IllegalArgumentException("Подзадача пересекается по времени с другой задачей или подзадачей!");
        }
        subtask.setId(getNextId());
        subtasks.put(subtask.getId(), subtask);

        // обновить приоритеты
        if (subtask.getStartTime() != null) {
            prioritizedTasks.add(subtask);
        }
        Epic epic = epics.get(subtask.getEpicID());
        if (epic != null) {
            epic.addSubtask(subtask.getId());
            updateEpicStatus(epic.getId());
            epic.recalculate(getSubtasksByEpic(epic.getId()));
        }
    }

    // Создание эпика
    @Override
    public void createEpic(Epic epic) {
        epic.setId(getNextId());
        epics.put(epic.getId(), epic);
    }

    // Обновление задачи (Task)
    @Override
    public void updateTask(Task task) {
        if (!tasks.containsKey(task.getId())) {
            return;
        }
        Task existingTask = tasks.get(task.getId());
        if (existingTask.getStartTime() != null) {
            prioritizedTasks.remove(existingTask);
        }
        if (task.getStartTime() != null && hasIntersection(task)) {
            throw new IllegalArgumentException("Обновление задачи приведет к пересечению!");
        }
        Task updatedTask = new Task(existingTask.getId(), existingTask.getTitle(), existingTask.getDescription(),
                task.getStatus());
        updatedTask.setDuration(task.getDuration());
        updatedTask.setStartTime(task.getStartTime());
        tasks.put(updatedTask.getId(), updatedTask);

        if (updatedTask.getStartTime() != null) {
            prioritizedTasks.add(updatedTask);
        }
    }

    // Обновление подзадачи (Subtask)
    @Override
    public void updateSubtask(Subtask subtask) {
        if (!subtasks.containsKey(subtask.getId())) {
            return;
        }
        Subtask existingSubtask = subtasks.get(subtask.getId());
        if (existingSubtask.getStartTime() != null) {
            prioritizedTasks.remove(existingSubtask);
        }
        if (subtask.getStartTime() != null && hasIntersection(subtask)) {
            throw new IllegalArgumentException("Обновление подзадачи приведет к пересечению!");
        }
        Subtask updatedSubtask = new Subtask(existingSubtask.getTitle(), existingSubtask.getDescription(),
                existingSubtask.getEpicID());
        updatedSubtask.setId(existingSubtask.getId());
        updatedSubtask.setStatus(subtask.getStatus());
        updatedSubtask.setDuration(subtask.getDuration());
        updatedSubtask.setStartTime(subtask.getStartTime());
        subtasks.put(updatedSubtask.getId(), updatedSubtask);

        if (updatedSubtask.getStartTime() != null) {
            prioritizedTasks.add(updatedSubtask);
        }
        Epic epic = epics.get(subtask.getEpicID());
        if (epic != null) {
            updateEpicStatus(epic.getId());
            epic.recalculate(getSubtasksByEpic(epic.getId()));
        }
    }

    // Обновление эпика (Epic)
    @Override
    public void updateEpic(Epic epic) {
        if (epics.containsKey(epic.getId())) {
            Epic existingEpic = epics.get(epic.getId());
            // Создаем новый обновленный объект
            Epic updatedEpic = new Epic(epic.getId(), epic.getTitle(), epic.getDescription(),
                    existingEpic.getSubtaskIds());
            epics.put(updatedEpic.getId(), updatedEpic);
            updateEpicStatus(updatedEpic.getId());
            epic.recalculate(getSubtasksByEpic(epic.getId()));
        }
    }

    // Обновление статуса эпика
    protected void updateEpicStatus(int epicId) {
        Epic epic = epics.get(epicId);
        if (epic != null) {
            // Используем Stream API вместо цикла
            boolean hasSubtasks = !epic.getSubtaskIds().isEmpty();
            List<Subtask> subtasksOfEpic = epic.getSubtaskIds().stream()
                    .map(subtasks::get)
                    .filter(Objects::nonNull)
                    .toList();

            boolean allNew = subtasksOfEpic.stream().allMatch(sub -> sub.getStatus() == Status.NEW);
            boolean allDone = subtasksOfEpic.stream().allMatch(sub -> sub.getStatus() == Status.DONE);

            if (!hasSubtasks || allNew) {
                epic.setStatus(Status.NEW);
            } else if (allDone) {
                epic.setStatus(Status.DONE);
            } else {
                epic.setStatus(Status.IN_PROGRESS);
            }
        }
    }

    // Удаление задачи по идентификатору
    @Override
    public void deleteTask(int id) {
        Task removed = tasks.remove(id);
        if (removed != null && removed.getStartTime() != null) {
            prioritizedTasks.remove(removed);
        }
        historyManager.remove(id);
    }

    // Удаление подзадачи по идентификатору
    public void deleteSubtasks(int id) {
        Subtask subtask = subtasks.remove(id);
        if (subtask != null) {
            if (subtask.getStartTime() != null) {
                prioritizedTasks.remove(subtask);
            }
            historyManager.remove(id);
            Epic epic = epics.get(subtask.getEpicID());
            if (epic != null) {
                epic.getSubtaskIds().remove((Integer) id);
                updateEpicStatus(epic.getId());
                epic.recalculate(getSubtasksByEpic(epic.getId()));
            }
        }
    }

    // Удаление эпика по идентификатору
    @Override
    public void deleteEpic(int id) {
        Epic epic = epics.remove(id);
        historyManager.remove(id);
        if (epic != null) {
            // Используем Stream API вместо цикла
            epic.getSubtaskIds()
                    .forEach(subtaskId -> {
                        Subtask removed = subtasks.remove(subtaskId);
                        if (removed != null && removed.getStartTime() != null) {
                            prioritizedTasks.remove(removed);
                        }
                        historyManager.remove(subtaskId);
                    });
        }
    }

    // Удаление всех задач
    @Override
    public void deleteAllTasks() {
        tasks.keySet().forEach(taskId -> {
            Task task = tasks.get(taskId);
            if (task != null && task.getStartTime() != null) {
                prioritizedTasks.remove(task);
            }
            historyManager.remove(taskId);
        });
        tasks.clear();
    }

    // Удаление всех подзадач
    @Override
    public void deleteAllSubtasks() {
        subtasks.keySet().forEach(subtaskId -> {
            Subtask subtask = subtasks.get(subtaskId);
            if (subtask != null && subtask.getStartTime() != null) {
                prioritizedTasks.remove(subtask);
            }
            historyManager.remove(subtaskId);
        });
        subtasks.clear();
        epics.values().forEach(epic -> {
            epic.getSubtaskIds().clear();
            updateEpicStatus(epic.getId());
        });
    }

    // Удаление всех эпиков
    @Override
    public void deleteAllEpics() {
        epics.keySet().forEach(epicId -> {
            historyManager.remove(epicId);
            Epic epic = epics.get(epicId);
            if (epic != null) {
                epic.getSubtaskIds().forEach(subtaskId -> {
                    Subtask subtask = subtasks.remove(subtaskId);
                    if (subtask != null && subtask.getStartTime() != null) {
                        prioritizedTasks.remove(subtask);
                    }
                    historyManager.remove(subtaskId);
                });
            }
        });
        epics.clear();
    }

    // Получение всех задач
    @Override
    public List<Task> getAllTasks() {
        return new ArrayList<>(tasks.values());
    }

    // Получение всех подзадач
    @Override
    public List<Subtask> getAllSubtasks() {
        return new ArrayList<>(subtasks.values());
    }

    // Получение всех эпиков
    @Override
    public List<Epic> getAllEpics() {
        return new ArrayList<>(epics.values());
    }

    // Получение всех подзадач определённого эпика
    @Override
    public List<Subtask> getSubtasksByEpic(int epicId) {
        Epic epic = getEpicById(epicId);
        if (epic == null) return Collections.emptyList();
        return epic.getSubtaskIds().stream()
                .map(subtasks::get)
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
    }
}

