public class Main {
    public static void main(String[] args) {
        TaskManager taskManager = new TaskManager();

        // Создание задач
        Task task1 = new Task("Изучить ФЗ№4", "Ознакомиться с техническим заданием ФЗ№4");
        Task task2 = new Task("Структура проекта к ФЗ№4", "Определить структуру проекта");

        taskManager.createTask(task1);
        taskManager.createTask(task2);

        // Создание эпиков
        Epic epic1 = new Epic("Формирование проекта к ФЗ№4", "Определить классы внутри проекта");
        Epic epic2 = new Epic("Наполнение классов к проекту ФЗ№4", "Определить и реализовать " +
                "функционал внутри классов проекта к ФЗ№4");

        taskManager.createEpic(epic1);
        taskManager.createEpic(epic2);

        // Создание подзадач для первого эпика
        Subtask subtask1 = new Subtask("Класс Task", "Реализовать методы внутри класса и переменные", epic1);
        Subtask subtask2 = new Subtask("Класс SubTask", "Реализовать методы внутри класса и переменные", epic1);
        Subtask subtask3 = new Subtask("Класс Epic", "Реализовать методы внутри класса и переменные", epic1);
        Subtask subtask4 = new Subtask("Класс TaskManager", "Реализовать методы внутри класса и переменные", epic1);
        // Используем метод createSubtask для добавления подзадач
        taskManager.createSubtask(subtask1);
        taskManager.createSubtask(subtask2);
        taskManager.createSubtask(subtask3);
        taskManager.createSubtask(subtask4);

        // Создание подзадачи для второго эпика
        Subtask subtask5 = new Subtask("Тестирование и Ревью", "Произвести тесты проекта по ФЗ№4," +
                " отправить на Ревью", epic2);

        // Используем метод createSubtask для добавления подзадачи
        taskManager.createSubtask(subtask5);

        // Печать задач, подзадач и эпиков
        System.out.println("Список задач:");
        System.out.println(taskManager.getTaskById(1));
        System.out.println(taskManager.getTaskById(2));

        System.out.println("\nСписок эпиков:");
        System.out.println(taskManager.getEpicById(3));
        System.out.println(taskManager.getEpicById(4));

        System.out.println("\nСписок подзадач для Эпика ID: " + epic1.getId());
        for (Subtask sub : epic1.getSubtasks().values()) {
            System.out.println(sub);
        }

        System.out.println("\nСписок подзадач для Эпика ID: " + epic2.getId());
        for (Subtask sub : epic2.getSubtasks().values()) {
            System.out.println(sub);
        }

        // Изменение статусов
        task1.setStatus(Status.IN_PROGRESS);
        subtask1.setStatus(Status.DONE);
        subtask2.setStatus(Status.DONE);
        subtask3.setStatus(Status.DONE);
        subtask4.setStatus(Status.DONE);

        // Обновляем статус эпика после изменения статусов подзадач
        taskManager.updateEpicStatus(epic1.getId());

        // Печать статусов после изменений
        System.out.println("\nТекущие статусы после изменений:");

        System.out.println("Задача ID: " + task1.getId() + taskManager.getTaskById(1).getStatus());

        for (Subtask sub : epic1.getSubtasks().values()) {
            System.out.println("Подзадача ID: " + sub.getId() + " " + sub.getTitle() + ", Статус: " + sub.getStatus());
        }

        // Проверка статуса эпика после изменения статусов подзадач
        System.out.println("Статус Эпика ID " + epic1.getId() + ": " + epic1.getStatus());

        // Удаление задачи и эпика
        taskManager.deleteTask(2); // Удаляем задачу 2
        taskManager.deleteEpic(4); // Удаляем Эпик 2

        // Печать оставшихся задач и эпиков после удаления
        System.out.println("\nСписок задач после удаления:");
        for (Task task : taskManager.getTasks().values()) {
            System.out.println(task);
        }

        System.out.println("\nСписок эпиков после удаления:");
        for (Epic epic : taskManager.getEpics().values()) {
            System.out.println(epic);
        }
    }
}