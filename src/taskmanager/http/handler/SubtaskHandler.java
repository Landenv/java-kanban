package taskmanager.http.handler;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import taskmanager.exception.NotFoundException;
import taskmanager.manager.TaskManager;
import taskmanager.utiltask.Subtask;
import com.google.gson.Gson;
import taskmanager.http.factory.GsonFactory;

import java.io.IOException;
import java.io.InputStreamReader;
import java.util.List;

public class SubtaskHandler extends BaseHttpHandler implements HttpHandler {
    private final TaskManager taskManager;
    private final Gson gson = GsonFactory.createGson();

    public SubtaskHandler(TaskManager taskManager) {
        this.taskManager = taskManager;
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        String path = exchange.getRequestURI().getPath();
        if (path.matches("/subtasks/\\d+")) {
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
        List<Subtask> subtasks = taskManager.getAllSubtasks();
        String jsonResponse = gson.toJson(subtasks);
        sendText(exchange, jsonResponse);
    }

    private void handleById(HttpExchange exchange) throws IOException {
        int id = Integer.parseInt(exchange.getRequestURI().getPath().split("/")[2]);
        if (exchange.getRequestMethod().equals("GET")) {
            try {
                Subtask subtask = taskManager.getSubtaskById(id);
                sendText(exchange, gson.toJson(subtask));
            } catch (NotFoundException e) {
                sendNotFound(exchange);
            }
        } else if (exchange.getRequestMethod().equals("DELETE")) {
            taskManager.deleteSubtasks(id);
            sendText(exchange, "Subtask deleted");
        } else {
            sendNotFound(exchange);
        }
    }

    private void handlePost(HttpExchange exchange) throws IOException {
        Subtask subtask = gson.fromJson(new InputStreamReader(exchange.getRequestBody()), Subtask.class);
        if (taskManager.hasIntersection(subtask)) {
            sendHasInteractions(exchange);
            return;
        }
        taskManager.createSubtask(subtask);
        sendText(exchange, gson.toJson(subtask));
    }

    private void handlePut(HttpExchange exchange) throws IOException {
        int id = Integer.parseInt(exchange.getRequestURI().getPath().split("/")[2]);
        System.out.println("Handling PUT for Subtask ID: " + id); // Логируем ID подзадачи
        Subtask subtask = gson.fromJson(new InputStreamReader(exchange.getRequestBody()), Subtask.class);
        subtask.setId(id);

        // Логируем данные подзадачи
        System.out.println("Subtask data before update: " + gson.toJson(subtask));

        if (taskManager.hasIntersection(subtask)) {
            sendHasInteractions(exchange);
            return;
        }

        try {
            taskManager.updateSubtask(subtask);
            sendText(exchange, gson.toJson(subtask));
        } catch (NotFoundException e) {
            System.out.println("Subtask not found: " + e.getMessage()); // Логируем ошибку
            sendNotFound(exchange);
        }
    }

    private void handleDelete(HttpExchange exchange) throws IOException {
        int id = Integer.parseInt(exchange.getRequestURI().getPath().split("/")[2]);
        taskManager.deleteSubtasks(id);
        sendText(exchange, "Subtask deleted");
    }
}
