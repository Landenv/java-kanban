package taskmanager.http.handler;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import taskmanager.utiltask.Task;

import java.io.IOException;
import java.net.HttpURLConnection;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class HistoryHandlerTest extends AbstractHandlerTest {

    @ParameterizedTest
    @MethodSource("provideTaskData")
    public void testGetHistory(Task task) throws IOException {
        taskManager.createTask(task);
        taskManager.getTaskById(task.getId());

        HttpURLConnection connection = createConnection("/history", "GET");
        assertEquals(200, connection.getResponseCode());
    }

}
