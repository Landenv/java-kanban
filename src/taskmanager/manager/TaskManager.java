package taskmanager.manager;

import taskmanager.utiltask.Epic;
import taskmanager.utiltask.Subtask;
import taskmanager.utiltask.Task;

import java.util.List;

public interface TaskManager {

    // Получение задачи по идентификатору
    Task getTaskById(int id);

    // Получение подзадачи по идентификатору
    Subtask getSubtaskById(int id);

    // Получение эпика по идентификатору
    Epic getEpicById(int id);

    // Создание задачи
    void createTask(Task task);

    // Создание подзадачи
    void createSubtask(Subtask subtask);

    // Создание эпика
    void createEpic(Epic epic);

    // Обновление задачи (Task)
    void updateTask(Task task);

    // Обновление подзадачи (Subtask)
    void updateSubtask(Subtask subtask);

    // Обновление эпика (Epic)
    void updateEpic(Epic epic);

    // Удаление задачи по идентификатору
    void deleteTask(int id);

    // Удаление подзадачи по идентификатору
    void deleteSubtasks(int id);

    // Удаление эпика по идентификатору
    void deleteEpic(int id);

    // Удаление всех задач
    void deleteAllTasks();

    // Удаление всех подзадач
    void deleteAllSubtasks();

    // Удаление всех эпиков
    void deleteAllEpics();

    // Получение всех задач
    List<Task> getAllTasks();

    // Получение всех подзадач
    List<Subtask> getAllSubtasks();

    // Получение всех эпиков
    List<Epic> getAllEpics();

    // Получение всех подзадач определённого эпика
    List<Subtask> getSubtasksByEpic(int epicId);

    // Получение истории просмотров
    List<Task> getHistory();

    List<Task> getPrioritizedTasks();

    boolean hasIntersection(Task task);
}
