package taskmanager.manager;

import taskmanager.utiltask.Epic;
import taskmanager.utiltask.Status;
import taskmanager.utiltask.Subtask;
import taskmanager.utiltask.Task;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class InMemoryTaskManager implements TaskManager {

    private final HashMap<Integer, Task> tasks;
    private final HashMap<Integer, Subtask> subtasks;
    private final HashMap<Integer, Epic> epics;
    private int nextId;
    private final HistoryManager historyManager;

    public InMemoryTaskManager() {
        tasks = new HashMap<>();
        subtasks = new HashMap<>();
        epics = new HashMap<>();
        nextId = 1;
        historyManager = Manager.getDefaultHistory();
    }

    // Получение следующего идентификатора
    public int getNextId() {
        return nextId++;
    }

    // Получение задачи по идентификатору
    @Override
    public Task getTaskById(int id) {
        Task task = tasks.get(id);
        if (task != null) {
            historyManager.add(task);
        }
        return task;
    }

    // Получение подзадачи по идентификатору
    @Override
    public Subtask getSubtaskById(int id) {
        Subtask subtask = subtasks.get(id);
        if (subtask != null) {
            historyManager.add(subtask);
        }
        return subtask;
    }

    // Получение эпика по идентификатору
    @Override
    public Epic getEpicById(int id) {
        Epic epic = epics.get(id);
        if (epic != null) {
            historyManager.add(epic);
        }
        return epic;
    }

    // Получение истории
    public List<Task> getHistory() {
        return historyManager.getHistory();
    }

    // Создание задачи
    @Override
    public void createTask(Task task) {
        task.setId(getNextId());
        tasks.put(task.getId(), task);
    }

    // Создание подзадачи
    @Override
    public void createSubtask(Subtask subtask) {
        if (subtask.getEpicID() == subtask.getId()) {
            throw new IllegalArgumentException("Эпик не может ссылаться на самого себя как подзадача.");
        }
        subtask.setId(getNextId());
        subtasks.put(subtask.getId(), subtask);
        Epic epic = epics.get(subtask.getEpicID());
        if (epic != null) {
            epic.addSubtask(subtask.getId());
            updateEpicStatus(epic.getId());
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
        if (tasks.containsKey(task.getId())) {
            tasks.put(task.getId(), task);
        }
    }

    // Обновление подзадачи (Subtask)
    @Override
    public void updateSubtask(Subtask subtask) {
        if (subtasks.containsKey(subtask.getId())) {
            subtasks.put(subtask.getId(), subtask);
            Epic epic = epics.get(subtask.getEpicID());
            if (epic != null) {
                updateEpicStatus(epic.getId());
            }
        }
    }

    // Обновление эпика (Epic)
    @Override
    public void updateEpic(Epic epic) {
        if (epics.containsKey(epic.getId())) {
            epics.put(epic.getId(), epic);
        }
    }

    // Обновление статуса эпика
    private void updateEpicStatus(int epicId) {
        Epic epic = epics.get(epicId);
        if (epic != null) {
            boolean hasSubtasks = !epic.getSubtaskIds().isEmpty();
            boolean allNew = true;
            boolean allDone = true;

            for (Integer subtaskId : epic.getSubtaskIds()) {
                Subtask sub = subtasks.get(subtaskId);
                if (sub != null) {
                    Status status = sub.getStatus();
                    if (status != Status.NEW) {
                        allNew = false;
                    }
                    if (status != Status.DONE) {
                        allDone = false;
                    }
                }
            }

            // Логика для обновления статуса эпика
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
        tasks.remove(id);
    }

    // Удаление подзадачи по идентификатору
    @Override
    public void deleteSubtasks(int id) {
        subtasks.remove(id);
    }

    // Удаление эпика по идентификатору
    @Override
    public void deleteEpic(int id) {
        Epic epic = epics.remove(id);
        if (epic != null) {
            // Удаляем все подзадачи, связанные с этим эпиком
            for (Integer subtaskId : epic.getSubtaskIds()) {
                subtasks.remove(subtaskId);
            }
        }
    }

    // Удаление всех задач
    @Override
    public void deleteAllTasks() {
        tasks.clear();
    }

    // Удаление всех подзадач
    @Override
    public void deleteAllSubtasks() {
        subtasks.clear();
        for (Epic epic : epics.values()) {
            epic.getSubtaskIds().clear();
            updateEpicStatus(epic.getId());
        }
    }

    // Удаление всех эпиков
    @Override
    public void deleteAllEpics() {
        epics.clear();
        subtasks.clear();
    }

    // Получение всех задач
    @Override
    public ArrayList<Task> getAllTasks() {
        return new ArrayList<>(tasks.values());
    }

    // Получение всех подзадач
    @Override
    public ArrayList<Subtask> getAllSubtasks() {
        return new ArrayList<>(subtasks.values());
    }

    // Получение всех эпиков
    @Override
    public ArrayList<Epic> getAllEpics() {
        return new ArrayList<>(epics.values());
    }

    // Получение всех подзадач определённого эпика
    @Override
    public ArrayList<Subtask> getSubtasksByEpic(int epicId) {
        Epic epic = getEpicById(epicId);
        ArrayList<Subtask> result = new ArrayList<>();

        if (epic != null) {
            for (Integer subtaskId : epic.getSubtaskIds()) {
                result.add(subtasks.get(subtaskId));
            }

        }
        return result;
    }
}

