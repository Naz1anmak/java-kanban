package test;

import manager.HistoryManager;
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
    private HistoryManager historyManager;

    @BeforeEach
    public void beforeEach() {
        historyManager = Managers.getDefaultHistory();
    }

    @Test
    void add() {
        Task task = new Task("Первая", "Описание 1", TaskStatus.NEW);

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
        Task task = new Task("Задача", "Описание задачи", TaskStatus.NEW);

        historyManager.add(task);

        List<Task> history = historyManager.getHistory();
        Task taskFromHistory = history.getFirst();

        assertEquals("Задача", taskFromHistory.getName());
        assertEquals("Описание задачи", taskFromHistory.getDescription());
        assertEquals(TaskStatus.NEW, taskFromHistory.getStatus());
    }
}