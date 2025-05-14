package taskmanager.app;

import taskmanager.manager.Manager;
import taskmanager.manager.TaskManager;
import taskmanager.utiltask.Epic;
import taskmanager.utiltask.Subtask;
import taskmanager.utiltask.Task;

public class Main {
    public static void main(String[] args) {
        TaskManager taskManager = Manager.getDefault();

        // Создаем две задачи
        Task task1 = new Task("Задача 1", "Описание задачи 1");
        Task task2 = new Task("Задача 2", "Описание задачи 2");
        taskManager.createTask(task1);
        taskManager.createTask(task2);

        // Создаем эпик с тремя подзадачами
        Epic epicWithSubtasks = new Epic("Эпик с подзадачами", "Описание эпика с подзадачами");
        taskManager.createEpic(epicWithSubtasks);
        Subtask subtask1 = new Subtask("Подзадача 1", "Описание подзадачи 1", epicWithSubtasks.getId());
        Subtask subtask2 = new Subtask("Подзадача 2", "Описание подзадачи 2", epicWithSubtasks.getId());
        Subtask subtask3 = new Subtask("Подзадача 3", "Описание подзадачи 3", epicWithSubtasks.getId());
        taskManager.createSubtask(subtask1);
        taskManager.createSubtask(subtask2);
        taskManager.createSubtask(subtask3);

        // Создаем эпик без подзадач
        Epic epicWithoutSubtasks = new Epic("Эпик без подзадач", "Описание эпика без подзадач");
        taskManager.createEpic(epicWithoutSubtasks);

        // Запрашиваем созданные задачи в разном порядке
        taskManager.getTaskById(task1.getId());
        taskManager.getEpicById(epicWithSubtasks.getId());
        taskManager.getSubtaskById(subtask1.getId());
        taskManager.getTaskById(task2.getId());
        taskManager.getSubtaskById(subtask2.getId());
        taskManager.getSubtaskById(subtask3.getId());
        taskManager.getEpicById(epicWithoutSubtasks.getId());

        // Выводим историю
        System.out.println("История просмотров:");
        taskManager.getHistory().forEach(System.out::println);

        // Удаляем задачу, которая есть в истории
        taskManager.deleteTask(task1.getId());
        System.out.println("\nПосле удаления задачи 1:");
        taskManager.getHistory().forEach(System.out::println);

        // Удаляем эпик с тремя подзадачами
        taskManager.deleteEpic(epicWithSubtasks.getId());
        System.out.println("\nПосле удаления эпика с подзадачами:");
        taskManager.getHistory().forEach(System.out::println);
    }
}
