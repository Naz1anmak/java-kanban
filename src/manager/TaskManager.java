package manager;

import exception.TaskIntersectionException;
import task.Epic;
import task.Subtask;
import task.Task;

import java.util.List;

public interface TaskManager {
    int addNewTask(Task task) throws TaskIntersectionException;

    Task getTask(int id);

    List<Task> getTasks();

    void updateTask(Task newTask);

    void deleteTaskById(int id);

    void deleteAllTasks();

    int addNewEpic(Epic epic);

    Epic getEpic(int id);

    List<Epic> getEpics();

    void updateEpicFill(Epic newEpic);

    void deleteEpicById(int id);

    void deleteAllEpics();

    int addNewSubtask(Subtask subtask);

    Subtask getSubtask(int id);

    List<Subtask> getEpicSubtasks(int epicId);

    List<Subtask> getSubtasks();

    void updateSubtask(Subtask newSubtask);

    void deleteSubtaskById(int id);

    void deleteAllSubtasks();

    List<Task> getHistory();

    List<Task> getPrioritizedTasks();
}
