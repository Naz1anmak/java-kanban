package manager;

import exception.TaskIntersectionException;
import history.HistoryManager;
import task.Epic;
import task.Subtask;
import task.Task;
import task.TaskStatus;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class InMemoryTaskManager implements TaskManager {
    protected int idCounter = 1;
    protected final Map<Integer, Task> tasks = new HashMap<>();
    protected final Map<Integer, Epic> epics = new HashMap<>();
    protected final Map<Integer, Subtask> subtasks = new HashMap<>();
    private final HistoryManager historyManager = Managers.getDefaultHistory();

    @Override
    public int addNewTask(Task task) {
        getIsIntersectionTasks(task);

        int taskId = idCounter++;
        Task taskCopy = new Task(task.getName(), task.getDescription(),
                task.getStatus(), task.getStartTime(), task.getDuration());
        taskCopy.setId(taskId);
        taskCopy.setEndTime(task.getEndTime());
        tasks.put(taskId, taskCopy);

        historyManager.addToPrioritizedTasks(taskCopy);

        System.out.println("Задача \"" + taskCopy.getName() + "\" с id=" + taskId + " добавлена!");
        return taskId;
    }

    @Override
    public Task getTask(int id) {
        Task task = tasks.get(id);
        if (task == null) {
            return null;
        }

        historyManager.add(task);
        return new Task(task.getName(), task.getDescription(),
                task.getStatus(), task.getStartTime(), task.getDuration());
    }

    @Override
    public List<Task> getTasks() {
        return new ArrayList<>(tasks.values());
    }

    @Override
    public void updateTask(Task oldTask, Task newTask) {
        getIsIntersectionTasks(newTask, oldTask.getId());

        newTask.setId(oldTask.getId());
        newTask.getEndTime(newTask.getStartTime(), newTask.getDuration());
        tasks.put(oldTask.getId(), newTask);
        historyManager.removePrioritizedTask(oldTask);
        historyManager.addToPrioritizedTasks(newTask);
        System.out.println("Задача c id=" + newTask.getId() + " обновлена!");
    }

    @Override
    public void deleteTaskById(int id) {
        Task task = tasks.remove(id);
        String name = task.getName();
        historyManager.remove(id);
        historyManager.removePrioritizedTask(task);
        System.out.println("Задача \"" + name + "\" удалена!");
    }

    @Override
    public void deleteAllTasks() {
        for (Task task : tasks.values()) {
            historyManager.removePrioritizedTask(task);
        }

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
        if (epic == null) {
            return null;
        }

        historyManager.add(epic);
        Epic epicCopy = new Epic(epic.getName(), epic.getDescription());
        epic.getSubtaskIds().forEach(epicCopy::addSubtaskId);
        updateEpicStatus(epicCopy);
        epicCopy.setId(epic.getId());
        epicCopy.setDuration(epic.getDuration());
        epicCopy.setStartTime(epic.getStartTime());
        return epicCopy;
    }

    @Override
    public List<Epic> getEpics() {
        return new ArrayList<>(epics.values());
    }

    @Override
    public void updateEpicFill(Epic oldEpic, Epic newEpic) {
        if (oldEpic == null || newEpic == null) {
            System.out.println("Ошибка. Передан пустой эпик.");
            return;
        }

        newEpic.setId(oldEpic.getId());
        if (!oldEpic.getSubtaskIds().isEmpty()) {
            oldEpic.getSubtaskIds().forEach(newEpic::addSubtaskId);
            updateEpicStatus(newEpic);
        }
        epics.put(oldEpic.getId(), newEpic);

        System.out.println("Эпик c id=" + oldEpic.getId() + " обновлен!");
    }

    @Override
    public void updateEpicStatus(Epic epic) {
        int countNew = 0;
        int countDone = 0;
        Duration duration = Duration.ZERO;
        LocalDateTime startTime = LocalDateTime.MAX;
        LocalDateTime endTime = LocalDateTime.MIN;

        for (int subtaskId : epic.getSubtaskIds()) {
            Subtask subtask = subtasks.get(subtaskId);
            if (subtask.getStatus() == TaskStatus.NEW) {
                countNew++;
            } else if (subtask.getStatus() == TaskStatus.DONE) {
                countDone++;
            }

            if (subtask.getStartTime().isBefore(startTime)) {
                startTime = subtask.getStartTime();
            }
            if (subtask.getEndTime(subtask.getStartTime(), subtask.getDuration()).isAfter(endTime)) {
                endTime = subtask.getEndTime(subtask.getStartTime(), subtask.getDuration());
            }
            duration = duration.plus(subtask.getDuration());

        }
        if (countNew == epic.getSubtaskIds().size()) epic.setStatus(TaskStatus.NEW);
        else if (countDone == epic.getSubtaskIds().size()) epic.setStatus(TaskStatus.DONE);
        else epic.setStatus(TaskStatus.IN_PROGRESS);

        epic.setDuration(duration);
        epic.setStartTime(startTime);
        epic.setEndTime(endTime);
    }

    @Override
    public void deleteEpicById(int id) {
        Epic epic = epics.get(id);
        if (epic == null) return;

        epic.getSubtaskIds().forEach(this::deleteSubtaskById);

        String name = epics.get(id).getName();
        historyManager.remove(id);
        epics.remove(id);

        System.out.println("Эпик \"" + name + "\" и его саб-задачи удалены!");
    }

    @Override
    public void deleteAllEpics() {
        deleteAllSubtasks();
        epics.clear();

        System.out.println("Все эпики и их саб-задачи удалены!");
    }

    @Override
    public int addNewSubtask(Subtask subtask) {
        getIsIntersectionTasks(subtask);

        Epic epic = epics.get(subtask.getIdEpic());
        if (epic == null) {
            System.out.println("Эпик с id " + subtask.getIdEpic() + " не найден.");
            return -1;
        }

        if (epic.getId() == subtask.getId()) {
            System.out.println("Подзадача не добавлена, её id совпадает с id эпика.");
            return -1;
        }

        if (subtask.getIdEpic() == subtask.getId()) {
            System.out.println("Саб-задача не может быть своим же эпиком. Саб-задача не добавлена.");
            return -1;
        }

        int subtaskId = idCounter++;
        Subtask subtaskCopy =
                new Subtask(subtask.getIdEpic(), subtask.getName(), subtask.getDescription(), subtask.getStatus(),
                        subtask.getStartTime(), subtask.getDuration());
        subtaskCopy.setId(subtaskId);
        subtasks.put(subtaskId, subtaskCopy);
        epic.addSubtaskId(subtaskId);
        updateEpicStatus(epic);

        if (subtaskCopy.getStartTime() != null) {
            historyManager.addToPrioritizedTasks(subtaskCopy);

            subtaskCopy.getEndTime(subtaskCopy.getStartTime(), subtaskCopy.getDuration());
        }

        System.out.println("Саб-задача \"" + subtaskCopy.getName() + "\" с id=" + subtaskId + " добавлена!");
        return subtaskId;
    }

    @Override
    public Subtask getSubtask(int id) {
        Subtask subtask = subtasks.get(id);
        if (subtask == null) {
            return null;
        }

        historyManager.add(subtask);
        return new Subtask(subtask.getIdEpic(), subtask.getName(), subtask.getDescription(), subtask.getStatus(),
                subtask.getStartTime(), subtask.getDuration());
    }

    @Override
    public List<Subtask> getEpicSubtasks(int epicId) {
        Epic epic = epics.get(epicId);

        if (epic == null) {
            System.out.println("Эпик с id " + epicId + " не найден.");
            return new ArrayList<>();
        }

        List<Subtask> subtasksInEpic = epic.getSubtaskIds().stream()
                .filter(subtasks::containsKey)
                .map(subtasks::get)
                .collect(Collectors.toList());

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
    public void updateSubtask(Subtask oldSubtask, Subtask newSubtask) {
        getIsIntersectionTasks(newSubtask, oldSubtask.getId());

        Epic epic = epics.get(newSubtask.getIdEpic());
        if (epic == null) {
            System.out.println("Эпик с id " + newSubtask.getIdEpic() + " не найден.");
            return;
        }

        newSubtask.setId(oldSubtask.getId());
        newSubtask.getEndTime(newSubtask.getStartTime(), newSubtask.getDuration());
        subtasks.put(newSubtask.getId(), newSubtask);
        historyManager.removePrioritizedTask(oldSubtask);
        historyManager.addToPrioritizedTasks(newSubtask);

        updateEpicStatus(epic);

        System.out.println("Саб-задача c id=" + newSubtask.getId() + " обновлена!");
    }

    @Override
    public void deleteSubtaskById(int id) {
        Subtask subtask = subtasks.remove(id);
        if (subtask == null) {
            System.out.println("Саб-задача с id " + id + " не найдена.");
            return;
        }

        Epic epic = epics.get(subtask.getIdEpic());
        if (epic != null) {
            historyManager.remove(id);
            historyManager.removePrioritizedTask(subtask);
            epic.removeSubtaskId(id);
            updateEpicStatus(epic);
        }

        System.out.println("Саб-задача \"" + subtask.getName() + "\" удалена!");
    }

    @Override
    public void deleteAllSubtasks() {
        for (Epic epic : epics.values()) {
            for (Integer subtaskId : epic.getSubtaskIds()) {
                epic.removeSubtaskId(subtaskId);
                historyManager.removePrioritizedTask(subtasks.get(subtaskId));
            }
            updateEpicStatus(epic);
        }

        subtasks.clear();
        System.out.println("Все саб-задачи удалены!");
    }

    @Override
    public List<Task> getHistory() {
        return historyManager.getHistory();
    }

    @Override
    public List<Task> getPrioritizedTasks() {
        return historyManager.getPrioritizedTasks();
    }

    private void getIsIntersectionTasks(Task newTask) {
        if (newTask.getStartTime() == null || newTask.getDuration() == null) {
            throw new TaskIntersectionException("В задаче отсутствует время начала или окончания.");
        }

        newTask.getEndTime(newTask.getStartTime(), newTask.getDuration());

        if (historyManager.isIntersectionTasks(newTask)) {
            throw new TaskIntersectionException(
                    "Задача \"" + newTask.getName() + "\" пересекается по времени с другой задачей!");
        }
    }

    private void getIsIntersectionTasks(Task updTask, int idAddedTask) {
        if (updTask.getStartTime() == null) {
            throw new TaskIntersectionException("В задаче отсутствует время начала или окончания.");
        }

        updTask.getEndTime(updTask.getStartTime(), updTask.getDuration());

        if (historyManager.isIntersectionTasks(updTask, idAddedTask)) {
            throw new TaskIntersectionException(
                    "Задача \"" + updTask.getName() + "\" пересекается по времени с другой задачей!");
        }
    }

    protected InMemoryHistoryManager getHistoryManager() {
        return (InMemoryHistoryManager) historyManager;
    }
}
