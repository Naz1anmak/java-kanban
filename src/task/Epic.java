package task;

import history.TasksTypes;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public final class Epic extends Task {
    private final List<Integer> subtaskIds = new ArrayList<>();

    public Epic(String name, String description) {
        super(name, description, TaskStatus.NEW, null, null);
    }

    public Epic(int id, String name, String description) {
        super(id, name, description, TaskStatus.NEW, null, null);
    }

    public Epic(int id, String name, String description, TaskStatus status,
                LocalDateTime startTime, Duration duration) {
        super(id, name, description, status, startTime, duration);
    }

    public List<Integer> getSubtaskIds() {
        return new ArrayList<>(subtaskIds);
    }

    public void setStatus(TaskStatus status) {
        this.status = status;
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
