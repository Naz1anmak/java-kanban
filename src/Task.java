import java.util.Objects;

public class Task {
    String name;
    String description;
    TaskStatus status = TaskStatus.NEW;

    public Task(String name, String description) {
        this.name = name;
        this.description = description;
    }

    @Override
    public boolean equals(Object object) {
        if (this == object) return true;
        if (object == null || getClass() != object.getClass()) return false;
        Task task = (Task) object;
        return Objects.equals(name, task.name) &&
                Objects.equals(description, task.description) &&
                Objects.equals(status, task.status);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, description, status);
    }

    @Override
    public String toString() {
        return "Task{" +
                "name='" + name + '\'' +
                ", description='" + description + '\'' +
                ", status=" + status +
                "}\n";
    }

    public static int generateId(Task task) {
        return task.hashCode();
    }
}
