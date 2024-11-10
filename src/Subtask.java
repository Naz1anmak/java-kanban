public class Subtask extends Task {
    private int epicId;
    private int id;

    public Subtask(int epicId, String name, String description) {
        super(name, description);
        this.epicId = epicId;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getId() {
        return id;
    }

    public int getEpicId() {
        return epicId;
    }

    @Override
    public String toString() {
        return "\n    Subtask{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", description='" + description + '\'' +
                ", status=" + status +
                '}';
    }
}
