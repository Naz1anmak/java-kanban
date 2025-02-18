package server.handlers;

import com.google.gson.Gson;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import exception.NotFoundException;
import exception.TaskIntersectionException;
import manager.TaskManager;
import server.HttpTaskServer;
import task.Task;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.List;

public class TaskHandler extends BaseHttpHandler implements HttpHandler {
    private final TaskManager taskManager;
    private final Gson gson;
    private String requestBody;

    public TaskHandler(TaskManager taskManager) {
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
            case GET_TASKS:
                List<Task> tasks = taskManager.getTasks();
                if (tasks.isEmpty()) {
                    sendIfEmptyList(exchange);
                    return;
                }
                sendText(exchange, gson.toJson(tasks), HttpStatusCode.OK);
                break;

            case GET_TASK_BY_ID:
                try {
                    int idForGet = extractIdFromPath(path);
                    Task task = taskManager.getTask(idForGet);
                    if (task == null) {
                        sendNotFound(exchange);
                        return;
                    }
                    sendText(exchange, gson.toJson(task), HttpStatusCode.OK);

                } catch (NotFoundException exception) {
                    sendText(exchange, exception.getMessage(), HttpStatusCode.NOT_FOUND);
                } catch (IllegalArgumentException exception) {
                    sendText(exchange, "Ошибка: неверный путь или идентификатор", HttpStatusCode.BAD_REQUEST);
                }
                break;

            case CREATE_OR_UPDATE_TASK:
                createOrUpdateTask(exchange);
                break;

            case DELETE_TASK:
                try {
                    int idForDelete = extractIdFromPath(path);
                    if (taskManager.getTask(idForDelete) == null) {
                        sendNotFound(exchange);
                        return;
                    }
                    taskManager.deleteTaskById(idForDelete);
                    sendText(exchange, "Задача с Id: " + idForDelete + " успешно удалена", HttpStatusCode.NO_CONTENT);

                } catch (NotFoundException exception) {
                    sendText(exchange, exception.getMessage(), HttpStatusCode.NOT_FOUND);
                }
                break;

            default:
                new HttpTaskServer.UnknownPathHandler().handle(exchange);
        }
    }

    private void createOrUpdateTask(HttpExchange exchange) throws IOException {
        Task newTask;
        try {
            newTask = gson.fromJson(requestBody, Task.class);
            if (newTask.getId() == 0) {
                newTask = new Task(newTask.getName(), newTask.getDescription(), newTask.getStatus(),
                        newTask.getStartTime(), newTask.getDuration());
            } else {
                newTask = new Task(newTask.getId(), newTask.getName(), newTask.getDescription(), newTask.getStatus(),
                        newTask.getStartTime(), newTask.getDuration());
            }

        } catch (Exception exception) {
            sendText(exchange, "Ошибка: некорректный формат задачи в теле запроса", HttpStatusCode.BAD_REQUEST);
            return;
        }

        if (newTask.getId() < -1) {
            sendText(exchange, "Ошибка: неверный Id задачи", HttpStatusCode.BAD_REQUEST);
            return;
        }

        if (newTask.getId() > 0 && taskManager.getTasks().isEmpty()) {
            sendText(exchange, "Ошибка: Список задач пуст", HttpStatusCode.BAD_REQUEST);
            return;
        }

        try {
            if (newTask.getId() == -1) {
                int newTaskId = taskManager.addNewTask(newTask);
                sendText(exchange, "Задача с Id: " + newTaskId + " успешно создана", HttpStatusCode.CREATED);
                return;
            }

            Task finalNewTask = newTask;
            List<Task> list = taskManager.getTasks().stream()
                    .filter(task -> task.getId() == finalNewTask.getId())
                    .toList();

            if (!list.isEmpty()) {
                taskManager.updateTask(newTask);
                sendText(exchange, "Задача с Id: " + newTask.getId() + " успешно обновлена", HttpStatusCode.CREATED);
            } else {
                sendNotFound(exchange);
            }

        } catch (TaskIntersectionException exception) {
            sendHasInteractions(exchange);
        } catch (Exception exception) {
            sendText(exchange, "Ошибка: неизвестная ошибка при добавлении задачи", HttpStatusCode.INTERNAL_SERVER_ERROR);
        }
    }

    private int extractIdFromPath(String path) {
        String[] pathParts = path.split("/");

        if (pathParts.length >= 3 && "tasks".equals(pathParts[1])) {
            return Integer.parseInt(pathParts[2]);
        }

        throw new IllegalArgumentException("Неверный путь, идентификатор не найден");
    }
}