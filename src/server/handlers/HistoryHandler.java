package server.handlers;

import com.google.gson.Gson;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import manager.TaskManager;
import server.HttpTaskServer;
import task.Task;

import java.io.IOException;
import java.util.List;

public class HistoryHandler extends BaseHttpHandler implements HttpHandler {
    private final TaskManager taskManager;
    private final Gson gson;

    public HistoryHandler(TaskManager taskManager) {
        this.taskManager = taskManager;
        this.gson = HttpTaskServer.getGson();
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        String method = exchange.getRequestMethod();
        String path = exchange.getRequestURI().getPath();
        Endpoint endpoint = Endpoint.endpointFromMethodAndPath(method, path);

        switch (endpoint) {
            case GET_HISTORY:
                List<Task> history = taskManager.getHistory();
                if (history.isEmpty()) {
                    sendIfEmptyList(exchange);
                    return;
                }
                sendText(exchange, gson.toJson(history), HttpStatusCode.OK);
                break;

            case GET_PRIORITIZED:
                List<Task> prioritizedTasks = taskManager.getPrioritizedTasks();
                if (prioritizedTasks.isEmpty()) {
                    sendIfEmptyList(exchange);
                    return;
                }

                sendText(exchange, gson.toJson(prioritizedTasks), HttpStatusCode.OK);
                break;

            default:
                new HttpTaskServer.UnknownPathHandler().handle(exchange);
        }
    }
}
