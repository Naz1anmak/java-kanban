package task;

import java.util.ArrayList;

public class Epic extends Task {
    private TaskStatus status;
    private ArrayList<Subtask> subtasks;


    public Epic(int idEpic, String name, String description) {
        super(idEpic, name, description);
        this.subtasks = new ArrayList<>();
        status = TaskStatus.NEW;
    }

    public ArrayList<Subtask> getSubtasks() {
        return subtasks;
    }

    public void addSubtask(Subtask subtask) {
        subtasks.add(subtask);
    }

    @Override
    public void setStatus(TaskStatus status) {
        this.status = status;
    }

    @Override
    public String toString() {
        return "Epic{" +
                "id=" + getId() +
                ", name='" + getName() + '\'' +
                ", description='" + getDescription() + '\'' +
                ", status=" + status +
                ", subtasks=" + subtasks +
                "}\n";
    }

}
