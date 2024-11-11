import java.util.HashMap;

public class TaskManager {

    static HashMap<Integer, Task> tasks = new HashMap<>();
    static HashMap<Integer, Subtask> subtasks = new HashMap<>();
    static HashMap<Integer, Epic> epics = new HashMap<>();

    public static void addNewTask(Task task) {
        tasks.put(Task.generateId(task), task);
        System.out.println("Задача \"" + task.name + "\" добавлена!");
    }

    public static boolean isTasksEmpty() {
        if (tasks.isEmpty()) {
            System.out.println("Список задач пуст!");
            return true;
        }
        return false;
    }

    public static boolean isTaskThere(int id) {
        if (!(tasks.containsKey(id))) {
            System.out.println("Задачи с таким id не было найдено!");
            return false;
        }
        return true;
    }

    public static void getTasks() {
        System.out.println(tasks);
    }

    public static void updateTaskFill(int id, String newName, String newDescription) {
        Task newTask = tasks.get(id);

        newTask.name = newName;
        newTask.description = newDescription;
        System.out.println("Задача обновлена!");
    }

    public static void updateTaskStatus(int id) {
        Task task = tasks.get(id);
        if (task.status == TaskStatus.NEW) {
            task.status = TaskStatus.DONE;
        } else task.status = TaskStatus.NEW;

        System.out.println("Статус задачи обновлен на \"" + task.status + "\".");
    }

    public static void findTask(int id) {
        if (!(isTaskThere(id))) return;
        System.out.println(tasks.get(id));
    }

    public static void deleteTasks() {
        tasks.clear();
        System.out.println("Все задачи удалены!");
    }

    public static void deleteTaskById(int id) {
        Task task = tasks.get(id);
        String name = task.name;
        tasks.remove(id);
        System.out.println("Задача \"" + name + "\" удалена!");
    }

    public static void addNewEpic(Epic epic) {
        epics.put(Task.generateId(epic), epic);
        System.out.println("Эпик \"" + epic.name + "\" добавлен!");
    }

    public static boolean isEpicsEmpty() {
        if (epics.isEmpty()) {
            System.out.println("Список эпиков пуст!");
            return true;
        }
        return false;
    }

    public static boolean isEpicThere(int id) {
        if (!(epics.containsKey(id))) {
            System.out.println("Эпик с таким id не был найден!");
            return false;
        }
        return true;
    }

    public static void getEpics() {
        System.out.println(epics);
    }

    public static void updateEpicFill(int id, String newName, String newDescription) {
        Epic newEpic = epics.get(id);

        newEpic.name = newName;
        newEpic.description = newDescription;

        System.out.println("Эпик обновлен!");
    }

    public static void updateEpicStatus(Epic epic) {
        int countNEW = 0;
        int countDONE = 0;
        for (Subtask subtask : epic.getSubtasks()) {
            if (subtask.status.equals(TaskStatus.NEW)) {
                countNEW++;
            } else if (subtask.status.equals(TaskStatus.DONE)) {
                countDONE++;
            }
        }
        if (countNEW == epic.getSubtasks().size()) epic.status = TaskStatus.NEW;
        else if (countDONE == epic.getSubtasks().size()) epic.status = TaskStatus.DONE;
        else epic.status = TaskStatus.IN_PROGRESS;
    }

    public static void findEpic(int id) {
        if (!(isEpicThere(id))) return;
        System.out.println(epics.get(id));
    }

    public static void deleteEpicById(int id) {
        String name = epics.get(id).name;
        Epic epic = epics.remove(id);
        for (Subtask subtask : epic.getSubtasks()) {
            subtasks.remove(subtask.getId());
        }
        System.out.println("Эпик \"" + name + "\" и его саб-задачи удалены!");
    }

    public static void deleteAllEpics() {
        subtasks.clear();
        epics.clear();
        System.out.println("Все эпики и их саб-задачи удалены!");
    }

    public static void addNewSubtask(int idOfEpic, Subtask subtask) {
        int subtaskId = Task.generateId(subtask);
        subtask.setId(subtaskId);
        subtasks.put(subtaskId, subtask);

        Epic epic = epics.get(idOfEpic);
        epic.getSubtasks().add(subtask);
        updateEpicStatus(epic);

        System.out.println("Саб-задача \"" + subtask.name + "\" добавлена!");
    }

    public static boolean isSubsEmpty() {
        if (subtasks.isEmpty()) {
            System.out.println("Саб-задач пока не было создано!");
            return true;
        }
        return false;
    }

    public static boolean isSubThere(int id) {
        if (!(subtasks.containsKey(id))) {
            System.out.println("Саб-задачи с таким id не было найдено!");
            return false;
        }
        return true;
    }

    public static void getSubtasks(int epicId) {
        if (epics.get(epicId).subtasks.isEmpty()) {
            System.out.println("В этом эпике саб-задач нет!");
            return;
        }
        Epic epic = epics.get(epicId);

        System.out.println("Эпик \"" + epic.getName() + "\" содержит в себе " + epic.getSubtasks().size() + " " +
                "саб-задачи: ");
        System.out.println(epics.get(epicId).subtasks);
    }

    public static void updateSubFill(int id, String newName, String newDescription) {
        Subtask newSubtask = subtasks.get(id);

        newSubtask.name = newName;
        newSubtask.description = newDescription;

        System.out.println("Саб-задача обновлена!");
    }

    public static void updateSubStatus(int id) {
        Subtask subtask = subtasks.get(id);
        if (subtask.status == TaskStatus.NEW) {
            subtask.status = TaskStatus.DONE;
        } else subtask.status = TaskStatus.NEW;

        System.out.println("Статус задачи обновлен на \"" + subtask.status + "\".");

        updateEpicStatus(epics.get(subtask.getEpicId()));
    }

    public static void findSub(int id) {
        if (!(isSubThere(id))) return;

        Subtask subtask = subtasks.get(id);
        System.out.println(subtask);

        int epicId = subtask.getEpicId();
        Epic epic = epics.get(epicId);

        System.out.println("Входит в эпик \"" + epic.getName() + "\", id=" + epicId);
    }

    public static void deleteSubtaskById(int id) {
        String name = subtasks.get(id).name;
        Subtask subtask = subtasks.remove(id);
        Epic epic = epics.get(subtask.getEpicId());
        epic.getSubtasks().remove(subtask);
        updateEpicStatus(epic);
        System.out.println("Саб-задача \"" + name + "\" удалена!");
    }

    public static void deleteAllSubtasks() {
        subtasks.clear();
        for (Epic epic : epics.values()) {
            epic.getSubtasks().clear();
            updateEpicStatus(epic);
        }
        System.out.println("Все саб-задачи удалены!");
    }
}
