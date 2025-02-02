package task;

import history.TasksTypes;

import java.time.Duration;
import java.time.LocalDateTime;

public class Subtask extends Task {
    private int idEpic;

    public Subtask(int idEpic, String name, String description, TaskStatus status,
                   LocalDateTime startTime, Duration duration) {
        super(name, description, status, startTime, duration);
        this.idEpic = idEpic;
    }

    public int getIdEpic() {
        return idEpic;
    }

    public void setIdEpic(int newIdEpic) {
        this.idEpic = newIdEpic;
    }

    @Override
    public String toString() {
        return String.format("%d,%s,%s,%s,%s,%d,%s,%s",
                getId(), TasksTypes.SUBTASK, getName(), getStatus(), getDescription(),
                getIdEpic(), getStartTime(), getDuration());
    }
}
