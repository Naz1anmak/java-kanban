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

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

public class HistoryHandlerTest {

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

    @Test
    public void testGetHistory() throws IOException, InterruptedException {
        Task task1 = new Task("Задача 1", "Описание 1", TaskStatus.NEW,
                LocalDateTime.now(), Duration.ofMinutes(30));
        Task task2 = new Task("Задача 2", "Описание 2", TaskStatus.IN_PROGRESS,
                LocalDateTime.now().plusHours(1), Duration.ofMinutes(45));
        int taskId1 = taskManager.addNewTask(task1);
        int taskId2 = taskManager.addNewTask(task2);

        taskManager.getTask(taskId1);
        taskManager.getTask(taskId2);

        HttpResponse<String> response = client.send(
                HttpRequest.newBuilder().uri(URI.create("http://localhost:8080/history")).GET().build(),
                HttpResponse.BodyHandlers.ofString()
        );

        assertEquals(200, response.statusCode(), "Неверный код ответа при запросе истории задач.");

        List<Task> history = gson.fromJson(response.body(), List.class);
        assertNotNull(history, "История задач не должна быть пустой.");
        assertEquals(2, history.size(), "История должна содержать 2 задачи.");
    }

    @Test
    public void testGetEmptyHistory() throws IOException, InterruptedException {
        HttpResponse<String> response = client.send(
                HttpRequest.newBuilder().uri(URI.create("http://localhost:8080/history")).GET().build(),
                HttpResponse.BodyHandlers.ofString()
        );

        assertEquals(204, response.statusCode(), "Неверный код ответа при запросе пустой истории.");
    }

    @Test
    public void testGetPrioritizedTasks() throws IOException, InterruptedException {
        Task task1 = new Task("Задача 1", "Описание 1", TaskStatus.NEW,
                LocalDateTime.now().plusHours(2), Duration.ofMinutes(30));
        Task task2 = new Task("Задача 2", "Описание 2", TaskStatus.NEW,
                LocalDateTime.now().plusHours(1), Duration.ofMinutes(45));
        taskManager.addNewTask(task1);
        taskManager.addNewTask(task2);

        HttpResponse<String> response = client.send(
                HttpRequest.newBuilder().uri(URI.create("http://localhost:8080/prioritized")).GET().build(),
                HttpResponse.BodyHandlers.ofString()
        );

        assertEquals(200, response.statusCode(), "Неверный код ответа при запросе приоритезированных задач.");

        List<Task> prioritizedTasks = gson.fromJson(response.body(), List.class);
        assertNotNull(prioritizedTasks, "Список приоритезированных задач не должен быть пустым.");
        assertEquals(2, prioritizedTasks.size(), "Количество приоритезированных задач должно быть равно 2.");
    }

    @Test
    public void testGetEmptyPrioritizedTasks() throws IOException, InterruptedException {
        HttpResponse<String> response = client.send(
                HttpRequest.newBuilder().uri(URI.create("http://localhost:8080/prioritized")).GET().build(),
                HttpResponse.BodyHandlers.ofString()
        );

        assertEquals(204, response.statusCode(), "Неверный код ответа при запросе пустого списка " +
                "приоритезированных задач.");
    }
}
