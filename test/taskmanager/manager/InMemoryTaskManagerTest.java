package taskmanager.manager;

import taskmanager.utiltask.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class InMemoryTaskManagerTest {

    private TaskManager taskManager;
    private Task task1;
    private Task task2;
    private Epic epic1;
    private Epic epic2;

    @BeforeEach
    void setUp() {
        taskManager = Manager.getDefault();
        task1 = new Task("Задача 1", "Описание задачи 1");
        task2 = new Task("Задача 2", "Описание задачи 2");
        epic1 = new Epic("Эпик 1", "Описание эпика 1");
        epic2 = new Epic("Эпик 2", "Описание эпика 2");
    }

    // Проверка добавления задачи
    @Test
    void addTask() {
        taskManager.createTask(task1);
        assertEquals(1, taskManager.getAllTasks().size());
        assertTrue(taskManager.getAllTasks().contains(task1));
    }

    // Проверка обновления задачи
    @Test
    void updateTask() {
        taskManager.createTask(task1);
        Task updatedTask = new Task("Обновленная задача 1", "Обновленное описание");
        updatedTask.setId(task1.getId());
        taskManager.updateTask(updatedTask);
        assertEquals(updatedTask, taskManager.getTaskById(task1.getId()));
    }

    // Проверка получения задачи
    @Test
    void getTask() {
        taskManager.createTask(task1);
        Task retrievedTask = taskManager.getTaskById(task1.getId());
        assertEquals(task1, retrievedTask);
    }

    // Проверка получения несуществующей задачи
    @Test
    void getTask_nonExistentId() {
        assertNull(taskManager.getTaskById(999));
    }

    // Проверка получения всех задач
    @Test
    void getTasks() {
        taskManager.createTask(task1);
        taskManager.createTask(task2);
        assertEquals(2, taskManager.getAllTasks().size());
    }

    // Проверка удаления задачи
    @Test
    void deleteTask() {
        taskManager.createTask(task1);
        taskManager.deleteTask(task1.getId());
        assertEquals(0, taskManager.getAllTasks().size());
    }

    // Проверка удаления несуществующей задачи
    @Test
    void deleteTask_nonExistentId() {
        taskManager.deleteTask(999);
        assertEquals(0, taskManager.getAllTasks().size());
    }

    // Проверка удаления всех задач
    @Test
    void deleteAllTasks() {
        taskManager.createTask(task1);
        taskManager.createTask(task2);
        taskManager.deleteAllTasks();
        assertEquals(0, taskManager.getAllTasks().size());
    }

    // Проверка добавления эпика
    @Test
    void addEpic() {
        taskManager.createEpic(epic1);
        assertEquals(1, taskManager.getAllEpics().size());
        assertTrue(taskManager.getAllEpics().contains(epic1));
    }

    // Проверка обновления эпика
    @Test
    void updateEpic() {
        taskManager.createEpic(epic1);
        Epic updatedEpic = new Epic("Обновленный эпик 1", "Обновленное описание");
        updatedEpic.setId(epic1.getId());
        taskManager.updateEpic(updatedEpic);
        assertEquals(updatedEpic, taskManager.getEpicById(epic1.getId()));
    }

    // Проверка получения эпика
    @Test
    void getEpic() {
        taskManager.createEpic(epic1);
        Epic retrievedEpic = taskManager.getEpicById(epic1.getId());
        assertEquals(epic1, retrievedEpic);
    }

    // Проверка получения несуществующего эпика
    @Test
    void getEpic_nonExistentId() {
        assertNull(taskManager.getEpicById(999));
    }

    // Проверка получения всех эпиков
    @Test
    void getEpics() {
        taskManager.createEpic(epic1);
        taskManager.createEpic(epic2);
        assertEquals(2, taskManager.getAllEpics().size());
    }

    // Проверка удаления эпика
    @Test
    void deleteEpic() {
        taskManager.createEpic(epic1);
        Subtask subtask1 = new Subtask("Подзадача 1", "Описание 1", epic1.getId());
        taskManager.createSubtask(subtask1);
        Subtask subtask2 = new Subtask("Подзадача 2", "Описание 2", epic1.getId());
        taskManager.createSubtask(subtask2);
        assertEquals(2, taskManager.getAllSubtasks().size());
        taskManager.deleteEpic(epic1.getId());
        assertEquals(0, taskManager.getAllEpics().size());
        assertEquals(0, taskManager.getAllSubtasks().size());
    }

    // Проверка удаления несуществующего эпика
    @Test
    void deleteEpic_nonExistentId() {
        taskManager.deleteEpic(999);
        assertEquals(0, taskManager.getAllEpics().size());
    }

    // Проверка удаления всех эпиков
    @Test
    void deleteAllEpics() {
        taskManager.createEpic(epic1);
        // Создаем подзадачи, не ссылаясь на Эпик, чтобы избежать исключения
        Subtask subtask1 = new Subtask("Подзадача 1", "Описание 1", epic1.getId());
        taskManager.createSubtask(subtask1);
        Subtask subtask2 = new Subtask("Подзадача 2", "Описание 2", epic1.getId());
        taskManager.createSubtask(subtask2);
        taskManager.createEpic(epic2); // Создаем второй эпик, не ссылаясь на первый
        taskManager.deleteAllEpics();
        assertEquals(0, taskManager.getAllEpics().size());
        assertEquals(0, taskManager.getAllSubtasks().size());
    }

    // Проверка добавления подзадачи
    @Test
    void addSubtask() {
        taskManager.createEpic(epic1);
        Subtask subtask1 = new Subtask("Подзадача 1", "Описание 1", epic1.getId()); // Создаем подзадачу с правильной ссылкой на эпик
        taskManager.createSubtask(subtask1);
        assertEquals(1, taskManager.getAllSubtasks().size());
        assertTrue(taskManager.getAllSubtasks().contains(subtask1));
    }

    // Проверка обновления подзадачи
    @Test
    void updateSubtask() {
        taskManager.createEpic(epic1);
        Subtask subtask1 = new Subtask("Подзадача 1", "Описание 1", epic1.getId());
        taskManager.createSubtask(subtask1);
        // Создаем обновленную подзадачу
        Subtask updatedSubtask = new Subtask("Обновленная подзадача 1", "Обновленное описание", epic1.getId());
        updatedSubtask.setId(subtask1.getId()); // Устанавливаем ID обновленной подзадачи
        // Обновляем подзадачу
        taskManager.updateSubtask(updatedSubtask);
        // Проверяем, что подзадача обновилась
        assertEquals(updatedSubtask, taskManager.getSubtaskById(subtask1.getId()));
    }

    // Проверка получения подзадачи
    @Test
    void getSubtask() {
        taskManager.createEpic(epic1);
        Subtask subtask = new Subtask("Подзадача 1", "Описание 1", epic1.getId());
        taskManager.createSubtask(subtask);
        Subtask retrievedSubtask = taskManager.getSubtaskById(subtask.getId());
        assertEquals(subtask, retrievedSubtask);
    }

    // Проверка получения несуществующей подзадачи
    @Test
    void getSubtask_nonExistentId() {
        assertNull(taskManager.getSubtaskById(999));
    }

    // Проверка получения всех подзадач
    @Test
    void getAllSubtasks() {
        taskManager.createEpic(epic1);
        Subtask subtask1 = new Subtask("Подзадача 1", "Описание 1", epic1.getId());
        taskManager.createSubtask(subtask1);
        Subtask subtask2 = new Subtask("Подзадача 2", "Описание 2", epic1.getId());
        taskManager.createSubtask(subtask2);
        assertEquals(2, taskManager.getAllSubtasks().size());
    }

    // Проверка удаления подзадачи
    @Test
    void deleteSubtask() {
        taskManager.createEpic(epic1);
        Subtask subtask = new Subtask("Подзадача 1", "Описание 1", epic1.getId());
        taskManager.createSubtask(subtask);
        assertEquals(1, taskManager.getAllSubtasks().size()); // Проверяем, что подзадача создана
        taskManager.deleteSubtasks(subtask.getId());
        assertEquals(0, taskManager.getAllSubtasks().size());
    }

    // Проверка удаления несуществующей подзадачи
    @Test
    void deleteSubtask_nonExistentId() {
        taskManager.deleteSubtasks(999);
        assertEquals(0, taskManager.getAllSubtasks().size());
    }

    // Проверка удаления всех подзадач
    @Test
    void deleteAllSubtask() {
        taskManager.createEpic(epic1);
        taskManager.createSubtask(new Subtask("Подзадача 1", "Описание 1", epic1.getId()));
        taskManager.createSubtask(new Subtask("Подзадача 2", "Описание 2", epic1.getId()));
        taskManager.deleteAllSubtasks();
        assertEquals(0, taskManager.getAllSubtasks().size());
    }

    // Проверка истории просмотров
    @Test
    void history() {
        taskManager.createTask(task1);
        taskManager.getTaskById(task1.getId());
        taskManager.createEpic(epic1);
        taskManager.getEpicById(epic1.getId());
        Subtask subtask = new Subtask("Подзадача 1", "Описание 1", epic1.getId());
        taskManager.createSubtask(subtask);
        taskManager.getSubtaskById(subtask.getId());
        assertEquals(3, taskManager.getHistory().size());
    }

    // Проверка статуса эпика без подзадач
    @Test
    void calcEpicStatus_noSubtasks() {
        taskManager.createEpic(epic1);
        assertEquals(Status.NEW, taskManager.getEpicById(epic1.getId()).getStatus());
    }

    // Проверка статуса эпика, если все подзадачи новые
    @Test
    void calcEpicStatus_allSubtasksNew() {
        Epic epic = new Epic("Эпик 1", "Описание эпика 1");
        taskManager.createEpic(epic);
        Subtask subtask1 = new Subtask("Подзадача 1", "Описание 1", epic.getId());
        taskManager.createSubtask(subtask1);
        Subtask subtask2 = new Subtask("Подзадача 2", "Описание 2", epic.getId());
        taskManager.createSubtask(subtask2);
        assertEquals(Status.NEW, taskManager.getEpicById(epic.getId()).getStatus());
    }

    // Проверка статуса эпика, если все подзадачи выполнены
    @Test
    void calcEpicStatus_allSubtasksDone() {
        taskManager.createEpic(epic1);
        Subtask subtask1 = new Subtask("Подзадача 1", "Описание 1", epic1.getId());
        taskManager.createSubtask(subtask1);
        subtask1.setStatus(Status.DONE);
        Subtask subtask2 = new Subtask("Подзадача 2", "Описание 2", epic1.getId());
        taskManager.createSubtask(subtask2);
        subtask2.setStatus(Status.DONE);
        taskManager.updateSubtask(subtask1);
        taskManager.updateSubtask(subtask2);
        assertEquals(Status.DONE, taskManager.getEpicById(epic1.getId()).getStatus());
    }

    // Проверка статуса эпика, если некоторые подзадачи в процессе выполнения
    @Test
    void calcEpicStatus_someSubtasksInProgress() {
        taskManager.createEpic(epic1);
        Subtask subtask1 = new Subtask("Подзадача 1", "Описание 1", epic1.getId());
        taskManager.createSubtask(subtask1);
        Subtask subtask2 = new Subtask("Подзадача 2", "Описание 2", epic1.getId());
        taskManager.createSubtask(subtask2);
        subtask1.setStatus(Status.IN_PROGRESS);
        taskManager.updateSubtask(subtask1);
        assertEquals(Status.IN_PROGRESS, taskManager.getEpicById(epic1.getId()).getStatus());
    }

    // Проверка статуса эпика, если подзадачи имеют разные статусы
    @Test
    void calcEpicStatus_someSubtasksNewAndDone() {
        taskManager.createEpic(epic1);
        Subtask subtask1 = new Subtask("Подзадача 1", "Описание 1", epic1.getId());
        taskManager.createSubtask(subtask1);
        Subtask subtask2 = new Subtask("Подзадача 2", "Описание 2", epic1.getId());
        subtask2.setStatus(Status.DONE);
        taskManager.createSubtask(subtask2);
        taskManager.updateEpic(epic1);
        assertEquals(Status.IN_PROGRESS, taskManager.getEpicById(epic1.getId()).getStatus());
    }

    // Проверка, что объект Epic нельзя добавить в самого себя в виде подзадачи
    @Test
    void epicCannotBeAddedAsSubtaskToItself() {
        Epic epic = new Epic("Эпик 1", "Описание эпика 1");
        taskManager.createEpic(epic);
        // Создаем подзадачу с ID, равным ID эпика
        Subtask subtask = new Subtask("Подзадача 1", "Описание 1", epic.getId());
        subtask.setId(epic.getId()); // Устанавливаем ID подзадачи равным ID эпика
        // Проверяем, что при попытке добавить подзадачу с тем же ID, что и у эпика, выбрасывается исключение
        Exception exception = assertThrows(IllegalArgumentException.class, () -> taskManager.createSubtask(subtask));
        String expectedMessage = "Эпик не может ссылаться на самого себя как подзадача.";
        String actualMessage = exception.getMessage();
        assertTrue(actualMessage.contains(expectedMessage));
    }

    // Проверка, что объект Subtask нельзя сделать своим же эпиком
    @Test
    void subtaskCannotBeItsOwnEpic() {
        Epic epic = new Epic("Эпик 1", "Описание 1");
        epic.setId(1); // Устанавливаем ID эпика
        taskManager.createEpic(epic); // Сначала создаем эпик
        Subtask subtask = new Subtask("Подзадача 1", "Описание 1", epic.getId());
        subtask.setId(epic.getId()); // Устанавливаем ID подзадачи равным ID эпика
        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            taskManager.createSubtask(subtask); // Проверяем, что подзадача не может ссылаться на самого себя как эпик
        });
        assertTrue(exception.getMessage().contains("Эпик не может ссылаться на самого себя как подзадача."));
    }

    // Проверка, что InMemoryTaskManager действительно добавляет задачи разного типа и может найти их по id
    @Test
    void inMemoryTaskManagerAddsAndFindsTasks() {
        Task task = new Task("Задача 1", "Описание 1");
        taskManager.createTask(task);
        Epic epic = new Epic("Эпик 1", "Описание 1");
        taskManager.createEpic(epic);
        Subtask subtask = new Subtask("Подзадача 1", "Описание 1", epic.getId());
        taskManager.createSubtask(subtask);

        assertEquals(task, taskManager.getTaskById(task.getId()));
        assertEquals(epic, taskManager.getEpicById(epic.getId()));
        assertEquals(subtask, taskManager.getSubtaskById(subtask.getId()));
    }

    // Проверка, что задачи с заданным id и сгенерированным id не конфликтуют внутри менеджера
    @Test
    void tasksWithSameIdDoNotConflict() {
        Task task1 = new Task("Задача 1", "Описание 1");
        task1.setId(1);
        taskManager.createTask(task1);

        Task task2 = new Task("Задача 2", "Описание 2");
        task2.setId(1); // Конфликт ID
        taskManager.createTask(task2); // Должно пройти без ошибок

        assertEquals(task1, taskManager.getTaskById(1));
    }

    // Проверка неизменности задачи (по всем полям) при добавлении задачи в менеджер
    @Test
    void taskImmutabilityOnAdd() {
        Task task = new Task("Задача 1", "Описание 1");
        taskManager.createTask(task);
        int originalId = task.getId();
        String originalTitle = task.getTitle();
        String originalDescription = task.getDescription();
        // Создаем новую задачу с измененными полями
        Task newTask = new Task("Новое Название", "Новое Описание");
        newTask.setId(originalId); // Устанавливаем ID оригинальной задачи
        taskManager.createTask(newTask); // Добавляем новую задачу в менеджер
        // Проверяем, что оригинальная задача осталась неизменной
        Task retrievedTask = taskManager.getTaskById(originalId);
        assertEquals(originalTitle, retrievedTask.getTitle());
        assertEquals(originalDescription, retrievedTask.getDescription());
    }
}

