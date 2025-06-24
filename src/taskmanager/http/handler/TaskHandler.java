package taskmanager.http.handler;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import taskmanager.exception.NotFoundException;
import taskmanager.manager.TaskManager;
import taskmanager.utiltask.Task;
import com.google.gson.Gson;
import taskmanager.http.factory.GsonFactory;

import java.io.IOException;
import java.io.InputStreamReader;
import java.util.List;

public class TaskHandler extends BaseHttpHandler implements HttpHandler {
    private final TaskManager taskManager;
    private final Gson gson = GsonFactory.createGson();

    public TaskHandler(TaskManager taskManager) {
        this.taskManager = taskManager;
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        String path = exchange.getRequestURI().getPath();
        if (path.matches("/tasks/\\d+")) {
            handleById(exchange);
        } else {
            switch (exchange.getRequestMethod()) {
                case "GET":
                    handleGet(exchange);
                    break;
                case "POST":
                    handlePost(exchange);
                    break;
                case "PUT":
                    handlePut(exchange);
                    break;
                case "DELETE":
                    handleDelete(exchange);
                    break;
                default:
                    sendNotFound(exchange);
            }
        }
    }

    private void handleGet(HttpExchange exchange) throws IOException {
        List<Task> tasks = taskManager.getAllTasks();
        String jsonResponse = gson.toJson(tasks);
        sendText(exchange, jsonResponse);
    }

    private void handleById(HttpExchange exchange) throws IOException {
        int id = Integer.parseInt(exchange.getRequestURI().getPath().split("/")[2]);
        if (exchange.getRequestMethod().equals("GET")) {
            try {
                Task task = taskManager.getTaskById(id);
                sendText(exchange, gson.toJson(task));
            } catch (NotFoundException e) {
                sendNotFound(exchange);
            }
        } else if (exchange.getRequestMethod().equals("DELETE")) {
            taskManager.deleteTask(id);
            sendText(exchange, "Task deleted");
        } else {
            sendNotFound(exchange);
        }
    }

    private void handlePost(HttpExchange exchange) throws IOException {
        Task task = gson.fromJson(new InputStreamReader(exchange.getRequestBody()), Task.class);
        if (taskManager.hasIntersection(task)) {
            sendHasInteractions(exchange);
            return;
        }
        taskManager.createTask(task);
        sendText(exchange, gson.toJson(task));
    }

    private void handlePut(HttpExchange exchange) throws IOException {
        int id = Integer.parseInt(exchange.getRequestURI().getPath().split("/")[2]);
        Task task = gson.fromJson(new InputStreamReader(exchange.getRequestBody()), Task.class);
        task.setId(id);

        if (taskManager.hasIntersection(task)) {
            sendHasInteractions(exchange);
            return;
        }

        try {
            taskManager.updateTask(task);
            sendText(exchange, gson.toJson(task));
        } catch (NotFoundException e) {
            sendNotFound(exchange);
        }
    }

    private void handleDelete(HttpExchange exchange) throws IOException {
        int id = Integer.parseInt(exchange.getRequestURI().getPath().split("/")[2]);
        taskManager.deleteTask(id);
        sendText(exchange, "Task deleted");
    }
}