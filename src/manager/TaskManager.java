package manager;

import task.Epic;
import task.Subtask;
import task.Task;

import java.util.ArrayList;

public interface TaskManager {
    int addNewTask(Task task);

    Task getTask(int id);

    ArrayList<Task> getTasks();

    void updateTask(Task task);

    void deleteTaskById(int id);

    void deleteTasks();

    int addNewEpic(Epic epic);

    Epic getEpic(int id);

    ArrayList<Epic> getEpics();

    void updateEpicFill(Epic newEpic);

    void updateEpicStatus(Epic epic);

    void deleteEpicById(int id);

    void deleteAllEpics();

    int addNewSubtask(Subtask subtask);

    Subtask getSubtask(int id);

    ArrayList<Subtask> getAllLinkedSubtasks(int epicId);

    ArrayList<Subtask> getSubtasks();

    void updateSub(Subtask oldSubtask, Subtask newSubtask);

    void deleteSubtaskById(int id);

    void deleteAllSubtasks();

}
