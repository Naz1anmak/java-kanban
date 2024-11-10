import java.util.ArrayList;

public class Epic extends Task {
    TaskStatus status = TaskStatus.NEW;
    ArrayList<Subtask> subtasks;

    public Epic(String name, String description) {
        super(name, description);
        this.subtasks = new ArrayList<>();
    }

    public ArrayList<Subtask> getSubtasks() {
        return subtasks;
    }

    public String getName() {
        return name;
    }

    @Override
    public String toString() {
        return "Epic{" +
                "name='" + name + '\'' +
                ", description='" + description + '\'' +
                ", status=" + status +
                ", subtasks=" + subtasks +
                "}\n";
    }
}
