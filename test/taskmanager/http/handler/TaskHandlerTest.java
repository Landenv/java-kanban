package taskmanager.http.handler;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import taskmanager.manager.InMemoryTaskManager;
import taskmanager.http.HttpTaskServer;
import taskmanager.utiltask.Task;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URL;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class TaskHandlerTest {
    private HttpTaskServer httpTaskServer;
    private InMemoryTaskManager taskManager;

    @BeforeEach
    public void setUp() throws IOException {
        taskManager = new InMemoryTaskManager();
        httpTaskServer = new HttpTaskServer(taskManager);
        httpTaskServer.start();
    }

    @AfterEach
    public void tearDown() {
        httpTaskServer.stop();
    }

    private HttpURLConnection createConnection(String endpoint, String method) throws IOException {
        URI uri = URI.create("http://localhost:8080" + endpoint);
        URL url = uri.toURL();
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod(method);
        return connection;
    }

    @Test
    public void testCreateTask() throws IOException {
        String jsonInputString = "{\"title\":\"Test Task\", \"description\":\"Test Description\"}";
        HttpURLConnection connection = createConnection("/tasks", "POST");
        connection.setDoOutput(true);
        connection.getOutputStream().write(jsonInputString.getBytes());

        assertEquals(200, connection.getResponseCode());

        assertEquals(1, taskManager.getAllTasks().size());
        assertEquals("Test Task", taskManager.getAllTasks().getFirst().getTitle());
    }

    @Test
    public void testGetAllTasks() throws IOException {
        taskManager.createTask(new Task("Test Task", "Test Description"));

        HttpURLConnection connection = createConnection("/tasks", "GET");
        assertEquals(200, connection.getResponseCode());

    }

    @Test
    public void testGetTaskById() throws IOException {
        Task task = new Task("Test Task", "Test Description");
        taskManager.createTask(task);

        HttpURLConnection connection = createConnection("/tasks/" + task.getId(), "GET");
        assertEquals(200, connection.getResponseCode());

        try (BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()))) {
            String response = reader.lines().collect(Collectors.joining());
            assertTrue(response.contains("Test Task"), "Response should contain the task title.");
        }
    }

    @Test
    public void testDeleteTask() throws IOException {
        Task task = new Task("Test Task", "Test Description");
        taskManager.createTask(task);

        HttpURLConnection connection = createConnection("/tasks/" + task.getId(), "DELETE");
        assertEquals(200, connection.getResponseCode());

        assertEquals(0, taskManager.getAllTasks().size());
    }

    @Test
    public void testUpdateTask() throws IOException {
        Task task = new Task("Test Task", "Test Description");
        taskManager.createTask(task);

        String jsonInputString = "{\"title\":\"Updated Task Title\", \"description\":\"Updated Description\"}";
        HttpURLConnection connection = createConnection("/tasks/" + task.getId(), "PUT");
        connection.setDoOutput(true);
        connection.getOutputStream().write(jsonInputString.getBytes());

        assertEquals(200, connection.getResponseCode());

        Task updatedTask = taskManager.getTaskById(task.getId());
        assertEquals("Updated Task Title", updatedTask.getTitle());
        assertEquals("Updated Description", updatedTask.getDescription());
    }
}
