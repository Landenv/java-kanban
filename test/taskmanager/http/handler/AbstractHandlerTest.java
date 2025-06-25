package taskmanager.http.handler;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import taskmanager.manager.InMemoryTaskManager;
import taskmanager.http.HttpTaskServer;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URL;

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
}
