package taskmanager.http.handler;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import taskmanager.utiltask.Task;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class TaskHandlerTest extends AbstractHandlerTest {

    @ParameterizedTest
    @MethodSource("provideTaskData")
    public void testCreateTask(Task task) throws IOException {
        HttpURLConnection connection = createConnection("/tasks", "POST");
        connection.setDoOutput(true);
        String jsonInputString = "{\"title\":\"" + task.getTitle() + "\", \"description\":\"" + task.getDescription() + "\"}";
        connection.getOutputStream().write(jsonInputString.getBytes());

        assertEquals(200, connection.getResponseCode());
        assertEquals(1, taskManager.getAllTasks().size());
        assertEquals(task.getTitle(), taskManager.getAllTasks().getFirst().getTitle());
    }

    @ParameterizedTest
    @MethodSource("provideTaskData")
    public void testGetAllTasks(Task task) throws IOException {
        taskManager.createTask(task);

        HttpURLConnection connection = createConnection("/tasks", "GET");
        assertEquals(200, connection.getResponseCode());

        try (BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()))) {
            String response = reader.lines().collect(Collectors.joining());
            assertTrue(response.contains(task.getTitle()), "Response should contain the task title.");
        }
    }

    @ParameterizedTest
    @MethodSource("provideTaskData")
    public void testGetTaskById(Task task) throws IOException {
        taskManager.createTask(task);

        HttpURLConnection connection = createConnection("/tasks/" + task.getId(), "GET");
        assertEquals(200, connection.getResponseCode());

        try (BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()))) {
            String response = reader.lines().collect(Collectors.joining());
            assertTrue(response.contains(task.getTitle()), "Response should contain the task title.");
        }
    }

    @ParameterizedTest
    @MethodSource("provideTaskData")
    public void testDeleteTask(Task task) throws IOException {
        taskManager.createTask(task);

        HttpURLConnection connection = createConnection("/tasks/" + task.getId(), "DELETE");
        assertEquals(200, connection.getResponseCode());

        assertEquals(0, taskManager.getAllTasks().size());
    }

    @ParameterizedTest
    @MethodSource("provideTaskData")
    public void testUpdateTask(Task task) throws IOException {
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
