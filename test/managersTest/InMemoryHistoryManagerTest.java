package managersTest;

import history.HistoryManager;
import manager.Managers;
import manager.TaskManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import task.Epic;
import task.Subtask;
import task.Task;
import task.TaskStatus;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.Month;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class InMemoryHistoryManagerTest {
    private final HistoryManager historyManager = Managers.getDefaultHistory();
    private final TaskManager taskManager = Managers.getDefault();
    private Task task;
    private Epic epic;
    private Subtask subtask;

    @BeforeEach
    void beforeEach() {
        task = new Task("Первая", "Описание 1", TaskStatus.NEW,
                LocalDateTime.of(2025, Month.JANUARY, 1, 13, 0), Duration.ofMinutes(120));
        epic = new Epic("Первый эпик", "Описание 1");
        subtask = new Subtask(-1,
                "Первая подзадача", "Описание 1", TaskStatus.NEW,
                LocalDateTime.of(2025, Month.MARCH, 1, 12, 0), Duration.ofMinutes(120));

        int taskId = taskManager.addNewTask(task);
        int epicId = taskManager.addNewEpic(epic);
        subtask = new Subtask(epicId,
                "Первая подзадача", "Описание 1", TaskStatus.NEW,
                LocalDateTime.of(2025, Month.MARCH, 1, 12, 0), Duration.ofMinutes(120));
        int subtaskId = taskManager.addNewSubtask(subtask);

        task = new Task(taskId, "Первая", "Описание 1", TaskStatus.NEW,
                LocalDateTime.of(2025, Month.JANUARY, 1, 13, 0), Duration.ofMinutes(120));
        epic = new Epic(epicId, "Первый эпик", "Описание 1");
        subtask = new Subtask(subtaskId, epicId,
                "Первая подзадача", "Описание 1", TaskStatus.NEW,
                LocalDateTime.of(2025, Month.MARCH, 1, 12, 0), Duration.ofMinutes(120));

        historyManager.add(task);
        historyManager.add(epic);
        historyManager.add(subtask);
    }

    @Test
    void add() {
        List<Task> history = historyManager.getHistory();
        assertNotNull(history, "История не пустая.");
        assertEquals(3, history.size(), "Количество элементов не совпадает.");
        assertEquals(task, history.getFirst(), "Первым элементом в истории должна быть Task.");
        assertEquals(epic, history.get(1), "Вторым элементом в истории должен быть Epic.");
        assertEquals(subtask, history.getLast(), "Третьим элементом в истории должна быть Subtask.");
    }

    @Test
    void shouldPreserveTaskDataInHistory() {
        List<Task> history = historyManager.getHistory();
        Task taskFromHistory = history.getFirst();

        assertEquals("Первая", taskFromHistory.getName());
        assertEquals("Описание 1", taskFromHistory.getDescription());
        assertEquals(TaskStatus.NEW, taskFromHistory.getStatus());
    }

    @Test
    void shouldCorrectlyDisplayHistoryAfterViewAgain() {
        historyManager.add(task);

        List<Task> history = historyManager.getHistory();
        assertNotNull(history, "История просмотров не должна быть null.");
        assertEquals(3, history.size(), "История должна содержать три задачи.");
        assertEquals(task.getName(), history.getLast().getName(),
                "Последняя просмотренная задача должна быть последней в истории.");
        assertEquals(epic.getName(), history.getFirst().getName(),
                "Первая просмотренная задача должна быть первой в истории.");
        assertEquals(subtask, history.get(1), "Вторым элементом в истории должна быть Subtask.");
    }

    @Test
    void shouldCorrectlyDisplayHistoryAfterDeletingTasks() {
        historyManager.remove(subtask.getId());
        historyManager.remove(epic.getId());
        List<Task> history = historyManager.getHistory();
        assertNotNull(history, "История просмотров не должна быть null.");
        assertEquals(1, history.size(), "Количество элементов не совпадает.");
        assertEquals(task, history.getFirst(), "Единственным элементом в истории должна быть Task.");
    }

    @Test
    void shouldDisplayEmptyHistory() {
        historyManager.remove(task.getId());
        historyManager.remove(epic.getId());
        historyManager.remove(subtask.getId());

        List<Task> history = historyManager.getHistory();
        assertEquals(0, history.size(), "История не пустая.");
    }
}