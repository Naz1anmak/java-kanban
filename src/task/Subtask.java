package task;

public class Subtask extends Task {
    private int idEpic;

    public Subtask(int idEpic, String name, String description, TaskStatus status) {
        super(name, description);
        this.status = status;
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
        return "\n    Subtask{" +
                "id=" + getId() +
                ", name='" + getName() + '\'' +
                ", description='" + getDescription() + '\'' +
                ", status=" + status +
                '}';
    }
}
