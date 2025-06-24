package taskmanager.http;

import com.sun.net.httpserver.HttpServer;
import taskmanager.manager.Manager;
import taskmanager.manager.TaskManager;
import taskmanager.http.handler.TaskHandler;
import taskmanager.http.handler.SubtaskHandler;
import taskmanager.http.handler.EpicHandler;
import taskmanager.http.handler.HistoryHandler;
import taskmanager.http.handler.PrioritizedHandler;

import java.io.IOException;
import java.net.InetSocketAddress;

public class HttpTaskServer {
    private final TaskManager taskManager;
    private HttpServer server;

    public HttpTaskServer(TaskManager taskManager) {
        this.taskManager = taskManager;
    }

    public void start() throws IOException {
        server = HttpServer.create(new InetSocketAddress(8080), 0);
        System.out.println("Сервер создан и слушает на порту 8080");
        server.createContext("/tasks", new TaskHandler(taskManager));
        server.createContext("/subtasks", new SubtaskHandler(taskManager));
        server.createContext("/epics", new EpicHandler(taskManager));
        server.createContext("/history", new HistoryHandler(taskManager));
        server.createContext("/prioritized", new PrioritizedHandler(taskManager));

        server.setExecutor(null);
        server.start();
        System.out.println("Сервер запущен");
    }

    public void stop() {
        if (server != null) {
            server.stop(0);
        }
    }

    public static void main(String[] args) {
        HttpTaskServer httpTaskServer = new HttpTaskServer(Manager.getDefault());
        try {
            httpTaskServer.start();
        } catch (IOException e) {
            System.err.println("Ошибка при запуске сервера: " + e.getMessage());
        }
    }
}
