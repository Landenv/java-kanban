package taskmanager.http.handler;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import taskmanager.utiltask.Task;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.time.LocalDateTime;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class PrioritizedHandlerTest extends AbstractHandlerTest {

    @ParameterizedTest
    @MethodSource("provideTaskData")
    public void testGetPrioritizedTasks(Task task) throws IOException {
        task.setStartTime(LocalDateTime.now());
        taskManager.createTask(task);

        HttpURLConnection connection = createConnection("/prioritized", "GET");
        assertEquals(200, connection.getResponseCode());
    }

    static Stream<Arguments> provideTaskData() {
        return Stream.of(
                Arguments.of(new Task("Test Task 1", "Test Description 1")),
                Arguments.of(new Task("Test Task 2", "Test Description 2"))
        );
    }
}
