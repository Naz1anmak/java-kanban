package task;

import history.TasksTypes;

import java.util.ArrayList;
import java.util.List;

public class Epic extends Task {
    private final List<Integer> subtaskIds = new ArrayList<>();

    public Epic(String name, String description) {
        super(name, description);
        this.setStatus(TaskStatus.NEW);
    }

    public List<Integer> getSubtaskIds() {
        return List.copyOf(subtaskIds);
    }

    public void addSubtaskId(int subtaskId) {
        subtaskIds.add(subtaskId);
    }

    public void removeSubtaskId(int subtaskId) {
        subtaskIds.remove(Integer.valueOf(subtaskId));
    }

    @Override
    public String toString() {
        return String.format("%d,%s,%s,%s,%s,%s,%s",
                getId(), TasksTypes.EPIC, getName(), getStatus(), getDescription(),
                getStartTime(), getDuration());
    }
}
