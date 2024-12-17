package manager;

import task.Epic;
import task.Subtask;
import task.Task;

import java.util.List;

public interface TaskManager {
    int addNewTask(Task task);

    Task getTask(int id);

    List<Task> getTasks();

    void updateTask(Task task);

    void deleteTaskById(int id);

    void deleteTasks();

    int addNewEpic(Epic epic);

    Epic getEpic(int id);

    List<Epic> getEpics();

    void updateEpicFill(Epic newEpic);

    void updateEpicStatus(Epic epic);

    void deleteEpicById(int id);

    void deleteAllEpics();

    int addNewSubtask(Subtask subtask);

    Subtask getSubtask(int id);

    List<Subtask> getAllLinkedSubtasks(int epicId);

    List<Subtask> getSubtasks();

    void updateSub(Subtask oldSubtask, Subtask newSubtask);

    void deleteSubtaskById(int id);

    void deleteAllSubtasks();

    List<Task> getHistory();
}
