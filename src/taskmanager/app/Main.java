package taskmanager.app;

import taskmanager.manager.TaskManager;
import taskmanager.manager.InMemoryTaskManager;
import taskmanager.utiltask.Epic;
import taskmanager.utiltask.Subtask;
import taskmanager.utiltask.Task;
import taskmanager.utiltask.Status;

public class Main {
    public static void main(String[] args) {
        // Создаем менеджер задач
        TaskManager taskManager = new InMemoryTaskManager();

        // Создаем задачи
        Task task1 = new Task("Задача 1", "Описание задачи 1");
        Task task2 = new Task("Задача 2", "Описание задачи 2");
        taskManager.createTask(task1);
        taskManager.createTask(task2);

        // Просматриваем задачи
        taskManager.getTaskById(task1.getId());
        taskManager.getTaskById(task2.getId());

        // Печатаем историю просмотров
        printHistory(taskManager);

        // Создаем эпики
        Epic epic1 = new Epic("Эпик 1", "Описание эпика 1");
        Epic epic2 = new Epic("Эпик 2", "Описание эпика 2");
        taskManager.createEpic(epic1);
        taskManager.createEpic(epic2);

        // Просматриваем эпики
        taskManager.getEpicById(epic1.getId());
        taskManager.getEpicById(epic2.getId());

        // Печатаем историю просмотров
        printHistory(taskManager);

        // Создаем подзадачи
        Subtask subtask1 = new Subtask("Подзадача 1", "Описание подзадачи 1", epic1.getId());
        Subtask subtask2 = new Subtask("Подзадача 2", "Описание подзадачи 2", epic1.getId());
        Subtask subtask3 = new Subtask("Подзадача 3", "Описание подзадачи 3", epic2.getId());
        Subtask subtask4 = new Subtask("Подзадача 4", "Описание подзадачи 4", epic2.getId());
        taskManager.createSubtask(subtask1);
        taskManager.createSubtask(subtask2);
        taskManager.createSubtask(subtask3);
        taskManager.createSubtask(subtask4);

        // Просматриваем подзадачи
        taskManager.getSubtaskById(subtask1.getId());
        taskManager.getSubtaskById(subtask2.getId());
        taskManager.getSubtaskById(subtask3.getId());
        taskManager.getSubtaskById(subtask4.getId());

        // Печатаем историю просмотров
        printHistory(taskManager);

        // Изменяем статус первой задачи и подзадачи
        task1.setStatus(Status.IN_PROGRESS);
        taskManager.updateTask(task1);
        subtask1.setStatus(Status.DONE);
        taskManager.updateSubtask(subtask1);

        // Просматриваем обновленную задачу и подзадачу
        taskManager.getTaskById(task1.getId());
        taskManager.getSubtaskById(subtask1.getId());

        // Печатаем историю просмотров
        printHistory(taskManager);
    }

    private static void printHistory(TaskManager manager) {
        System.out.println("===================================");
        System.out.println("История просмотров:");
        for (Task task : manager.getHistory()) {
            System.out.println(task);
        }
        System.out.println("===================================");
    }
}
