package task;

import com.google.gson.annotations.Expose;
import history.TasksTypes;

import java.time.Duration;
import java.time.LocalDateTime;

public final class Subtask extends Task {
    @Expose
    private final int idEpic;

    public Subtask(int idEpic, String name, String description, TaskStatus status,
                   LocalDateTime startTime, Duration duration) {
        super(name, description, status, startTime, duration);
        this.idEpic = idEpic;
    }

    public Subtask(int id, int idEpic, String name, String description, TaskStatus status,
                   LocalDateTime startTime, Duration duration) {
        super(id, name, description, status, startTime, duration);
        this.idEpic = idEpic;
    }

    public int getIdEpic() {
        return idEpic;
    }

    @Override
    public String toString() {
        return String.format("%d,%s,%s,%s,%s,%d,%s,%s",
                getId(), TasksTypes.SUBTASK, getName(), getStatus(), getDescription(),
                getIdEpic(), getStartTime(), getDuration());
    }
}
