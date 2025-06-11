package taskmanager.manager;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import taskmanager.utiltask.*;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public abstract class TaskManagerTest<T extends TaskManager> {
    protected T manager;

    // Абстрактный метод для инициализации менеджера
    @BeforeEach
    public abstract void setUp();

    // Вспомогательные методы
    protected Task makeTask() {
        return new Task("Task", "Desc", Duration.ofMinutes(15), LocalDateTime.now());
    }
    protected Epic makeEpic() {
        return new Epic("Epic", "EpicDesc");
    }
    protected Subtask makeSubtask(int epicId) {
        return new Subtask("Sub", "SubDesc", epicId, Duration.ofMinutes(10), LocalDateTime.now());
    }

    // CRUD for Task
    @Test
    void createAndGetTask_shouldReturnSameTask() {
        Task task = makeTask();
        manager.createTask(task);
        assertEquals(task, manager.getTaskById(task.getId()));
    }

    @Test
    void updateTask_shouldUpdateFields() {
        Task task = makeTask();
        manager.createTask(task);
        task.setTitle("Updated");
        manager.updateTask(task);
        assertEquals("Updated", manager.getTaskById(task.getId()).getTitle());
    }

    @Test
    void deleteTask_shouldRemoveTask() {
        Task task = makeTask();
        manager.createTask(task);
        manager.deleteTask(task.getId());
        assertNull(manager.getTaskById(task.getId()));
    }

    // CRUD for Epic
    @Test
    void createAndGetEpic_shouldReturnSameEpic() {
        Epic epic = makeEpic();
        manager.createEpic(epic);
        assertEquals(epic, manager.getEpicById(epic.getId()));
    }

    @Test
    void updateEpic_shouldUpdateFields() {
        Epic epic = makeEpic();
        manager.createEpic(epic);
        epic.setTitle("UpdatedEpic");
        manager.updateEpic(epic);
        assertEquals("UpdatedEpic", manager.getEpicById(epic.getId()).getTitle());
    }

    @Test
    void deleteEpic_shouldRemoveEpicAndSubtasks() {
        Epic epic = makeEpic();
        manager.createEpic(epic);
        Subtask sub = makeSubtask(epic.getId());
        manager.createSubtask(sub);
        manager.deleteEpic(epic.getId());
        assertNull(manager.getEpicById(epic.getId()));
        assertNull(manager.getSubtaskById(sub.getId()));
        assertFalse(manager.getAllSubtasks().contains(sub));
    }

    // CRUD for Subtask
    @Test
    void createAndGetSubtask_shouldReturnSameSubtask() {
        Epic epic = makeEpic();
        manager.createEpic(epic);
        Subtask sub = makeSubtask(epic.getId());
        manager.createSubtask(sub);
        assertEquals(sub, manager.getSubtaskById(sub.getId()));
    }

    @Test
    void updateSubtask_shouldUpdateFields() {
        Epic epic = makeEpic();
        manager.createEpic(epic);
        Subtask sub = makeSubtask(epic.getId());
        manager.createSubtask(sub);
        sub.setTitle("UpdatedSub");
        manager.updateSubtask(sub);
        assertEquals("UpdatedSub", manager.getSubtaskById(sub.getId()).getTitle());
    }

    @Test
    void deleteSubtask_shouldRemoveItAndUpdateEpic() {
        Epic epic = makeEpic();
        manager.createEpic(epic);
        Subtask sub = makeSubtask(epic.getId());
        manager.createSubtask(sub);
        manager.deleteSubtasks(sub.getId());
        assertNull(manager.getSubtaskById(sub.getId()));
        assertFalse(manager.getEpicById(epic.getId()).getSubtaskIds().contains(sub.getId()));
    }

    // Проверка получения всех задач
    @Test
    void getAllTasks_shouldReturnAllCreatedTasks() {
        LocalDateTime base = LocalDateTime.now();
        Task t1 = new Task("Task1", "Desc1", Duration.ofMinutes(15), base);
        Task t2 = new Task("Task2", "Desc2", Duration.ofMinutes(15), base.plusMinutes(20));
        manager.createTask(t1);
        manager.createTask(t2);
        List<Task> tasks = manager.getAllTasks();
        assertTrue(tasks.contains(t1));
        assertTrue(tasks.contains(t2));
    }

    @Test
    void getAllEpics_shouldReturnAllCreatedEpics() {
        Epic e1 = makeEpic();
        Epic e2 = makeEpic();
        manager.createEpic(e1);
        manager.createEpic(e2);
        List<Epic> epics = manager.getAllEpics();
        assertTrue(epics.contains(e1));
        assertTrue(epics.contains(e2));
    }

    @Test
    void getAllSubtasks_shouldReturnAllCreatedSubtasks() {
        Epic epic = makeEpic();
        manager.createEpic(epic);
        LocalDateTime base = LocalDateTime.now();
        Subtask s1 = new Subtask("S1", "Desc1", epic.getId(), Duration.ofMinutes(10), base);
        Subtask s2 = new Subtask("S2", "Desc2", epic.getId(), Duration.ofMinutes(10), base.plusMinutes(15));
        manager.createSubtask(s1);
        manager.createSubtask(s2);
        List<Subtask> subs = manager.getAllSubtasks();
        assertTrue(subs.contains(s1));
        assertTrue(subs.contains(s2));
    }

    // Проверка получения подзадач по эпику
    @Test
    void getSubtasksByEpic_shouldReturnAllEpicSubtasks() {
        Epic epic = makeEpic();
        manager.createEpic(epic);
        Subtask s1 = new Subtask("S1", "Desc1", epic.getId(), Duration.ofMinutes(10), LocalDateTime.now());
        Subtask s2 = new Subtask("S2", "Desc2", epic.getId(), Duration.ofMinutes(10), LocalDateTime.now().plusMinutes(20));
        manager.createSubtask(s1);
        manager.createSubtask(s2);
        List<Subtask> subs = manager.getSubtasksByEpic(epic.getId());
        assertTrue(subs.contains(s1));
        assertTrue(subs.contains(s2));
    }

    // Граничные условия статуса эпика
    @Test
    void epicStatus_allSubtasksNew_shouldBeNew() {
        Epic epic = makeEpic();
        manager.createEpic(epic);
        manager.createSubtask(new Subtask("s1", "d1", epic.getId()));
        manager.createSubtask(new Subtask("s2", "d2", epic.getId()));
        assertEquals(Status.NEW, manager.getEpicById(epic.getId()).getStatus());
    }

    @Test
    void epicStatus_allSubtasksDone_shouldBeDone() {
        Epic epic = makeEpic();
        manager.createEpic(epic);
        Subtask s1 = new Subtask("s1", "d1", epic.getId());
        s1.setStatus(Status.DONE);
        Subtask s2 = new Subtask("s2", "d2", epic.getId());
        s2.setStatus(Status.DONE);
        manager.createSubtask(s1);
        manager.createSubtask(s2);
        assertEquals(Status.DONE, manager.getEpicById(epic.getId()).getStatus());
    }

    @Test
    void epicStatus_newAndDone_shouldBeInProgress() {
        Epic epic = makeEpic();
        manager.createEpic(epic);
        Subtask s1 = new Subtask("s1", "d1", epic.getId());
        s1.setStatus(Status.NEW);
        Subtask s2 = new Subtask("s2", "d2", epic.getId());
        s2.setStatus(Status.DONE);
        manager.createSubtask(s1);
        manager.createSubtask(s2);
        assertEquals(Status.IN_PROGRESS, manager.getEpicById(epic.getId()).getStatus());
    }

    @Test
    void epicStatus_subtasksInProgress_shouldBeInProgress() {
        Epic epic = makeEpic();
        manager.createEpic(epic);
        Subtask s1 = new Subtask("s1", "d1", epic.getId());
        s1.setStatus(Status.IN_PROGRESS);
        Subtask s2 = new Subtask("s2", "d2", epic.getId());
        s2.setStatus(Status.IN_PROGRESS);
        manager.createSubtask(s1);
        manager.createSubtask(s2);
        assertEquals(Status.IN_PROGRESS, manager.getEpicById(epic.getId()).getStatus());
    }

    // Проверка работы истории просмотров
    @Test
    void getHistory_shouldReturnCorrectHistory() {
        Task t = makeTask();
        manager.createTask(t);
        manager.getTaskById(t.getId());
        Epic e = makeEpic();
        manager.createEpic(e);
        manager.getEpicById(e.getId());
        assertEquals(2, manager.getHistory().size());
        assertTrue(manager.getHistory().contains(t));
        assertTrue(manager.getHistory().contains(e));
    }
}
