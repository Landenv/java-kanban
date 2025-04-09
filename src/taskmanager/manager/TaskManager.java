package taskmanager.manager;

import taskmanager.utiltask.Task;
import taskmanager.utiltask.Subtask;
import taskmanager.utiltask.Epic;
import taskmanager.utiltask.Status;

import java.util.ArrayList;
import java.util.HashMap;

public class TaskManager {

    private final HashMap<Integer, Task> tasks;
    private final HashMap<Integer, Subtask> subtasks;
    private final HashMap<Integer, Epic> epics;
    private int nextId;

    public TaskManager() {
        tasks = new HashMap<>();
        subtasks = new HashMap<>();
        epics = new HashMap<>();
        nextId = 1;
    }

    // Получение следующего идентификатора
    private int getNextId() {
        return nextId++;
    }

    // Получение задачи по идентификатору
    public Task getTaskById(int id) {
        return tasks.get(id);
    }

    // Получение подзадачи по идентификатору
    public Subtask getSubtaskById(int id) {
        return subtasks.get(id);
    }

    // Получение эпика по идентификатору
    public Epic getEpicById(int id) {
        return epics.get(id);
    }

    // Создание задачи
    public void createTask(Task task) {
        task.setId(getNextId());
        tasks.put(task.getId(), task);
    }

    // Создание подзадачи
    public void createSubtask(Subtask subtask) {
        subtask.setId(getNextId());
        subtasks.put(subtask.getId(), subtask);
        Epic epic = epics.get(subtask.getEpicID());
        if (epic != null) {
            epic.addSubtask(subtask.getId());
            updateEpicStatus(epic.getId());
        }
    }

    // Создание эпика
    public void createEpic(Epic epic) {
        epic.setId(getNextId());
        epics.put(epic.getId(), epic);
    }

    // Обновление задачи (Task)
    public void updateTask(Task task) {
        if (tasks.containsKey(task.getId())) {
            tasks.put(task.getId(), task);
        }
    }

    // Обновление подзадачи (Subtask)
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
    public void deleteTask(int id) {
        tasks.remove(id);
    }

    // Удаление подзадачи по идентификатору
    public void deleteSubtasks(int id) {
        subtasks.remove(id);
    }

    // Удаление эпика по идентификатору
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
    public void deleteAllTasks() {
        tasks.clear();
    }

    // Удаление всех подзадач
    public void deleteAllSubtasks() {
        subtasks.clear();
        for (Epic epic : epics.values()) {
            epic.getSubtaskIds().clear();
        }
    }

    // Удаление всех эпиков
    public void deleteAllEpics() {
        epics.clear();
        deleteAllSubtasks();
    }

    // Получение всех задач
    public ArrayList<Task> getAllTasks() {
        return new ArrayList<>(tasks.values());
    }

    // Получение всех подзадач
    public ArrayList<Subtask> getAllSubtasks() {
        return new ArrayList<>(subtasks.values());
    }

    // Получение всех эпиков
    public ArrayList<Epic> getAllEpics() {
        return new ArrayList<>(epics.values());
    }

    // Получение всех подзадач определённого эпика
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