package taskmanager.http.handler;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import taskmanager.exception.NotFoundException;
import taskmanager.manager.TaskManager;
import taskmanager.utiltask.Epic;
import com.google.gson.Gson;
import taskmanager.http.factory.GsonFactory;

import java.io.IOException;
import java.io.InputStreamReader;
import java.util.List;

public class EpicHandler extends BaseHttpHandler implements HttpHandler {
    private final TaskManager taskManager;
    private final Gson gson = GsonFactory.createGson();

    public EpicHandler(TaskManager taskManager) {
        this.taskManager = taskManager;
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        String path = exchange.getRequestURI().getPath();
        if (path.matches("/epics/\\d+")) {
            handleById(exchange);
        } else {
            switch (exchange.getRequestMethod()) {
                case "GET":
                    handleGet(exchange);
                    break;
                case "POST":
                    handlePost(exchange);
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
        List<Epic> epics = taskManager.getAllEpics();
        String jsonResponse = gson.toJson(epics);
        sendText(exchange, jsonResponse);
    }

    private void handleById(HttpExchange exchange) throws IOException {
        int id = Integer.parseInt(exchange.getRequestURI().getPath().split("/")[2]);
        if (exchange.getRequestMethod().equals("GET")) {
            try {
                Epic epic = taskManager.getEpicById(id);
                sendText(exchange, gson.toJson(epic));
            } catch (NotFoundException e) {
                sendNotFound(exchange);
            }
        } else if (exchange.getRequestMethod().equals("DELETE")) {
            taskManager.deleteEpic(id);
            sendText(exchange, "Epic deleted");
        } else {
            sendNotFound(exchange);
        }
    }

    private void handlePost(HttpExchange exchange) throws IOException {
        Epic epic = gson.fromJson(new InputStreamReader(exchange.getRequestBody()), Epic.class);
        taskManager.createEpic(epic);
        sendText(exchange, gson.toJson(epic));
    }

    private void handleDelete(HttpExchange exchange) throws IOException {
        int id = Integer.parseInt(exchange.getRequestURI().getPath().split("/")[2]);
        taskManager.deleteEpic(id);
        sendText(exchange, "Epic deleted");
    }
}
