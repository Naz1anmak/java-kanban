package serverTest;

import com.google.gson.Gson;
import manager.InMemoryTaskManager;
import manager.TaskManager;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import server.HttpTaskServer;
import task.Epic;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class EpicHandlerTest {

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
    public void testAddEpic() throws IOException, InterruptedException {
        Epic epic = new Epic("Тестовый эпик", "Описание эпика");
        String epicJson = gson.toJson(epic);

        HttpResponse<String> response = sendPostRequest("http://localhost:8080/epics", epicJson);
        assertEquals(201, response.statusCode(), "Неверный код ответа при добавлении эпика.");

        List<Epic> epics = taskManager.getEpics();
        assertNotNull(epics, "Список эпиков не должен быть пуст.");
        assertEquals(1, epics.size(), "Количество эпиков должно быть равно 1.");
        assertEquals("Тестовый эпик", epics.get(0).getName(), "Имя эпика не совпадает.");
    }

    @Test
    public void testGetAllEpics() throws IOException, InterruptedException {
        taskManager.addNewEpic(new Epic("Эпик 1", "Описание 1"));
        taskManager.addNewEpic(new Epic("Эпик 2", "Описание 2"));

        HttpResponse<String> response = sendGetRequest("http://localhost:8080/epics");
        assertEquals(200, response.statusCode(), "Неверный код ответа при запросе всех эпиков.");

        List<Epic> epics = gson.fromJson(response.body(), List.class);
        assertNotNull(epics, "Список эпиков не должен быть пуст.");
        assertEquals(2, epics.size(), "Количество эпиков должно быть равно 2.");
    }

    @Test
    public void testGetEpicById() throws IOException, InterruptedException {
        int epicId = taskManager.addNewEpic(new Epic("Эпик 1", "Описание 1"));

        HttpResponse<String> response = sendGetRequest("http://localhost:8080/epics/" + epicId);
        assertEquals(200, response.statusCode(), "Неверный код ответа при запросе эпика по ID.");

        Epic epic = gson.fromJson(response.body(), Epic.class);
        assertNotNull(epic, "Эпик не должен быть null.");
        assertEquals("Эпик 1", epic.getName(), "Имя эпика не совпадает.");
    }

    @Test
    public void testGetEpicByIdNotFound() throws IOException, InterruptedException {
        HttpResponse<String> response = sendGetRequest("http://localhost:8080/epics/999");
        assertEquals(404, response.statusCode(), "Ожидается код 404 для несуществующего эпика.");
    }

    @Test
    public void testUpdateEpic() throws IOException, InterruptedException {
        int epicId = taskManager.addNewEpic(new Epic("Эпик 1", "Описание 1"));
        Epic updatedEpic = new Epic(epicId, "Обновленный эпик", "Обновленное описание");
        String epicJson = gson.toJson(updatedEpic);

        HttpResponse<String> response = sendPostRequest("http://localhost:8080/epics", epicJson);
        assertEquals(201, response.statusCode(), "Неверный код ответа при обновлении эпика.");

        Epic epic = taskManager.getEpic(epicId);
        assertNotNull(epic, "Эпик не должен быть null.");
        assertEquals("Обновленный эпик", epic.getName(), "Имя эпика не совпадает после обновления.");
    }

    @Test
    public void testDeleteEpicById() throws IOException, InterruptedException {
        int epicId = taskManager.addNewEpic(new Epic("Эпик 1", "Описание 1"));

        HttpResponse<String> response = sendDeleteRequest("http://localhost:8080/epics/" + epicId);
        assertEquals(204, response.statusCode(), "Неверный код ответа при удалении эпика.");

        Epic epic = taskManager.getEpic(epicId);
        assertNull(epic, "Эпик должен быть null после удаления.");
    }

    @Test
    public void testDeleteEpicByIdNotFound() throws IOException, InterruptedException {
        HttpResponse<String> response = sendDeleteRequest("http://localhost:8080/epics/999");
        assertEquals(404, response.statusCode(), "Ожидается код 404 для удаления несуществующего эпика.");
    }
}
