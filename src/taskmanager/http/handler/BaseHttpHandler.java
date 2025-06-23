package taskmanager.http.handler;

import com.sun.net.httpserver.HttpExchange;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

public class BaseHttpHandler {
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
}
