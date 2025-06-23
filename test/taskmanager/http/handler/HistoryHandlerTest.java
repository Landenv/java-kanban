package taskmanager.http.handler;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import taskmanager.manager.InMemoryTaskManager;
import taskmanager.http.HttpTaskServer;
import taskmanager.utiltask.Task;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URL;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class HistoryHandlerTest {
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

    private HttpURLConnection createConnection() throws IOException {
        URI uri = URI.create("http://localhost:8080" + "/history");
        URL url = uri.toURL();
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod("GET");
        return connection;
    }

    @Test
    public void testGetHistory() throws IOException {
        Task task = new Task("Test Task", "Test Description");
        taskManager.createTask(task);
        taskManager.getTaskById(task.getId());

        HttpURLConnection connection = createConnection();
        assertEquals(200, connection.getResponseCode());

    }
}
