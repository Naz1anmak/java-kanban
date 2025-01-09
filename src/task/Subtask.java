package task;

import history.TasksTypes;

public class Subtask extends Task {
    private int idEpic;

    public Subtask(int idEpic, String name, String description, TaskStatus status) {
        super(name, description, status);
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
        return String.format("%d,%s,%s,%s,%s,%d",
                getId(), TasksTypes.SUBTASK, getName(),
                getStatus(), getDescription(), getIdEpic());
    }
}
