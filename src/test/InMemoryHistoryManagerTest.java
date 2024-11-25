package test;

import history.HistoryManager;
import manager.Managers;
import org.junit.jupiter.api.Test;
import task.Epic;
import task.Subtask;
import task.Task;
import task.TaskStatus;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class InMemoryHistoryManagerTest {
    private final HistoryManager historyManager = Managers.getDefaultHistory();

    Task task = new Task("Первая", "Описание 1", TaskStatus.NEW);

    @Test
    void add() {
        Epic epic = new Epic("Первый эпик", "Описание 1");

        Subtask subtask = new Subtask(epic.getId(),
                "Первая саб-таска", "Описание1", TaskStatus.NEW);

        historyManager.add(task);
        historyManager.add(epic);
        historyManager.add(subtask);

        final List<Task> history = historyManager.getHistory();
        assertNotNull(history, "История не пустая.");
        assertEquals(3, history.size(), "Количество элементов не совпадает.");
        assertEquals(task, history.getFirst(), "Первой задачей в истории должна быть Task.");
        assertEquals(epic, history.get(1), "Вторым элементом в истории должен быть Epic.");
        assertEquals(subtask, history.getLast(), "Третьим элементом в истории должна быть Subtask.");
    }

    @Test
    public void shouldPreserveTaskDataInHistory() {
        historyManager.add(task);
        List<Task> history = historyManager.getHistory();
        Task taskFromHistory = history.getFirst();

        assertEquals("Первая", taskFromHistory.getName());
        assertEquals("Описание 1", taskFromHistory.getDescription());
        assertEquals(TaskStatus.NEW, taskFromHistory.getStatus());
    }
}