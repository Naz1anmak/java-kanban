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

public class HistoryHandler extends BaseHttpHandler implements HttpHandler {
    private final TaskManager taskManager;

    public HistoryHandler(TaskManager taskManager) {
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

        List<Task> history = taskManager.getHistory();
        switch (endpoint) {
            case GET_HISTORY:
                if (history.isEmpty()) {
                    sendIfEmptyList(exchange);
                    return;
                }
                sendText(exchange, gson.toJson(history), 200);
                break;

            default:
                new HttpTaskServer.UnknownPathHandler().handle(exchange);
        }
    }
}
