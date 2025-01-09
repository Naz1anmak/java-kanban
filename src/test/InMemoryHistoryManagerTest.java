package test;

import history.HistoryManager;
import manager.Managers;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import task.Epic;
import task.Subtask;
import task.Task;
import task.TaskStatus;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class InMemoryHistoryManagerTest {
    private final HistoryManager historyManager = Managers.getDefaultHistory();
    private Task task;
    private Epic epic;
    private Subtask subtask;

    @BeforeEach
    void beforeEach() {
        task = new Task("Первая", "Описание 1", TaskStatus.NEW);
        epic = new Epic("Первый эпик", "Описание 1");
        subtask = new Subtask(epic.getId(),
                "Первая саб-таска", "Описание1", TaskStatus.NEW);

        task.setId(1);
        epic.setId(2);
        subtask.setId(3);

        subtask.setIdEpic(epic.getId());
        epic.addSubtaskId(subtask.getId());

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
        assertNotNull(history, "История не пустая.");
        assertEquals(3, history.size(), "Количество элементов не совпадает.");
        assertEquals(epic, history.getFirst(), "Первым элементом в истории должен быть Epic.");
        assertEquals(subtask, history.get(1), "Вторым элементом в истории должна быть Subtask.");
        assertEquals(task, history.getLast(), "Третьим элементом в истории должна быть Task.");
    }

    @Test
    void shouldCorrectlyDisplayHistoryAfterDeletingTasks() {
        /* Так как конструктор InMemoryTaskManager менять нельзя, чтобы он принимал экземпляр HistoryManager, делаем
        вручную то, что делает метод deleteEpicById() */
        historyManager.remove(subtask.getId());
        historyManager.remove(epic.getId());
        List<Task> history = historyManager.getHistory();
        assertNotNull(history, "История не пустая.");
        assertEquals(1, history.size(), "Количество элементов не совпадает.");
        assertEquals(task, history.getFirst(), "Единственным элементом в истории должна быть Task.");
    }
}