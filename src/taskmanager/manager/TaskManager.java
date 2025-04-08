package taskmanager.manager;

import taskmanager.utiltask.Task;
import taskmanager.utiltask.Subtask;
import taskmanager.utiltask.Epic;
import taskmanager.utiltask.Status;


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
        nextId = 1; // Общий счетчик для всех типов задач
    }

    // Получение следующего идентификатора
    private int getNextId() {
        while (tasks.containsKey(nextId) || subtasks.containsKey(nextId) || epics.containsKey(nextId)) {
            nextId++;
        }
        return nextId++;
    }

    // Получение задачи по идентификатору
    public Task getTaskById(int id) {
        return tasks.get(id);
    }

    public Subtask getSubtaskById(int id) {
        return subtasks.get(id);
    }

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
        subtask.getEpic().addSubtask(subtask); // Добавляем подзадачу к эпику
        updateEpicStatus(subtask.getEpic().getId());
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
            updateEpicStatus(subtask.getEpic().getId()); // Обновляем статус эпика после изменения подзадачи
        }
    }

    // Обновление эпика (Epic)
    public void updateEpic(Epic epic) {
        if (epics.containsKey(epic.getId())) {
            epics.put(epic.getId(), epic);
            updateEpicStatus(epic.getId()); // Обновляем статус эпика после изменения
        }
    }

    public void updateEpicStatus(int epicId) {
        Epic epic = epics.get(epicId);
        if (epic != null) {
            boolean hasSubtasks = !epic.getSubtasks().isEmpty();
            boolean allNew = true;
            boolean allDone = true;

            for (Subtask sub : epic.getSubtasks().values()) {
                Status status = sub.getStatus();
                if (status != Status.NEW) {
                    allNew = false; // Если есть хоть одна подзадача не NEW
                }
                if (status != Status.DONE) {
                    allDone = false; // Если есть хоть одна подзадача не DONE
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

    // Гетеры
    public HashMap<Integer, Epic> getEpics() {
        return epics;
    }

    public HashMap<Integer, Task> getTasks() {
        return tasks;
    }

    public HashMap<Integer, Subtask> getSubtasks() {
        return subtasks;
    }

    // Удаление задачи по идентификатору
    public void deleteTask(int id) {
        tasks.remove(id);
    }

    // Удаление эпика по идентификатору
    public void deleteEpic(int id) {
        Epic epic = epics.remove(id); // Удаляем эпик и получаем его
        if (epic != null) { // Проверяем, был ли найден эпик
            // Удаляем все подзадачи, связанные с этим эпиком
            for (Integer subtaskId : epic.getSubtasks().keySet()) {
                subtasks.remove(subtaskId); // Удаляем подзадачу по идентификатору
            }
        }
    }

    // Удаление всех задач
    public void deleteAllTasks() {
        tasks.clear();
    }

    // Удаление всех эпиков
    public void deleteAllEpics() {
        epics.clear();
        subtasks.clear();  // Удаляем все подзадачи при удалении всех эпиков.
    }

    // Получение всех задач
    public HashMap<Integer, Task> getAllTasks() {
        return tasks;
    }

    // Получение всех подзадач
    public HashMap<Integer, Subtask> getAllSubtasks() {
        return subtasks;
    }

    // Получение всех эпиков
    public HashMap<Integer, Epic> getAllEpics() {
        return epics;
    }

    // Получение всех подзадач определённого эпика
    public HashMap<Integer, Subtask> getSubtasksByEpic(int epicId) {
        Epic epic = getEpicById(epicId);
        HashMap<Integer, Subtask> result = new HashMap<>();

        if (epic != null) {
            result.putAll(epic.getSubtasks());
        }

        return result;
    }

}