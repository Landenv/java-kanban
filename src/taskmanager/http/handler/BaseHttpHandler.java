package taskmanager.http.handler;

import com.google.gson.Gson;
import com.sun.net.httpserver.HttpExchange;
import taskmanager.http.factory.GsonFactory;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.function.Supplier;

public class BaseHttpHandler {
    protected final Gson gson;

    public BaseHttpHandler() {
        this.gson = GsonFactory.createGson();
    }

    protected void sendText(HttpExchange h, String text) throws IOException {
        byte[] resp = text.getBytes(StandardCharsets.UTF_8);
        h.getResponseHeaders().add("Content-Type", "application/json;charset=utf-8");
        h.sendResponseHeaders(200, resp.length);
        h.getResponseBody().write(resp);
        h.close();
    }

    protected void sendNotFound(HttpExchange h) throws IOException {
        String response = "Resource not found";
        h.sendResponseHeaders(404, response.length());
        h.getResponseBody().write(response.getBytes(StandardCharsets.UTF_8));
        h.close();
    }

    protected void sendHasInteractions(HttpExchange h) throws IOException {
        String response = "Task overlaps with existing tasks";
        h.sendResponseHeaders(406, response.length());
        h.getResponseBody().write(response.getBytes(StandardCharsets.UTF_8));
        h.close();
    }

    protected <T> void handleSimpleGet(HttpExchange exchange, Supplier<List<T>> dataSupplier) throws IOException {
        try {
            List<T> data = dataSupplier.get();
            sendJson(exchange, data);
        } catch (Exception e) {
            sendNotFound(exchange);
        }
    }

    protected void sendJson(HttpExchange exchange, Object data) throws IOException {
        String json = gson.toJson(data);
        sendText(exchange, json);
    }
}
