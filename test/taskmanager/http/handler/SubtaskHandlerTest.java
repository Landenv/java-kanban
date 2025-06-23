package taskmanager.http.handler;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import taskmanager.manager.InMemoryTaskManager;
import taskmanager.http.HttpTaskServer;
import taskmanager.utiltask.Epic;
import taskmanager.utiltask.Subtask;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URL;

import static org.junit.jupiter.api.Assertions.assertEquals;

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
    public void testDeleteSubtask() throws IOException {
        Subtask subtask = new Subtask("Test Subtask", "Test Subtask Description", 1);
        taskManager.createSubtask(subtask);

        HttpURLConnection connection = createConnection("/subtasks/" + subtask.getId(), "DELETE");
        assertEquals(200, connection.getResponseCode());

        assertEquals(0, taskManager.getAllSubtasks().size());
    }
}
