package manager;

import task.Task;

import java.util.ArrayList;
import java.util.List;

public class InMemoryHistoryManager implements HistoryManager {
    private final List<Task> history = new ArrayList<>(10);

    public void add(Task task) {
        if (history.size() == 10) {
            history.removeFirst();
            history.add(task);
            return;
        }
        history.add(task);
    }

    @Override
    public List<Task> getHistory() {
        System.out.println("История просмотров: ");
        return history;
    }
}
