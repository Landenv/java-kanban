package taskmanager.http.handler;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import taskmanager.manager.InMemoryTaskManager;
import taskmanager.http.HttpTaskServer;
import taskmanager.utiltask.Epic;
import taskmanager.utiltask.Subtask;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URL;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class SubtaskHandlerTest {
    private HttpTaskServer httpTaskServer;
    private InMemoryTaskManager taskManager;

    @BeforeEach
    public void setUp() throws IOException {
        taskManager = new InMemoryTaskManager();
        httpTaskServer = new HttpTaskServer(taskManager);
        httpTaskServer.start();

        taskManager.createEpic(new Epic("Test Epic", "Epic Description"));
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
    public void testCreateSubtask() throws IOException {
        String jsonInputString = "{\"title\":\"Test Subtask\", \"description\":\"Test Subtask Description\", \"epicId\":1}";
        HttpURLConnection connection = createConnection("/subtasks", "POST");
        connection.setDoOutput(true);
        connection.getOutputStream().write(jsonInputString.getBytes());

        assertEquals(200, connection.getResponseCode());

        assertEquals(1, taskManager.getAllSubtasks().size());
        assertEquals("Test Subtask", taskManager.getAllSubtasks().getFirst().getTitle());
    }

    @Test
    public void testGetAllSubtasks() throws IOException {
        taskManager.createSubtask(new Subtask("Test Subtask", "Test Subtask Description", 1));

        HttpURLConnection connection = createConnection("/subtasks", "GET");
        assertEquals(200, connection.getResponseCode());

    }

    @Test
    public void testGetSubtaskById() throws IOException {
        Subtask subtask = new Subtask("Test Subtask", "Test Subtask Description", 1);
        taskManager.createSubtask(subtask);

        HttpURLConnection connection = createConnection("/subtasks/" + subtask.getId(), "GET");
        assertEquals(200, connection.getResponseCode());

        try (BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()))) {
            String response = reader.lines().collect(Collectors.joining());
            assertTrue(response.contains("Test Subtask"), "Response should contain the subtask title.");
        }
    }

    @Test
    public void testDeleteSubtask() throws IOException {
        Subtask subtask = new Subtask("Test Subtask", "Test Subtask Description", 1);
        taskManager.createSubtask(subtask);

        HttpURLConnection connection = createConnection("/subtasks/" + subtask.getId(), "DELETE");
        assertEquals(200, connection.getResponseCode());

        assertEquals(0, taskManager.getAllSubtasks().size());
    }

    @Test
    public void testUpdateSubtask() throws IOException {
        Subtask subtask = new Subtask("Test Subtask", "Test Subtask Description", 1);
        taskManager.createSubtask(subtask);

        String jsonInputString = "{\"title\":\"Updated Subtask Title\", \"description\":\"Updated Subtask Description\", \"epicId\":1}";
        HttpURLConnection connection = createConnection("/subtasks/" + subtask.getId(), "PUT");
        connection.setDoOutput(true);
        connection.getOutputStream().write(jsonInputString.getBytes());

        assertEquals(200, connection.getResponseCode());

        Subtask updatedSubtask = taskManager.getSubtaskById(subtask.getId());
        assertEquals("Updated Subtask Title", updatedSubtask.getTitle());
        assertEquals("Updated Subtask Description", updatedSubtask.getDescription());
    }
}
