package managersTest;

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
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.Month;

import static org.junit.jupiter.api.Assertions.*;

public class FileBackedTaskManagerTest extends TaskManagerTest<FileBackedTaskManager> {

    @Override
    protected FileBackedTaskManager createTaskManager() {
        return new FileBackedTaskManager(null);
    }

    private static final Path FILE_PATH = Paths.get("autoSaveTest");
    private File file;
    private FileBackedTaskManager fileBackedTaskManager;

    private Task task1;
    private Task task2;
    private Task task3;
    private Epic epic1;
    private Epic epic2;
    private Subtask subtask1;
    private Subtask subtask2;
    private Subtask subtask3;

    private Task updatedTask;

    @BeforeEach
    void beforeEach() throws IOException {
        if (Files.exists(FILE_PATH)) {
            Files.writeString(FILE_PATH, "");
        } else {
            Files.createFile(FILE_PATH);
        }

        file = FILE_PATH.toFile();
        fileBackedTaskManager = new FileBackedTaskManager(FILE_PATH);

        task1 = new Task("Первая", "Описание 1", TaskStatus.NEW,
                LocalDateTime.of(2025, Month.JANUARY, 1, 13, 0), Duration.ofMinutes(120));
        task2 = new Task("Вторая", "Описание 2", TaskStatus.DONE,
                LocalDateTime.of(2025, Month.JANUARY, 1, 10, 0), Duration.ofMinutes(180));

        epic1 = new Epic("Первый эпик", "Описание 1");
        epic2 = new Epic("Второй эпик", "Описание 2");

        subtask1 = new Subtask(-1,
                "Первая подзадача", "Описание 1", TaskStatus.NEW,
                LocalDateTime.of(2025, Month.MARCH, 1, 12, 0), Duration.ofMinutes(120));
        subtask2 = new Subtask(-1,
                "Вторая подзадача", "Описание 2", TaskStatus.NEW,
                LocalDateTime.of(2025, Month.JANUARY, 26, 12, 0), Duration.ofMinutes(180));
        subtask3 = new Subtask(-1,
                "Третья подзадача", "Описание 3", TaskStatus.DONE,
                LocalDateTime.of(2025, Month.MARCH, 1, 20, 15), Duration.ofMinutes(150));

        updatedTask = new Task(-1,
                "Изменённое название", "Описание 1",
                TaskStatus.DONE,
                LocalDateTime.of(2025, Month.JANUARY, 1, 13, 0), Duration.ofMinutes(120));
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

        } catch (IOException exception) {
            fail("Не удалось выполнить тест: " + exception.getMessage());
        }
    }

    private void operationsWithTasksAndPopulateManager(FileBackedTaskManager fileBackedTaskManager) {
        int task1Id = fileBackedTaskManager.addNewTask(task1);
        int task2Id = fileBackedTaskManager.addNewTask(task2);
        int epic1Id = fileBackedTaskManager.addNewEpic(epic1);
        int epic2Id = fileBackedTaskManager.addNewEpic(epic2);

        task1 = new Task(task1Id, task1.getName(), task1.getDescription(), task1.getStatus(), task1.getStartTime(),
                task1.getDuration());
        task2 = new Task(task2Id, task2.getName(), task2.getDescription(), task2.getStatus(), task2.getStartTime(),
                task2.getDuration());
        subtask1 = new Subtask(epic1Id, subtask1.getName(), subtask1.getDescription(), subtask1.getStatus(),
                subtask1.getStartTime(), subtask1.getDuration());
        subtask2 = new Subtask(epic2Id, subtask2.getName(), subtask2.getDescription(), subtask2.getStatus(),
                subtask2.getStartTime(), subtask2.getDuration());
        subtask3 = new Subtask(epic2Id, subtask3.getName(), subtask3.getDescription(), subtask3.getStatus(),
                subtask3.getStartTime(), subtask3.getDuration());

        fileBackedTaskManager.addNewSubtask(subtask1);
        int subtask2Id = fileBackedTaskManager.addNewSubtask(subtask2);
        int subtask3Id = fileBackedTaskManager.addNewSubtask(subtask3);

        subtask2 = new Subtask(subtask2Id, epic2Id, subtask2.getName(), subtask2.getDescription(), subtask2.getStatus(),
                subtask2.getStartTime(), subtask2.getDuration());
        subtask3 = new Subtask(subtask3Id, epic2Id, subtask3.getName(), subtask3.getDescription(), subtask3.getStatus(),
                subtask3.getStartTime(), subtask3.getDuration());
        epic2 = fileBackedTaskManager.getEpic(epic2Id);

        updatedTask = new Task(task1Id,
                "Изменённое название", "Описание 1",
                TaskStatus.DONE,
                LocalDateTime.of(2025, Month.JANUARY, 1, 13, 0), Duration.ofMinutes(120));
        fileBackedTaskManager.updateTask(updatedTask);

        fileBackedTaskManager.deleteTaskById(fileBackedTaskManager.getTasks().getLast().getId());
        fileBackedTaskManager.deleteEpicById(fileBackedTaskManager.getEpics().getFirst().getId());

        task3 = new Task("Третья", "Описание 3", TaskStatus.DONE,
                LocalDateTime.of(2025, Month.JANUARY, 1, 10, 0), Duration.ofMinutes(180));
        int task3Id = fileBackedTaskManager.addNewTask(task3);
        task3 = new Task(task3Id, "Третья", "Описание 3", TaskStatus.DONE,
                LocalDateTime.of(2025, Month.JANUARY, 1, 10, 0), Duration.ofMinutes(180));
    }

    @Test
    void savingTasks() {
        operationsWithTasksAndPopulateManager(fileBackedTaskManager);

        assertEquals(2, fileBackedTaskManager.getTasks().size(), "Должны быть две задачи.");
        assertEquals(updatedTask, fileBackedTaskManager.getTasks().getFirst(), "Задача должна совпадать.");
        assertEquals(task3, fileBackedTaskManager.getTasks().getLast(), "Задача должна совпадать.");

        assertEquals(1, fileBackedTaskManager.getEpics().size(), "Количество эпиков должно совпадать.");
        assertEquals(epic2, fileBackedTaskManager.getEpics().getFirst(), "Эпик должен совпадать.");

        assertEquals(2, fileBackedTaskManager.getSubtasks().size(), "Количество подзадач должно совпадать.");
        assertEquals(subtask2, fileBackedTaskManager.getSubtasks().getFirst(), "Подзадача должна совпадать.");
        assertEquals(subtask3.getIdEpic(), epic2.getId(), "Подзадача не привязана.");
    }

    @Test
    void loadingTasks() {
        operationsWithTasksAndPopulateManager(fileBackedTaskManager);
        FileBackedTaskManager loadedManager = FileBackedTaskManager.loadFromFile(file);

        assertEquals(2, loadedManager.getTasks().size(), "Количество задач должно совпадать.");
        assertEquals(updatedTask, loadedManager.getTasks().getFirst(), "Задача должна совпадать.");
        assertEquals(task3, loadedManager.getTasks().getLast(), "Задача должна совпадать.");

        assertEquals(1, loadedManager.getEpics().size(), "Количество эпиков должно совпадать.");
        assertEquals(epic2, loadedManager.getEpics().getFirst(), "Эпик должен совпадать.");

        assertEquals(2, loadedManager.getSubtasks().size(), "Количество подзадач должно совпадать.");
        assertEquals(subtask2, loadedManager.getSubtasks().getFirst(), "Подзадача должна совпадать.");
        assertEquals(subtask2.getIdEpic(), epic2.getId(), "Подзадача не привязана.");
        assertEquals(subtask3, loadedManager.getSubtasks().getLast(), "Подзадача должна совпадать.");
        assertEquals(subtask3.getIdEpic(), epic2.getId(), "Подзадача не привязана.");
    }
}