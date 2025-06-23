package taskmanager.http.handler;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import taskmanager.manager.TaskManager;
import taskmanager.utiltask.Task;
import com.google.gson.Gson;
import taskmanager.http.factory.GsonFactory;

import java.io.IOException;
import java.util.List;

public class PrioritizedHandler extends BaseHttpHandler implements HttpHandler {
    private final TaskManager taskManager;
    private final Gson gson = GsonFactory.createGson();

    public PrioritizedHandler(TaskManager taskManager) {
        this.taskManager = taskManager;
    }

    public void handle(HttpExchange exchange) throws IOException {
        System.out.println("Received request: " + exchange.getRequestURI());
        try {
            List<Task> prioritizedTasks = taskManager.getPrioritizedTasks();
            String jsonResponse = gson.toJson(prioritizedTasks);
            sendText(exchange, jsonResponse);
        } catch (Exception e) {
            System.out.println("Error processing request: " + e.getMessage());
            sendNotFound(exchange);
        }
    }
}
