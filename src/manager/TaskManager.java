package manager;

import task.Epic;
import task.Subtask;
import task.Task;
import task.TaskStatus;

import java.util.ArrayList;
import java.util.HashMap;

public class TaskManager {
    private int idCounter = 1;
    private final HashMap<Integer, Task> tasks = new HashMap<>();
    private final HashMap<Integer, Epic> epics = new HashMap<>();
    private final HashMap<Integer, Subtask> subtasks = new HashMap<>();

    public void addNewTask(Task task) {
        int taskId = idCounter++;
        task.setId(taskId);
        tasks.put(task.getId(), task);
        System.out.println("Задача \"" + task.getName() + "\" с id=" + taskId + " добавлена!");
    }

    public ArrayList<Task> getTasks() {
        return new ArrayList<Task>(tasks.values());
    }

    public void updateTask(Task task) {
        tasks.put(task.getId(), task);
        System.out.println("Задача c id=" + task.getId() + " обновлена!");
    }

    public Task findTask(int id) {
        return tasks.get(id);
    }

    public void deleteTaskById(int id) {
        Task task = tasks.remove(id);
        String name = task.getName();
        System.out.println("Задача \"" + name + "\" удалена!");
    }

    public void deleteTasks() {
        tasks.clear();
        System.out.println("Все задачи удалены!");
    }

    public void addNewEpic(Epic epic) {
        int epicId = idCounter++;
        epic.setId(epicId);
        epics.put(epicId, epic);
        System.out.println("Эпик \"" + epic.getName() + "\" с id=" + epicId + " добавлен!");
    }

    public ArrayList<Epic> getEpics() {
        return new ArrayList<Epic>(epics.values());
    }

    public void updateEpicFill(Epic newEpic) {
        Epic oldEpic = epics.get(newEpic.getId());

        oldEpic.setName(newEpic.getName());
        oldEpic.setDescription(newEpic.getDescription());

        System.out.println("Эпик c id=" + newEpic.getId() + " обновлен!");
    }

    private void updateEpicStatus(Epic epic) {
        int countNew = 0;
        int countDone = 0;

        for (int subtaskId : epic.getSubtaskIds()) {
            Subtask subtask = subtasks.get(subtaskId);
            if (subtask.getStatus() == TaskStatus.NEW) {
                countNew++;
            } else if (subtask.getStatus() == TaskStatus.DONE) {
                countDone++;
            }
        }
        if (countNew == epic.getSubtaskIds().size()) epic.setStatus(TaskStatus.NEW);
        else if (countDone == epic.getSubtaskIds().size()) epic.setStatus(TaskStatus.DONE);
        else epic.setStatus(TaskStatus.IN_PROGRESS);
    }

    public Epic findEpic(int id) {
        return epics.get(id);
    }

    public void deleteEpicById(int id) {
        for (int subtaskId  : epics.get(id).getSubtaskIds()) {
            subtasks.remove(subtaskId);
        }
        String name = epics.get(id).getName();
        epics.remove(id);

        System.out.println("Эпик \"" + name + "\" и его саб-задачи удалены!");
    }

    public void deleteAllEpics() {
        subtasks.clear();
        epics.clear();
        System.out.println("Все эпики и их саб-задачи удалены!");
    }

    public void addNewSubtask(Subtask subtask) {
        int subtaskId = idCounter++;
        subtask.setId(subtaskId);
        subtasks.put(subtaskId, subtask);

        Epic epic = epics.get(subtask.getIdEpic());
        if (epic != null) {
            epic.addSubtaskId(subtaskId);
            updateEpicStatus(epic);
            System.out.println("Саб-задача \"" + subtask.getName() + "\" с id=" + subtaskId + " добавлена!");
        } else {
            System.out.println("Эпик с id " + subtask.getIdEpic() + " не найден.");
        }
    }

    public ArrayList<Subtask> getAllLinkedSubtasks(int epicId) {
        Epic epic = epics.get(epicId);

        if (epic == null) {
            System.out.println("Эпик с id " + epicId + " не найден.");
            return new ArrayList<>();
        }

        ArrayList<Subtask> subtasksInEpic = new ArrayList<>();
        for (int subtaskId : epic.getSubtaskIds()) {
            Subtask subtask = subtasks.get(subtaskId);
            if (subtask != null) {
                subtasksInEpic.add(subtask);
            }
        }

        if (subtasks.isEmpty()) {
            System.out.println("В этом эпике саб-задач нет!");
        } else {
            System.out.print("Эпик \"" + epic.getName() + "\" содержит " + epic.getSubtaskIds().size() + " саб-таски: ");
        }
        return subtasksInEpic;
    }

    public ArrayList<Subtask> getSubtasks() {
        return new ArrayList<Subtask>(subtasks.values());
    }

    public void updateSub(Subtask oldSubtask, Subtask newSubtask) {
        int id = oldSubtask.getId();
        newSubtask.setId(id);
        newSubtask.setIdEpic(oldSubtask.getIdEpic());
        subtasks.put(id, newSubtask);

        Epic epic = epics.get(newSubtask.getIdEpic());
        if (epic == null) {
            System.out.println("Эпик с id " + newSubtask.getIdEpic() + " не найден.");
            return;
        }

        updateEpicStatus(epic);

        System.out.println("Саб-задача c id=" + newSubtask.getId() + " обновлена!");
    }

    public Subtask findSub(int id) {
        return subtasks.get(id);
    }

    public void deleteSubtaskById(int id) {
        Subtask subtask = subtasks.remove(id);
        if (subtask != null) {
            Epic epic = epics.get(subtask.getIdEpic());
            if (epic != null) {
                epic.removeSubtaskId(id);
                updateEpicStatus(epic);
            }
            System.out.println("Саб-задача \"" + subtask.getName() + "\" удалена!");
        } else {
            System.out.println("Саб-задача с id " + id + " не найдена.");
        }
    }

    public void deleteAllSubtasks() {
        subtasks.clear();
        for (Epic epic : epics.values()) {
            epic.getSubtaskIds().clear();
            updateEpicStatus(epic);
        }
        System.out.println("Все саб-задачи удалены!");
    }
}
