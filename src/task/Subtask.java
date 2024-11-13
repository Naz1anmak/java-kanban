package task;

public class Subtask extends Task {
    private final int idEpic;
    private TaskStatus status;

    public Subtask(int idEpic, String name, String description, TaskStatus status) {
        super(idEpic, name, description);
        this.status = status;
        this.idEpic = idEpic;
    }

    public Subtask(int idEpic, int id, String name, String description, TaskStatus status) {
        super(id, name, description);
        this.status = status;
        this.idEpic = idEpic;
    }

    public int getIdEpic() {
        return idEpic;
    }

    @Override
    public TaskStatus getStatus() {
        return status;
    }

    @Override
    public String toString() {
        return "\n    Subtask{" +
                "id=" + getId() +
                ", name='" + getName() + '\'' +
                ", description='" + getDescription() + '\'' +
                ", status=" + status +
                '}';
    }
}
