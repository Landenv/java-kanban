package taskmanager.manager;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import taskmanager.utiltask.*;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class InMemoryTaskManagerTest extends TaskManagerTest<InMemoryTaskManager> {

    @BeforeEach
    public void setUp() {
        manager = new InMemoryTaskManager();
    }

    // все стандартные тесты вынесены в TaskManagerTest
    @Test
    void epicCannotBeAddedAsSubtaskToItself() {
        Epic epic = new Epic("Эпик 1", "Описание эпика 1");
        manager.createEpic(epic);
        Subtask subtask = new Subtask("Подзадача 1", "Описание 1", epic.getId());
        subtask.setId(epic.getId());
        Exception exception = assertThrows(IllegalArgumentException.class, () -> manager.createSubtask(subtask));
        assertTrue(exception.getMessage().contains("Эпик не может ссылаться на самого себя как подзадача."));
    }

    @Test
    void subtaskCannotBeItsOwnEpic() {
        Epic epic = new Epic("Эпик 1", "Описание 1");
        epic.setId(1);
        manager.createEpic(epic);
        Subtask subtask = new Subtask("Подзадача 1", "Описание 1", epic.getId());
        subtask.setId(epic.getId());
        Exception exception = assertThrows(IllegalArgumentException.class, () -> manager.createSubtask(subtask));
        assertTrue(exception.getMessage().contains("Эпик не может ссылаться на самого себя как подзадача."));
    }

    @Test
    void prioritizedTasks_shouldReturnTasksInChronologicalOrder() {
        LocalDateTime now = LocalDateTime.of(2024, 6, 11, 13, 0);

        Task t1 = new Task("task1", "desc1", Duration.ofMinutes(60), now.plusHours(3)); // позже всех
        Task t2 = new Task("task2", "desc2", Duration.ofMinutes(30), now.plusHours(1)); // раньше всех
        Task t3 = new Task("task3", "desc3", Duration.ofMinutes(45), now.plusHours(2)); // середина

        manager.createTask(t1);
        manager.createTask(t2);
        manager.createTask(t3);

        List<Task> ordered = ((InMemoryTaskManager) manager).getPrioritizedTasks();
        assertEquals(t2, ordered.get(0));
        assertEquals(t3, ordered.get(1));
        assertEquals(t1, ordered.get(2));
    }
}
