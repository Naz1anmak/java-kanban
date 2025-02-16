package server.handlers;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import manager.TaskManager;
import server.HttpTaskServer;
import server.adapters.Adapters;
import task.Task;

import java.io.IOException;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;

public class PrioritizedHandler extends BaseHttpHandler implements HttpHandler {
    private final TaskManager taskManager;

    public PrioritizedHandler(TaskManager taskManager) {
        this.taskManager = taskManager;
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        String method = exchange.getRequestMethod();
        String path = exchange.getRequestURI().getPath();
        Endpoint endpoint = Endpoint.endpointFromMethodAndPath(method, path);

        Gson gson = new GsonBuilder()
                .setPrettyPrinting()
                .excludeFieldsWithoutExposeAnnotation()
                .registerTypeAdapter(LocalDateTime.class, new Adapters.LocalDateTimeAdapter())
                .registerTypeAdapter(Duration.class, new Adapters.DurationAdapter())
                .create();

        List<Task> prioritizedTasks = taskManager.getPrioritizedTasks();
        switch (endpoint) {
            case GET_PRIORITIZED:
                if (prioritizedTasks.isEmpty()) {
                    sendIfEmptyList(exchange);
                    return;
                }

                sendText(exchange, gson.toJson(prioritizedTasks), 200);
                break;

            default:
                new HttpTaskServer.UnknownPathHandler().handle(exchange);
        }
    }
}
