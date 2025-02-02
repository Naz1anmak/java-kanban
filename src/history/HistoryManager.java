package history;

import task.Task;

import java.util.List;

public interface HistoryManager {
    void add(Task task);

    void remove(int id);

    List<Task> getHistory();

    List<Task> getPrioritizedTasks();

    void addToPrioritizedTasks(Task task);

    void removePrioritizedTask(Task oldTask);

    boolean isIntersectionTasks(Task newTask);

    boolean isIntersectionTasks(Task newTask, int idAddedTask);
}
