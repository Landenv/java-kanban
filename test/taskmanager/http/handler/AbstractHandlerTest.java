package taskmanager.http.handler;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.params.provider.Arguments;
import taskmanager.manager.InMemoryTaskManager;
import taskmanager.http.HttpTaskServer;
import taskmanager.utiltask.Epic;
import taskmanager.utiltask.Subtask;
import taskmanager.utiltask.Task;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URL;
import java.util.stream.Stream;

public abstract class AbstractHandlerTest {
    protected HttpTaskServer httpTaskServer;
    protected InMemoryTaskManager taskManager;

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

    protected HttpURLConnection createConnection(String endpoint, String method) throws IOException {
        URI uri = URI.create("http://localhost:8080" + endpoint);
        URL url = uri.toURL();
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod(method);
        return connection;
    }

    static Stream<Arguments> provideTaskData() {
        return Stream.of(
                Arguments.of(new Task("Test Task 1", "Test Description 1")),
                Arguments.of(new Task("Test Task 2", "Test Description 2"))
        );
    }

    static Stream<Arguments> provideEpicData() {
        return Stream.of(
                Arguments.of(new Epic("Test Epic 1", "Epic Description 1")),
                Arguments.of(new Epic("Test Epic 2", "Epic Description 2"))
        );
    }

    static Stream<Arguments> provideSubtaskData() {
        return Stream.of(
                Arguments.of(new Subtask("Test Subtask 1", "Subtask Description 1", 1)),
                Arguments.of(new Subtask("Test Subtask 2", "Subtask Description 2", 1))
        );
    }

}
