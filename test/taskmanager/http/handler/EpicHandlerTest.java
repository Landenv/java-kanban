package taskmanager.http.handler;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import taskmanager.manager.InMemoryTaskManager;
import taskmanager.http.HttpTaskServer;
import taskmanager.utiltask.Epic;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URL;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class EpicHandlerTest {
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
    public void testCreateEpic() throws IOException {
        String jsonInputString = "{\"title\":\"Test Epic\", \"description\":\"Epic Description\"}";
        HttpURLConnection connection = createConnection("/epics", "POST");
        connection.setDoOutput(true);
        connection.getOutputStream().write(jsonInputString.getBytes());

        assertEquals(200, connection.getResponseCode());

        assertEquals(1, taskManager.getAllEpics().size());
        assertEquals("Test Epic", taskManager.getAllEpics().getFirst().getTitle());
    }

    @Test
    public void testGetAllEpics() throws IOException {
        taskManager.createEpic(new Epic("Test Epic", "Epic Description"));

        HttpURLConnection connection = createConnection("/epics", "GET");
        System.out.println("Requesting URL: " + connection.getURL()); // Добавьте этот вывод
        assertEquals(200, connection.getResponseCode());

        try (BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()))) {
            String response = reader.lines().collect(Collectors.joining());
            assertTrue(response.contains("Test Epic"), "Response should contain the created epic title.");
        }
    }

    @Test
    public void testDeleteEpic() throws IOException {
        Epic epic = new Epic("Test Epic", "Epic Description");
        taskManager.createEpic(epic);

        HttpURLConnection connection = createConnection("/epics/" + epic.getId(), "DELETE");
        assertEquals(200, connection.getResponseCode());

        assertEquals(0, taskManager.getAllEpics().size());
    }
}
