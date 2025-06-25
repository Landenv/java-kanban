package taskmanager.http.handler;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import taskmanager.utiltask.Epic;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class EpicHandlerTest extends AbstractHandlerTest {

    @ParameterizedTest
    @MethodSource("provideEpicData")
    public void testCreateEpic(Epic epic) throws IOException {
        HttpURLConnection connection = createConnection("/epics", "POST");
        connection.setDoOutput(true);
        String jsonInputString = "{\"title\":\"" + epic.getTitle() + "\", \"description\":\"" + epic.getDescription() + "\"}";
        connection.getOutputStream().write(jsonInputString.getBytes());

        assertEquals(200, connection.getResponseCode());
        assertEquals(1, taskManager.getAllEpics().size());
        assertEquals(epic.getTitle(), taskManager.getAllEpics().getFirst().getTitle());
    }

    @ParameterizedTest
    @MethodSource("provideEpicData")
    public void testGetAllEpics(Epic epic) throws IOException {
        taskManager.createEpic(epic);

        HttpURLConnection connection = createConnection("/epics", "GET");
        assertEquals(200, connection.getResponseCode());

        try (BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()))) {
            String response = reader.lines().collect(Collectors.joining());
            assertTrue(response.contains(epic.getTitle()), "Response should contain the epic title.");
        }
    }

    @ParameterizedTest
    @MethodSource("provideEpicData")
    public void testGetEpicById(Epic epic) throws IOException {
        taskManager.createEpic(epic);

        HttpURLConnection connection = createConnection("/epics/" + epic.getId(), "GET");
        assertEquals(200, connection.getResponseCode());

        try (BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()))) {
            String response = reader.lines().collect(Collectors.joining());
            assertTrue(response.contains(epic.getTitle()), "Response should contain the epic title.");
        }
    }

    @ParameterizedTest
    @MethodSource("provideEpicData")
    public void testDeleteEpic(Epic epic) throws IOException {
        taskManager.createEpic(epic);

        HttpURLConnection connection = createConnection("/epics/" + epic.getId(), "DELETE");
        assertEquals(200, connection.getResponseCode());

        assertEquals(0, taskManager.getAllEpics().size());
    }

    @ParameterizedTest
    @MethodSource("provideEpicData")
    public void testUpdateEpic(Epic epic) throws IOException {
        taskManager.createEpic(epic);

        String jsonInputString = "{\"title\":\"Updated Epic Title\", \"description\":\"Updated Description\"}";
        HttpURLConnection connection = createConnection("/epics/" + epic.getId(), "PUT");
        connection.setDoOutput(true);
        connection.getOutputStream().write(jsonInputString.getBytes());

        assertEquals(200, connection.getResponseCode());

        Epic updatedEpic = taskManager.getEpicById(epic.getId());
        assertEquals("Updated Epic Title", updatedEpic.getTitle());
        assertEquals("Updated Description", updatedEpic.getDescription());
    }

}
