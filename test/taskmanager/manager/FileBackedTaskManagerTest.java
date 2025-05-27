package taskmanager.manager;

import taskmanager.utiltask.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.*;

public class FileBackedTaskManagerTest {
    @TempDir
    Path tempDir;

    FileBackedTaskManager manager;
    Path taskListFile;

    @BeforeEach
    void setUp() {
        taskListFile = tempDir.resolve("tasks.csv");
        manager = new FileBackedTaskManager(taskListFile);
    }

    // Метод для создания тестового файла с данными
    private void createTestFile(Path file) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(file.toFile()))) {
            writer.write("id,type,title,status,description,epic\n");
            writer.write("1,TASK,Задача 1,DONE,Описание задачи 1,\n");
            writer.write("2,EPIC,Эпик 1,IN_PROGRESS,Описание эпика 1,\n");
            writer.write("3,SUBTASK,Подзадача 1,IN_PROGRESS,Описание подзадачи 1,2\n");
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    void testSaveAndLoadEmptyFile() {
        // Сохранение пустого файла
        manager.save();

        // Загрузка из сохраненного файла
        FileBackedTaskManager loadedManager = FileBackedTaskManager.loadFromFile(taskListFile);

        // Проверка, что все коллекции пустые
        assertTrue(loadedManager.getAllTasks().isEmpty());
        assertTrue(loadedManager.getAllEpics().isEmpty());
        assertTrue(loadedManager.getAllSubtasks().isEmpty());
    }

    @Test
    void testSaveMultipleTasks() {
        // Создаем несколько задач разных типов
        Task task = new Task(1, "Задача 1", "Описание задачи 1", Status.DONE);
        Epic epic = new Epic(2, "Эпик 1", "Описание эпика 1");
        Subtask subtask = new Subtask(3, "Подзадача 1", "Описание подзадачи 1", Status.IN_PROGRESS, 2);

        // Добавляем задачи
        manager.createTask(task);
        manager.createEpic(epic);
        manager.createSubtask(subtask);

        // Проверяем, что задачи добавлены
        assertEquals(1, manager.getAllTasks().size());
        assertEquals(1, manager.getAllEpics().size());
        assertEquals(1, manager.getAllSubtasks().size());

        // Сохраняем изменения
        manager.save();
    }

    @Test
    void testLoadMultipleTasks() {
        Path testFile = tempDir.resolve("test_tasks.csv");
        createTestFile(testFile);

        // Загружаем данные из файла
        FileBackedTaskManager loadedManager = FileBackedTaskManager.loadFromFile(testFile);

        // Проверяем загруженные данные
        assertEquals(1, loadedManager.getAllTasks().size());
        assertEquals(1, loadedManager.getAllEpics().size());
        assertEquals(1, loadedManager.getAllSubtasks().size());

        // Проверяем корректность данных
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
}