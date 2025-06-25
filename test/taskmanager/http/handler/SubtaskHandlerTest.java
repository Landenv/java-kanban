package taskmanager.http.handler;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import taskmanager.utiltask.Epic;
import taskmanager.utiltask.Subtask;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class SubtaskHandlerTest extends AbstractHandlerTest {

    @BeforeEach
    public void setUp() throws IOException {
        super.setUp();
        taskManager.createEpic(new Epic("Test Epic", "Epic Description"));
    }

    @ParameterizedTest
    @MethodSource("provideSubtaskData")
    public void testCreateSubtask(Subtask subtask) throws IOException {
        HttpURLConnection connection = createConnection("/subtasks", "POST");
        connection.setDoOutput(true);
        String jsonInputString = "{\"title\":\"" + subtask.getTitle() + "\", \"description\":\"" + subtask.getDescription() + "\", \"epicId\":" + subtask.getEpicID() + "}";
        connection.getOutputStream().write(jsonInputString.getBytes());

        assertEquals(200, connection.getResponseCode());
        assertEquals(1, taskManager.getAllSubtasks().size());
        assertEquals(subtask.getTitle(), taskManager.getAllSubtasks().getFirst().getTitle());
    }

    @ParameterizedTest
    @MethodSource("provideSubtaskData")
    public void testGetAllSubtasks(Subtask subtask) throws IOException {
        taskManager.createSubtask(subtask);

        HttpURLConnection connection = createConnection("/subtasks", "GET");
        assertEquals(200, connection.getResponseCode());

        try (BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()))) {
            String response = reader.lines().collect(Collectors.joining());
            assertTrue(response.contains(subtask.getTitle()), "Response should contain the subtask title.");
        }
    }

    @ParameterizedTest
    @MethodSource("provideSubtaskData")
    public void testGetSubtaskById(Subtask subtask) throws IOException {
        taskManager.createSubtask(subtask);

        HttpURLConnection connection = createConnection("/subtasks/" + subtask.getId(), "GET");
        assertEquals(200, connection.getResponseCode());

        try (BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()))) {
            String response = reader.lines().collect(Collectors.joining());
            assertTrue(response.contains(subtask.getTitle()), "Response should contain the subtask title.");
        }
    }

    @ParameterizedTest
    @MethodSource("provideSubtaskData")
    public void testDeleteSubtask(Subtask subtask) throws IOException {
        taskManager.createSubtask(subtask);

        HttpURLConnection connection = createConnection("/subtasks/" + subtask.getId(), "DELETE");
        assertEquals(200, connection.getResponseCode());

        assertEquals(0, taskManager.getAllSubtasks().size());
    }

    @ParameterizedTest
    @MethodSource("provideSubtaskData")
    public void testUpdateSubtask(Subtask subtask) throws IOException {
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
