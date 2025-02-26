package server.handlers;

import com.google.gson.Gson;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import exception.NotFoundException;
import exception.TaskIntersectionException;
import manager.TaskManager;
import server.HttpTaskServer;
import task.Epic;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Optional;

public class EpicHandler extends BaseHttpHandler implements HttpHandler {
    private final TaskManager taskManager;
    private final Gson gson;
    private String requestBody;

    public EpicHandler(TaskManager taskManager) {
        this.taskManager = taskManager;
        this.gson = HttpTaskServer.getGson();
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        String method = exchange.getRequestMethod();
        String path = exchange.getRequestURI().getPath();
        requestBody = new String(exchange.getRequestBody().readAllBytes(), StandardCharsets.UTF_8);
        Endpoint endpoint = Endpoint.endpointFromMethodAndPath(method, path);

        switch (endpoint) {
            case GET_EPICS:
                List<Epic> epics = taskManager.getEpics();
                if (epics.isEmpty()) {
                    sendIfEmptyList(exchange);
                    return;
                }
                sendText(exchange, gson.toJson(epics), HttpStatusCode.OK);
                break;

            case GET_EPIC_BY_ID:
                try {
                    int idForGet = extractIdFromPath(path);
                    Epic epic = taskManager.getEpic(idForGet);
                    if (epic == null) {
                        sendNotFound(exchange);
                        return;
                    }
                    sendText(exchange, gson.toJson(epic), HttpStatusCode.OK);

                } catch (NotFoundException exception) {
                    sendText(exchange, exception.getMessage(), HttpStatusCode.NOT_FOUND);
                } catch (IllegalArgumentException exception) {
                    sendText(exchange, "Ошибка: неверный путь или идентификатор", HttpStatusCode.BAD_REQUEST);
                }
                break;

            case GET_EPIC_SUBTASKS:
                try {
                    int idForGet = extractIdFromPath(path);
                    Optional<Epic> epic = taskManager.getEpics().stream()
                            .filter(epicStr -> epicStr.getId() == idForGet)
                            .findFirst();

                    if (epic.isEmpty()) {
                        sendNotFound(exchange);
                        return;
                    }
                    sendText(exchange, gson.toJson(taskManager.getEpicSubtasks(idForGet)), HttpStatusCode.OK);

                } catch (IllegalArgumentException exception) {
                    sendText(exchange, "Ошибка: неверный путь или идентификатор", HttpStatusCode.BAD_REQUEST);
                }
                break;

            case CREATE_OR_UPDATE_EPIC:
                createOrUpdateEpic(exchange);
                break;

            case DELETE_EPIC:
                try {
                    int idForDelete = extractIdFromPath(path);
                    if (taskManager.getEpic(idForDelete) == null) {
                        sendNotFound(exchange);
                        return;
                    }
                    taskManager.deleteEpicById(idForDelete);
                    sendText(exchange, "Эпик с Id: " + idForDelete + " успешно удален", HttpStatusCode.NO_CONTENT);

                } catch (NotFoundException exception) {
                    sendText(exchange, exception.getMessage(), HttpStatusCode.NOT_FOUND);
                }
                break;

            default:
                new HttpTaskServer.UnknownPathHandler().handle(exchange);
        }
    }

    private void createOrUpdateEpic(HttpExchange exchange) throws IOException {
        Epic newEpic;
        try {
            newEpic = gson.fromJson(requestBody, Epic.class);
            if (newEpic.getId() == 0) {
                newEpic = new Epic(newEpic.getName(), newEpic.getDescription());
            } else {
                newEpic = new Epic(newEpic.getId(), newEpic.getName(), newEpic.getDescription());
            }

        } catch (Exception exception) {
            sendText(exchange, "Ошибка: некорректный формат эпика в теле запроса", HttpStatusCode.BAD_REQUEST);
            return;
        }

        if (newEpic.getId() < -1) {
            sendText(exchange, "Ошибка: неверный Id эпика", HttpStatusCode.BAD_REQUEST);
            return;
        }

        if (newEpic.getId() > 0 && taskManager.getEpics().isEmpty()) {
            sendText(exchange, "Ошибка: Список эпиков пуст", HttpStatusCode.BAD_REQUEST);
            return;
        }

        try {
            if (newEpic.getId() == -1) {
                int newEpicId = taskManager.addNewEpic(newEpic);
                sendText(exchange, "Эпик с Id: " + newEpicId + " успешно создан", HttpStatusCode.CREATED);
                return;
            }

            Epic finalNewSubtask = newEpic;
            List<Epic> list = taskManager.getEpics().stream()
                    .filter(epic -> epic.getId() == finalNewSubtask.getId())
                    .toList();

            if (!list.isEmpty()) {
                taskManager.updateEpicFill(newEpic);
                sendText(exchange, "Эпик с Id: " + newEpic.getId() + " успешно обновлен", HttpStatusCode.CREATED);
            } else {
                sendNotFound(exchange);
            }

        } catch (TaskIntersectionException exception) {
            sendHasInteractions(exchange);
        } catch (Exception exception) {
            sendText(exchange, "Ошибка: неизвестная ошибка при добавлении эпика", HttpStatusCode.INTERNAL_SERVER_ERROR);
        }
    }

    private int extractIdFromPath(String path) {
        String[] pathParts = path.split("/");

        if (pathParts.length >= 3 && "epics".equals(pathParts[1])) {
            return Integer.parseInt(pathParts[2]);
        }

        throw new IllegalArgumentException("Неверный путь, идентификатор не найден");
    }
}