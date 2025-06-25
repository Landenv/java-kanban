package taskmanager.http.handler;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import taskmanager.exception.NotFoundException;
import taskmanager.manager.TaskManager;
import taskmanager.utiltask.Task;

import java.io.IOException;
import java.io.InputStreamReader;
import java.util.List;

public abstract class AbstractTaskHandler<T extends Task> extends BaseHttpHandler implements HttpHandler {
    protected final TaskManager taskManager;

    public AbstractTaskHandler(TaskManager taskManager) {
        super();
        this.taskManager = taskManager;
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        String path = exchange.getRequestURI().getPath();
        if (path.matches(getPathPattern())) {
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

    protected abstract String getPathPattern();

    protected abstract T getById(int id) throws NotFoundException;

    protected abstract void create(T item);

    protected abstract void update(T item);

    protected abstract void delete(int id);

    protected abstract Class<T> getType();

    protected abstract List<T> getAllItems();

    private void handleById(HttpExchange exchange) throws IOException {
        int id = Integer.parseInt(exchange.getRequestURI().getPath().split("/")[2]);
        if (exchange.getRequestMethod().equals("GET")) {
            try {
                T item = getById(id);
                sendJson(exchange, item);
            } catch (NotFoundException e) {
                sendNotFound(exchange);
            }
        } else if (exchange.getRequestMethod().equals("PUT")) {
            try {
                T item = gson.fromJson(new InputStreamReader(exchange.getRequestBody()), getType());
                item.setId(id); // Устанавливаем ID задачи
                update(item);
                sendJson(exchange, item);
            } catch (NotFoundException e) {
                sendNotFound(exchange);
            } catch (IllegalArgumentException e) {
                sendHasInteractions(exchange); // Если есть пересечения
            } catch (Exception e) {
                sendNotFound(exchange); // Обработка других ошибок
            }
        } else if (exchange.getRequestMethod().equals("DELETE")) {
            delete(id);
            sendText(exchange, "Item deleted");
        } else {
            sendNotFound(exchange);
        }
    }

    private void handleGet(HttpExchange exchange) throws IOException {
        List<T> items = getAllItems();
        sendJson(exchange, items);
    }

    private void handlePost(HttpExchange exchange) throws IOException {
        T item = gson.fromJson(new InputStreamReader(exchange.getRequestBody()), getType());
        create(item);
        sendJson(exchange, item);
    }

    private void handleDelete(HttpExchange exchange) throws IOException {
        int id = Integer.parseInt(exchange.getRequestURI().getPath().split("/")[2]);
        delete(id);
        sendText(exchange, "Item deleted");
    }
}
