package taskmanager.manager;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import taskmanager.utiltask.*;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Duration;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

public class FileBackedTaskManagerTest extends TaskManagerTest<FileBackedTaskManager> {
    @TempDir
    Path tempDir;

    Path taskListFile;

    @BeforeEach
    public void setUp() {
        taskListFile = tempDir.resolve("tasks.csv");
        manager = new FileBackedTaskManager(taskListFile);
    }

    // Все стандартные тесты вынесены в TaskManagerTest
    private void createTestFile(Path file) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(file.toFile()))) {
            writer.write("id,type,title,status,description,epic,duration,startTime,endTime\n");
            writer.write("1,TASK,Задача 1,DONE,Описание задачи 1,,,,\n");
            writer.write("2,EPIC,Эпик 1,IN_PROGRESS,Описание эпика 1,,,,\n");
            writer.write("3,SUBTASK,Подзадача 1,IN_PROGRESS,Описание подзадачи 1,2,,,\n");
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    void testSaveAndLoadEmptyFile() {
        manager.save();
        FileBackedTaskManager loadedManager = FileBackedTaskManager.loadFromFile(taskListFile);
        assertTrue(loadedManager.getAllTasks().isEmpty());
        assertTrue(loadedManager.getAllEpics().isEmpty());
        assertTrue(loadedManager.getAllSubtasks().isEmpty());
    }

    @Test
    void testSaveMultipleTasks() {
        Task task = new Task(1, "Задача 1", "Описание задачи 1", Status.DONE);
        Epic epic = new Epic(2, "Эпик 1", "Описание эпика 1");
        Subtask subtask = new Subtask(3, "Подзадача 1", "Описание подзадачи 1", Status.IN_PROGRESS, 2, null, null);
        manager.createTask(task);
        manager.createEpic(epic);
        manager.createSubtask(subtask);
        assertEquals(1, manager.getAllTasks().size());
        assertEquals(1, manager.getAllEpics().size());
        assertEquals(1, manager.getAllSubtasks().size());
        manager.save();
    }

    @Test
    void testLoadMultipleTasks() {
        Path testFile = tempDir.resolve("test_tasks.csv");
        createTestFile(testFile);
        FileBackedTaskManager loadedManager = FileBackedTaskManager.loadFromFile(testFile);
        assertEquals(1, loadedManager.getAllTasks().size());
        assertEquals(1, loadedManager.getAllEpics().size());
        assertEquals(1, loadedManager.getAllSubtasks().size());
        Task loadedTask = loadedManager.getAllTasks().getFirst();
        Epic loadedEpic = loadedManager.getAllEpics().getFirst();
        Subtask loadedSubtask = loadedManager.getAllSubtasks().getFirst();
        assertEquals(1, loadedTask.getId());
        assertEquals("Задача 1", loadedTask.getTitle());
        assertEquals(Status.DONE, loadedTask.getStatus());
        assertEquals("Описание задачи 1", loadedTask.getDescription());
        assertEquals(2, loadedEpic.getId());
        assertEquals("Эпик 1", loadedEpic.getTitle());
        assertEquals(Status.IN_PROGRESS, loadedEpic.getStatus());
        assertEquals("Описание эпика 1", loadedEpic.getDescription());
        assertEquals(3, loadedSubtask.getId());
        assertEquals("Подзадача 1", loadedSubtask.getTitle());
        assertEquals(Status.IN_PROGRESS, loadedSubtask.getStatus());
        assertEquals("Описание подзадачи 1", loadedSubtask.getDescription());
        assertEquals(2, loadedSubtask.getEpicID());
    }

    @Test
    void createTask_withOverlappingTime_shouldThrowException() {
        LocalDateTime now = LocalDateTime.of(2024, 6, 1, 9, 0);
        Task t1 = new Task("Task1", "desc", Duration.ofMinutes(90), now);
        Task t2 = new Task("Task2", "desc", Duration.ofMinutes(60), now.plusMinutes(30)); // overlap
        manager.createTask(t1);
        assertThrows(IllegalArgumentException.class, () -> manager.createTask(t2));
    }

    @Test
    void updateTask_withOverlappingTime_shouldThrowException() {
        LocalDateTime now = LocalDateTime.of(2024, 6, 1, 11, 0);
        Task t1 = new Task("Task1", "desc", Duration.ofMinutes(60), now);
        Task t2 = new Task("Task2", "desc", Duration.ofMinutes(60), now.plusMinutes(120)); // NO overlap
        manager.createTask(t1);
        manager.createTask(t2);
        Task t2Updated = new Task("Task2", "desc", Duration.ofMinutes(60), now.plusMinutes(30));
        t2Updated.setId(t2.getId());
        assertThrows(IllegalArgumentException.class, () -> manager.updateTask(t2Updated));
    }

    // Проверка выброса исключений (файлы)
    @Test
    void loadFromFile_withInvalidHeader_shouldThrowManagerSaveException() throws Exception {
        Path file = tempDir.resolve("invalid_header.csv");
        Files.writeString(file, "invalid,header,strange,fields\n1,TASK,Task,NEW,Desc,null,60,2024-06-08T10:00,null\n");
        assertThrows(ManagerSaveException.class, () -> FileBackedTaskManager.loadFromFile(file));
    }

    @Test
    void loadFromFile_withBrokenLine_shouldThrowManagerSaveException() throws Exception {
        Path file = tempDir.resolve("invalid_line.csv");
        Files.writeString(file, "id,type,title,status,description,epic,duration,startTime,endTime\n1,TASK,Task,NEW,Desc\n");
        assertThrows(ManagerSaveException.class, () -> FileBackedTaskManager.loadFromFile(file));
    }

    @Test
    void save_and_load_validFile_doesNotThrow() {
        assertDoesNotThrow(() -> {
            manager.createTask(new Task("Task1", "Desc1"));
            manager.save();
            FileBackedTaskManager.loadFromFile(taskListFile);
        });
    }
}
