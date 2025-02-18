package server.handlers;

import com.sun.net.httpserver.HttpExchange;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;

public abstract class BaseHttpHandler {

    protected void sendText(HttpExchange exchange, String response, HttpStatusCode statusCode) throws IOException {
        byte[] responseBytes = response.getBytes(StandardCharsets.UTF_8);
        exchange.getResponseHeaders().add("Content-Type", "application/json; charset=UTF-8");
        exchange.sendResponseHeaders(statusCode.getCode(), responseBytes.length);
        try (OutputStream os = exchange.getResponseBody()) {
            os.write(responseBytes);
        }
    }

    protected void sendNotFound(HttpExchange exchange) throws IOException {
        sendText(exchange, "Ошибка: путь не найден", HttpStatusCode.NOT_FOUND);
    }

    protected void sendHasInteractions(HttpExchange exchange) throws IOException {
        sendText(exchange, "Ошибка: задача пересекается с существующими задачами", HttpStatusCode.NOT_ACCEPTABLE);
    }

    protected void sendIfEmptyList(HttpExchange exchange) throws IOException {
        exchange.sendResponseHeaders(HttpStatusCode.NO_CONTENT.getCode(), -1);
    }
}
