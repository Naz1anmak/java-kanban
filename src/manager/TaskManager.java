package manager;

import task.Epic;
import task.Subtask;
import task.Task;
import task.TaskStatus;

import java.util.ArrayList;
import java.util.HashMap;

public class TaskManager {
    private static int idCounter = 1;
    private final HashMap<Integer, Task> tasks = new HashMap<>();
    private final HashMap<Integer, Epic> epics = new HashMap<>();
    private final HashMap<Integer, Subtask> subtasks = new HashMap<>();

    public void addNewTask(String name, String description, TaskStatus status) {
        Task task = new Task(idCounter++, name, description, status);
        tasks.put(task.getId(), task);
        System.out.println("Задача \"" + task.getName() + "\" добавлена!");
    }

    public ArrayList<Task> getTasks() {
        ArrayList<Task> tasksList = new ArrayList<>();
        for (Task task : tasks.values()) {
            tasksList.add(task);
        }
        return tasksList;
    }

    public void updateTask(Task task) {
        tasks.put(task.getId(), task);
        System.out.println("Задача обновлена!");
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

    public void addNewEpic(String name, String description) {
        int id = idCounter++;
        Epic epic = new Epic(id, name, description);
        epics.put(id, epic);
        System.out.println("Эпик \"" + name + "\" добавлен!");
    }

    public ArrayList<Epic> getEpics() {
        ArrayList<Epic> epicList = new ArrayList<>();
        for (Epic epic : epics.values()) {
            epicList.add(epic);
        }
        return epicList;
    }

    public void updateEpicFill(Epic newEpic) {
        Epic oldEpic = epics.get(newEpic.getId());

        oldEpic.setName(newEpic.getName());
        oldEpic.setDescription(newEpic.getDescription());

        System.out.println("Эпик обновлен!");
    }

    private void updateEpicStatus(Epic epic) {
        int countNew = 0;
        int countDone = 0;
        for (Subtask subtask : epic.getSubtasks()) {
            if (subtask.getStatus().equals(TaskStatus.NEW)) {
                countNew++;
            } else if (subtask.getStatus().equals(TaskStatus.DONE)) {
                countDone++;
            }
        }
        if (countNew == epic.getSubtasks().size()) epic.setStatus(TaskStatus.NEW);
        else if (countDone == epic.getSubtasks().size()) epic.setStatus(TaskStatus.DONE);
        else epic.setStatus(TaskStatus.IN_PROGRESS);
    }

    public Epic findEpic(int id) {
        return epics.get(id);
    }

    public void deleteEpicById(int id) {
        Epic epic = epics.remove(id);
        String name = epic.getName();
        for (Subtask subtask : epic.getSubtasks()) {
            subtasks.remove(subtask.getId());
        }
        System.out.println("Эпик \"" + name + "\" и его саб-задачи удалены!");
    }

    public void deleteAllEpics() {
        subtasks.clear();
        epics.clear();
        System.out.println("Все эпики и их саб-задачи удалены!");
    }

    public void addNewSubtask(Subtask subtask) {
        int id = idCounter++;
        subtask.setId(id);
        subtasks.put(id, subtask);

        Epic epic = epics.get(subtask.getIdEpic());
        if (epic != null) {
            epic.addSubtask(subtask);
            updateEpicStatus(epic);
            System.out.println("Саб-задача \"" + subtask.getName() + "\" добавлена!");
        } else {
            System.out.println("Эпик с id " + subtask.getIdEpic() + " не найден.");
        }
    }

    public ArrayList<Subtask> getSubtasks(int epicId) {
        Epic epic = epics.get(epicId);

        if (epic == null) {
            System.out.println("Эпик с id " + epicId + " не найден.");
            return new ArrayList<>();
        }

        ArrayList<Subtask> subtasks = epic.getSubtasks();
        if (subtasks.isEmpty()) {
            System.out.println("В этом эпике саб-задач нет!");
        } else {
            System.out.print("Эпик \"" + epic.getName() + "\" содержит " + subtasks.size() + " саб-задачи:");
            for (Subtask subtask : subtasks) {
                System.out.print(subtask);
            }
        }
        return subtasks;
    }

    public void updateSub(Subtask newSubtask) {
        subtasks.put(newSubtask.getId(), newSubtask);

        Epic epic = epics.get(newSubtask.getIdEpic());
        if (epic == null) {
            System.out.println("Эпик с id " + newSubtask.getIdEpic() + " не найден.");
            return;
        }

        ArrayList<Subtask> epicSubtasks = epic.getSubtasks();
        for (int i = 0; i < epicSubtasks.size(); i++) {
            if (epicSubtasks.get(i).getId() == newSubtask.getId()) {
                epicSubtasks.set(i, newSubtask);
                break;
            }
        }

        updateEpicStatus(epic);

        System.out.println("Саб-задача обновлена!");
    }


    public Subtask findSub(int id) {
        return subtasks.get(id);
    }

    public void deleteSubtaskById(int id) {
        Subtask subtask = subtasks.remove(id);
        String name = subtask.getName();
        Epic epic = epics.get(subtask.getIdEpic());
        epic.getSubtasks().remove(subtask);
        updateEpicStatus(epic);
        System.out.println("Саб-задача \"" + name + "\" удалена!");
    }

    public void deleteAllSubtasks() {
        subtasks.clear();
        for (Epic epic : epics.values()) {
            epic.getSubtasks().clear();
            updateEpicStatus(epic);
        }
        System.out.println("Все саб-задачи удалены!");
    }
}
