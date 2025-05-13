package taskmanager.manager;

import taskmanager.utiltask.Task;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import java.util.List;

public class InMemoryHistoryManagerTest {

    private HistoryManager historyManager;
    private TaskManager taskManager;
    private Task task1;
    private Task task2;
    private Task task3;

    @BeforeEach
    void setUp() {
        historyManager = Manager.getDefaultHistory();
        taskManager = Manager.getDefault();
        task1 = new Task("Task 1", "Description 1");
        task2 = new Task("Task 2", "Description 2");
        task3 = new Task("Task 3", "Description 3");

        taskManager.createTask(task1);
        taskManager.createTask(task2);
        taskManager.createTask(task3);
    }
    // Проверка, что задача добавляется в историю
    @Test
    void add_shouldAddTaskToHistory() {
        historyManager.add(task1);
        List<Task> history = historyManager.getHistory();
        assertEquals(1, history.size());
        assertEquals(task1, history.getFirst());
    }
    // Проверка, что null задача игнорируется
    @Test
    void add_shouldIgnoreNullTask() {
        historyManager.add(null);
        List<Task> history = historyManager.getHistory();
        assertEquals(0, history.size());
    }
    // Проверка, что история пуста, когда ничего не добавлено
    @Test
    void getHistory_shouldReturnEmptyListWhenHistoryIsEmpty() {
        List<Task> history = historyManager.getHistory();
        assertTrue(history.isEmpty());
    }
    // Проверка, что история возвращает задачи в правильном порядке
    @Test
    void getHistory_shouldReturnTasksInCorrectOrder() {
        historyManager.add(task1);
        historyManager.add(task2);
        historyManager.add(task3);
        List<Task> history = historyManager.getHistory();
        assertEquals(3, history.size());
        assertEquals(task1, history.get(0));
        assertEquals(task2, history.get(1));
        assertEquals(task3, history.get(2));
    }

    // Проверка, что задачи, добавляемые в HistoryManager, сохраняют предыдущую версию задачи и её данных.
    @Test
    void historyManagerStoresPreviousTaskVersion() {
        // Создаем менеджер задач
        TaskManager taskManager = Manager.getDefault();
        // Создаем задачу
        Task task = new Task("Task 1", "Description 1");
        taskManager.createTask(task);
        taskManager.getTaskById(task.getId());
        // Изменяем задачу
        Task updatedTask = new Task("Updated Task", "Updated Description");
        updatedTask.setId(task.getId());
        taskManager.updateTask(updatedTask);
        // Проверяем, что история содержит оригинальную задачу
        List<Task> history = taskManager.getHistory();
        assertEquals(1, history.size());
        assertEquals("Task 1", history.getFirst().getTitle());
    }

    // Проверка, что при повторном добавлении одной и той же задачи в историю она обновляет её положение.
    @Test
    void add_shouldUpdateTaskInHistory() {
        historyManager.add(task1);
        historyManager.add(task1); // Добавляем повторно
        List<Task> history = historyManager.getHistory();
        assertEquals(1, history.size());
        assertEquals(task1, history.get(0));
    }

    // Проверка, что удаление задачи из менеджера также удаляет её из истории.
    @Test
    void remove_shouldRemoveTaskFromHistory() {
        taskManager.createTask(task1);
        taskManager.getTaskById(task1.getId());
        taskManager.deleteTask(task1.getId());
        List<Task> history = historyManager.getHistory();
        assertTrue(history.isEmpty());
    }

}