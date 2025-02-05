package task;

import history.TasksTypes;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Objects;

public class Task {
    private final String name;
    private final String description;
    private final int id;
    protected TaskStatus status;
    private final LocalDateTime startTime;
    private final Duration duration;
    private final LocalDateTime endTime;

    public Task(String name, String description, TaskStatus status, LocalDateTime startTime, Duration duration) {
        this(-1, name, description, status, startTime, duration);
    }

    public Task(int id, String name, String description, TaskStatus status,
                LocalDateTime startTime, Duration duration) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.status = status;
        this.startTime = startTime;
        this.duration = duration;
        this.endTime = (startTime != null && duration != null) ? startTime.plus(duration) : null;
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public TaskStatus getStatus() {
        return status;
    }

    public LocalDateTime getStartTime() {
        return startTime;
    }

    public Duration getDuration() {
        return duration;
    }

    public LocalDateTime getEndTime() {
        return endTime;
    }

    @Override
    public boolean equals(Object object) {
        if (this == object) return true;
        if (object == null || getClass() != object.getClass()) return false;
        Task task = (Task) object;
        return id == task.id;
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }

    @Override
    public String toString() {
        return String.format("%d,%s,%s,%s,%s,%s,%s",
                getId(), TasksTypes.TASK, getName(), getStatus(), getDescription(), getStartTime(), getDuration());
    }
}
