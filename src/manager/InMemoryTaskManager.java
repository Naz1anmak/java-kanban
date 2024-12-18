package manager;

import history.HistoryManager;
import task.Epic;
import task.Subtask;
import task.Task;
import task.TaskStatus;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class InMemoryTaskManager implements TaskManager {
    private int idCounter = 1;
    private final Map<Integer, Task> tasks = new HashMap<>();
    private final Map<Integer, Epic> epics = new HashMap<>();
    private final Map<Integer, Subtask> subtasks = new HashMap<>();
    private final HistoryManager historyManager = Managers.getDefaultHistory();

    @Override
    public int addNewTask(Task task) {
        int taskId = idCounter++;
        Task taskCopy = new Task(task.getName(), task.getDescription(), task.getStatus());
        taskCopy.setId(taskId);
        tasks.put(taskId, taskCopy);
        System.out.println("Задача \"" + taskCopy.getName() + "\" с id=" + taskId + " добавлена!");
        return taskId;
    }

    @Override
    public Task getTask(int id) {
        Task task = tasks.get(id);

        if (task != null) {
            historyManager.add(task);
            return new Task(task.getName(), task.getDescription(), task.getStatus());
        }

        return null;
    }

    @Override
    public List<Task> getTasks() {
        return new ArrayList<>(tasks.values());
    }

    @Override
    public void updateTask(Task task) {
        tasks.put(task.getId(), task);
        System.out.println("Задача c id=" + task.getId() + " обновлена!");
    }

    @Override
    public void deleteTaskById(int id) {
        Task task = tasks.remove(id);
        String name = task.getName();
        historyManager.remove(id);
        System.out.println("Задача \"" + name + "\" удалена!");
    }

    @Override
    public void deleteTasks() {
        tasks.clear();
        System.out.println("Все задачи удалены!");
    }

    @Override
    public int addNewEpic(Epic epic) {
        int epicId = idCounter++;
        Epic epicCopy = new Epic(epic.getName(), epic.getDescription());
        epicCopy.setId(epicId);
        epics.put(epicId, epicCopy);
        System.out.println("Эпик \"" + epicCopy.getName() + "\" с id=" + epicId + " добавлен!");
        return epicId;
    }

    @Override
    public Epic getEpic(int id) {
        Epic epic = epics.get(id);

        if (epic != null) {
            historyManager.add(epic);
            Epic epicCopy = new Epic(epic.getName(), epic.getDescription());
            epicCopy.setId(epic.getId());
            epicCopy.getSubtaskIds().addAll(epic.getSubtaskIds());
            return epicCopy;
        }

        return null;
    }

    @Override
    public List<Epic> getEpics() {
        return new ArrayList<>(epics.values());
    }

    @Override
    public void updateEpicFill(Epic newEpic) {
        Epic oldEpic = epics.get(newEpic.getId());

        if (oldEpic != null) {
            oldEpic.setName(newEpic.getName());
            oldEpic.setDescription(newEpic.getDescription());

            System.out.println("Эпик c id=" + newEpic.getId() + " обновлен!");
        }
    }

    @Override
    public void updateEpicStatus(Epic epic) {
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

    @Override
    public void deleteEpicById(int id) {
        Epic epic = epics.get(id);
        if (epic == null) return;

        List<Integer> subtasksIds = new ArrayList<>(epic.getSubtaskIds());

        for (int subtaskId : subtasksIds) {
            deleteSubtaskById(subtaskId);
        }

        String name = epics.get(id).getName();
        epics.remove(id);
        historyManager.remove(id);

        System.out.println("Эпик \"" + name + "\" и его саб-задачи удалены!");
    }

    @Override
    public void deleteAllEpics() {
        subtasks.clear();
        epics.clear();
        System.out.println("Все эпики и их саб-задачи удалены!");
    }

    @Override
    public int addNewSubtask(Subtask subtask) {
        Epic epic = epics.get(subtask.getIdEpic());
        if (epic == null) {
            System.out.println("Эпик с id " + subtask.getIdEpic() + " не найден.");
            return -1;
        }

        if (epic.getId() == subtask.getId()) {
            System.out.println("Эпик не может быть добавлен в качестве своей же подзадачи.");
            return -1;
        }

        if (subtask.getIdEpic() == subtask.getId()) {
            System.out.println("Саб-задача не может быть своим же эпиком. Саб-задача не добавлена.");
            return -1;
        }

        int subtaskId = idCounter++;
        Subtask subtaskCopy =
                new Subtask(subtask.getIdEpic(), subtask.getName(), subtask.getDescription(), subtask.getStatus());
        subtaskCopy.setId(subtaskId);
        subtasks.put(subtaskId, subtaskCopy);
        epic.addSubtaskId(subtaskId);
        updateEpicStatus(epic);

        System.out.println("Саб-задача \"" + subtaskCopy.getName() + "\" с id=" + subtaskId + " добавлена!");
        return subtaskId;
    }

    @Override
    public Subtask getSubtask(int id) {
        Subtask subtask = subtasks.get(id);

        if (subtask != null) {
            historyManager.add(subtask);
            return new Subtask(subtask.getIdEpic(), subtask.getName(), subtask.getDescription(), subtask.getStatus());
        }

        return null;
    }

    @Override
    public List<Subtask> getAllLinkedSubtasks(int epicId) {
        Epic epic = epics.get(epicId);

        if (epic == null) {
            System.out.println("Эпик с id " + epicId + " не найден.");
            return new ArrayList<>();
        }

        List<Subtask> subtasksInEpic = new ArrayList<>();
        for (int subtaskId : epic.getSubtaskIds()) {
            Subtask subtask = subtasks.get(subtaskId);
            if (subtask != null) {
                subtasksInEpic.add(subtask);
            }
        }

        if (subtasks.isEmpty()) {
            System.out.println("В этом эпике саб-задач нет!");
        } else {
            System.out.print("Содержание эпика \"" + epic.getName() + "\": ");
        }
        return subtasksInEpic;
    }

    @Override
    public List<Subtask> getSubtasks() {
        return new ArrayList<>(subtasks.values());
    }

    @Override
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

    @Override
    public void deleteSubtaskById(int id) {
        Subtask subtask = subtasks.remove(id);
        if (subtask != null) {
            Epic epic = epics.get(subtask.getIdEpic());
            if (epic != null) {
                epic.removeSubtaskId(id);
                updateEpicStatus(epic);
            }

            historyManager.remove(id);

            System.out.println("Саб-задача \"" + subtask.getName() + "\" удалена!");
        } else {
            System.out.println("Саб-задача с id " + id + " не найдена.");
        }
    }

    @Override
    public void deleteAllSubtasks() {
        subtasks.clear();
        for (Epic epic : epics.values()) {
            epic.getSubtaskIds().clear();
            updateEpicStatus(epic);
        }
        System.out.println("Все саб-задачи удалены!");
    }

    @Override
    public List<Task> getHistory() {
        return historyManager.getHistory();
    }
}
