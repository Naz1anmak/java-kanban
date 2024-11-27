package manager;

import history.HistoryManager;
import task.Task;

import java.util.LinkedList;
import java.util.List;

public class InMemoryHistoryManager implements HistoryManager {
    private final List<Task> history = new LinkedList<>();
    private static final int MAX_HISTORY_SIZE = 10;

    public void add(Task task) {
        history.add(task);

        if (history.size() > MAX_HISTORY_SIZE) {
            history.removeFirst();
        }
    }

    @Override
    public List<Task> getHistory() {
        System.out.println("История просмотров: ");
        return List.copyOf(history);
    }
}
