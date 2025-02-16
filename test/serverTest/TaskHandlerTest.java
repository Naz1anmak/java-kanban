package serverTest;

import com.google.gson.Gson;
import manager.InMemoryTaskManager;
import manager.TaskManager;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import server.HttpTaskServer;
import task.Task;
import task.TaskStatus;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class TaskHandlerTest {

    private TaskManager taskManager;
    private HttpTaskServer taskServer;
    private Gson gson;
    private HttpClient client;

    @BeforeEach
    public void setUp() throws IOException {
        taskManager = new InMemoryTaskManager();
        taskServer = new HttpTaskServer(taskManager);
        gson = HttpTaskServer.getGson();
        client = HttpClient.newHttpClient();
        taskServer.start();
    }

    @AfterEach
    public void tearDown() {
        taskServer.stop();
    }

    private HttpResponse<String> sendPostRequest(String url, String jsonBody) throws IOException, InterruptedException {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .POST(HttpRequest.BodyPublishers.ofString(jsonBody))
                .build();
        return client.send(request, HttpResponse.BodyHandlers.ofString());
    }

    private HttpResponse<String> sendGetRequest(String url) throws IOException, InterruptedException {
        HttpRequest request = HttpRequest.newBuilder().uri(URI.create(url)).GET().build();
        return client.send(request, HttpResponse.BodyHandlers.ofString());
    }

    private HttpResponse<String> sendDeleteRequest(String url) throws IOException, InterruptedException {
        HttpRequest request = HttpRequest.newBuilder().uri(URI.create(url)).DELETE().build();
        return client.send(request, HttpResponse.BodyHandlers.ofString());
    }

    @Test
    public void testAddTask() throws IOException, InterruptedException {
        Task task = new Task("Тестовая задача", "Описание задачи",
                TaskStatus.NEW, LocalDateTime.now(), Duration.ofMinutes(5));
        String taskJson = gson.toJson(task);

        HttpResponse<String> response = sendPostRequest("http://localhost:8080/tasks", taskJson);
        assertEquals(201, response.statusCode(), "Неверный код ответа при добавлении задачи.");

        List<Task> tasks = taskManager.getTasks();
        assertNotNull(tasks, "Список задач не должен быть пуст.");
        assertEquals(1, tasks.size(), "Количество задач должно быть равно 1.");
        assertEquals("Тестовая задача", tasks.get(0).getName(), "Имя задачи не совпадает.");
    }

    @Test
    public void testGetAllTasks() throws IOException, InterruptedException {
        taskManager.addNewTask(new Task("Задача 1", "Описание 1", TaskStatus.NEW,
                LocalDateTime.now(), Duration.ofMinutes(30)));
        taskManager.addNewTask(new Task("Задача 2", "Описание 2", TaskStatus.DONE,
                LocalDateTime.now().plusHours(1), Duration.ofMinutes(45)));

        HttpResponse<String> response = sendGetRequest("http://localhost:8080/tasks");
        assertEquals(200, response.statusCode(), "Неверный код ответа при запросе всех задач.");

        List<Task> tasks = gson.fromJson(response.body(), List.class);
        assertNotNull(tasks, "Список задач не должен быть пуст.");
        assertEquals(2, tasks.size(), "Количество задач должно быть равно 2.");
    }

    @Test
    public void testGetTaskById() throws IOException, InterruptedException {
        int taskId = taskManager.addNewTask(new Task("Задача 1", "Описание 1",
                TaskStatus.NEW, LocalDateTime.now(), Duration.ofMinutes(30)));

        HttpResponse<String> response = sendGetRequest("http://localhost:8080/tasks/" + taskId);
        assertEquals(200, response.statusCode(), "Неверный код ответа при запросе задачи по ID.");

        Task task = gson.fromJson(response.body(), Task.class);
        assertNotNull(task, "Задача не должна быть null.");
        assertEquals("Задача 1", task.getName(), "Имя задачи не совпадает.");
    }

    @Test
    public void testGetTaskByIdNotFound() throws IOException, InterruptedException {
        HttpResponse<String> response = sendGetRequest("http://localhost:8080/tasks/999");
        assertEquals(404, response.statusCode(), "Ожидается код 404 для несуществующей задачи.");
    }

    @Test
    public void testUpdateTask() throws IOException, InterruptedException {
        int taskId = taskManager.addNewTask(new Task("Задача 1", "Описание 1", TaskStatus.NEW,
                LocalDateTime.now(), Duration.ofMinutes(30)));
        Task updatedTask = new Task(taskId, "Обновленная задача", "Обновленное описание",
                TaskStatus.IN_PROGRESS, LocalDateTime.now(), Duration.ofMinutes(45));
        String taskJson = gson.toJson(updatedTask);

        HttpResponse<String> response = sendPostRequest("http://localhost:8080/tasks", taskJson);
        assertEquals(201, response.statusCode(), "Неверный код ответа при обновлении задачи.");

        Task task = taskManager.getTask(taskId);
        assertNotNull(task, "Задача не должна быть null.");
        assertEquals("Обновленная задача", task.getName(), "Имя задачи не совпадает после обновления.");
    }

    @Test
    public void testDeleteTaskById() throws IOException, InterruptedException {
        int taskId = taskManager.addNewTask(new Task("Задача 1", "Описание 1",
                TaskStatus.NEW, LocalDateTime.now(), Duration.ofMinutes(30)));

        HttpResponse<String> response = sendDeleteRequest("http://localhost:8080/tasks/" + taskId);
        assertEquals(204, response.statusCode(), "Неверный код ответа при удалении задачи.");

        Task task = taskManager.getTask(taskId);
        assertNull(task, "Задача должна быть null после удаления.");
    }

    @Test
    public void testDeleteTaskByIdNotFound() throws IOException, InterruptedException {
        HttpResponse<String> response = sendDeleteRequest("http://localhost:8080/tasks/999");
        assertEquals(404, response.statusCode(), "Ожидается код 404 для удаления несуществующей задачи.");
    }
}
