package test;

import manager.FileBackedTaskManager;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import task.Epic;
import task.Subtask;
import task.Task;
import task.TaskStatus;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import static org.junit.jupiter.api.Assertions.*;

public class FileBackedTaskManagerTest {
    private static final Path FILE_PATH = Paths.get("test/autoSaveTest");
    private File file;
    private FileBackedTaskManager fileBackedTaskManager;

    private Task updatedTask;
    private Task task5;
    private Epic epic2;
    private Subtask subtask3;

    @BeforeEach
    void beforeEach() throws IOException {
        if (Files.exists(FILE_PATH)) {
            Files.writeString(FILE_PATH, "");
        } else {
            Files.createFile(FILE_PATH);
        }

        file = FILE_PATH.toFile();
        fileBackedTaskManager = new FileBackedTaskManager(FILE_PATH);
    }

    @AfterAll
    static void afterAll() throws IOException {
        Files.delete(FILE_PATH);
    }

    @Test
    void creatingAndUploadingEmptyFile() {
        try {
            assertNotNull(fileBackedTaskManager,
                    "Метод loadFromFile() должен возвращать " +
                            "проинициализированный экземпляр FileBackedTaskManager.");

            String fileContent = Files.readString(file.toPath());
            assertTrue(fileContent.isEmpty(), "Файл должен быть пустым.");

        } catch (IOException e) {
            fail("Не удалось выполнить тест: " + e.getMessage());
        }
    }

    private void operationsWithTasksAndPopulateManager(FileBackedTaskManager fileBackedTaskManager) {
        Task task1 = new Task("Первая", "Описание 1", TaskStatus.NEW);
        int task1Id = fileBackedTaskManager.addNewTask(task1);
        task1.setId(task1Id);
        Task task2 = new Task("Вторая", "Описание 2", TaskStatus.DONE);
        fileBackedTaskManager.addNewTask(task2);
        Epic epic1 = new Epic("Первый эпик", "Описание 1");
        fileBackedTaskManager.addNewEpic(epic1);
        epic2 = new Epic("Второй эпик", "Описание 2");
        int epic2Id = fileBackedTaskManager.addNewEpic(epic2);
        epic2.setId(epic2Id);
        Subtask subtask1 = new Subtask(fileBackedTaskManager.getEpics().getFirst().getId(),
                "Первая саб-таска", "Описание1", TaskStatus.NEW);
        fileBackedTaskManager.addNewSubtask(subtask1);
        Subtask subtask2 = new Subtask(fileBackedTaskManager.getEpics().getFirst().getId(),
                "Вторая саб-таска", "Описание2", TaskStatus.NEW);
        fileBackedTaskManager.addNewSubtask(subtask2);
        subtask3 = new Subtask(fileBackedTaskManager.getEpics().getLast().getId(),
                "Третья саб-таска", "Описание3", TaskStatus.NEW);
        int subtask3Id = fileBackedTaskManager.addNewSubtask(subtask3);
        subtask3.setId(subtask3Id);
        epic2.addSubtaskId(subtask3Id);

        updatedTask = new Task(fileBackedTaskManager.getTasks().getFirst().getId(),
                "Четвертая", "Обновленная", TaskStatus.DONE);
        fileBackedTaskManager.updateTask(updatedTask);

        fileBackedTaskManager.deleteTaskById(fileBackedTaskManager.getTasks().getLast().getId());
        fileBackedTaskManager.deleteEpicById(fileBackedTaskManager.getEpics().getFirst().getId());

        task5 = new Task("Пятая", "Описание 5", TaskStatus.NEW);
        int task5Id = fileBackedTaskManager.addNewTask(task5);
        task5.setId(task5Id);
    }

    @Test
    void savingTasks() {
        operationsWithTasksAndPopulateManager(fileBackedTaskManager);

        assertEquals(2, fileBackedTaskManager.getTasks().size(), "Должны быть две задачи.");
        assertEquals(updatedTask, fileBackedTaskManager.getTasks().getFirst(), "Задача должна совпадать.");
        assertEquals(task5, fileBackedTaskManager.getTasks().getLast(), "Задача должна совпадать.");

        assertEquals(1, fileBackedTaskManager.getEpics().size(), "Количество эпиков должно совпадать.");
        assertEquals(epic2, fileBackedTaskManager.getEpics().getFirst(), "Эпик должен совпадать.");

        assertEquals(1, fileBackedTaskManager.getSubtasks().size(), "Количество сабтасок должно совпадать.");
        assertEquals(subtask3, fileBackedTaskManager.getSubtasks().getFirst(), "Сабтаска должна совпадать.");
        assertEquals(subtask3.getIdEpic(), epic2.getId(), "Сабтаска не привязана.");
    }

    @Test
    void loadingTasks() {
        operationsWithTasksAndPopulateManager(fileBackedTaskManager);
        FileBackedTaskManager loadedManager = FileBackedTaskManager.loadFromFile(file);

        assertEquals(2, loadedManager.getTasks().size(), "Количество задач должно совпадать.");
        assertEquals(updatedTask, loadedManager.getTasks().getFirst(), "Задача должна совпадать.");
        assertEquals(task5, loadedManager.getTasks().getLast(), "Задача должна совпадать.");

        assertEquals(1, loadedManager.getEpics().size(), "Количество эпиков должно совпадать.");
        assertEquals(epic2, loadedManager.getEpics().getFirst(), "Эпик должен совпадать.");

        assertEquals(1, loadedManager.getSubtasks().size(), "Количество сабтасок должно совпадать.");
        assertEquals(subtask3, loadedManager.getSubtasks().getFirst(), "Сабтаска должна совпадать.");
        assertEquals(subtask3.getIdEpic(), epic2.getId(), "Сабтаска не привязана.");
    }
}