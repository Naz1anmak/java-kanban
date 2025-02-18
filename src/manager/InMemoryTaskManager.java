package manager;

import exception.NotFoundException;
import exception.TaskIntersectionException;
import history.HistoryManager;
import task.Epic;
import task.Subtask;
import task.Task;
import task.TaskStatus;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

public class InMemoryTaskManager implements TaskManager {
    protected int idCounter = 1;
    protected final Map<Integer, Task> tasks = new HashMap<>();
    protected final Map<Integer, Epic> epics = new HashMap<>();
    protected final Map<Integer, Subtask> subtasks = new HashMap<>();
    private final Set<Task> prioritizedTasks = new TreeSet<>(taskComparator);
    private final HistoryManager historyManager = Managers.getDefaultHistory();

    @Override
    public int addNewTask(Task task) {
        if (isIntersectionTasks(task)) {
            throw new TaskIntersectionException(
                    "Задача \"" + task.getName() + "\" пересекается по времени с другой задачей!");
        }

        int taskId;
        if (task.getId() == -1) {
            taskId = idCounter++;
            task = new Task(taskId, task.getName(), task.getDescription(),
                    task.getStatus(), task.getStartTime(), task.getDuration());
        } else {
            taskId = task.getId();
        }

        if (task.getStartTime() != null) {
            addToPrioritizedTasks(task);
        }

        tasks.put(taskId, task);
        System.out.println("Задача \"" + task.getName() + "\" с id=" + taskId + " добавлена!");
        return taskId;
    }

    @Override
    public Task getTask(int id) {
        Task task = tasks.get(id);
        if (task == null) {
            throw new NotFoundException("Задача с id " + id + " не найдена.");
        }

        historyManager.add(task);
        return task;
    }

    @Override
    public List<Task> getTasks() {
        return new ArrayList<>(tasks.values());
    }

    @Override
    public void updateTask(Task newTask) {
        Task oldTask = tasks.get(newTask.getId());
        if (oldTask == null) {
            System.out.println("Задача с id " + newTask.getId() + " не найдена.");
            return;
        }

        if (isIntersectionTasks(newTask)) {
            throw new TaskIntersectionException(
                    "Задача \"" + newTask.getName() + "\" пересекается по времени с другой задачей!");
        }

        removePrioritizedTask(tasks.get(newTask.getId()));
        addToPrioritizedTasks(newTask);
        tasks.put(newTask.getId(), newTask);
        System.out.println("Задача c id=" + newTask.getId() + " обновлена!");
    }

    @Override
    public void deleteTaskById(int id) {
        Task task = tasks.remove(id);
        if (task == null) {
            throw new NotFoundException("Задача с id " + id + " не найдена.");
        }

        historyManager.remove(id);
        removePrioritizedTask(task);
        System.out.println("Задача \"" + task.getName() + "\" удалена!");
    }

    @Override
    public void deleteAllTasks() {
        for (Task task : tasks.values()) {
            removePrioritizedTask(task);
        }

        tasks.clear();
        System.out.println("Все задачи удалены!");
    }

    @Override
    public int addNewEpic(Epic epic) {
        int epicId;
        if (epic.getId() == -1) {
            epicId = idCounter++;
            epic = new Epic(epicId, epic.getName(), epic.getDescription());
        } else {
            epicId = epic.getId();
        }
        epics.put(epicId, epic);
        System.out.println("Эпик \"" + epic.getName() + "\" с id=" + epicId + " добавлен!");
        return epicId;
    }

    @Override
    public Epic getEpic(int id) {
        Epic epic = epics.get(id);
        if (epic == null) {
            throw new NotFoundException("Эпик с id " + id + " не найден.");
        }

        historyManager.add(epic);
        return epic;
    }

    @Override
    public List<Epic> getEpics() {
        return new ArrayList<>(epics.values());
    }

    @Override
    public void updateEpicFill(Epic newEpic) {
        Epic oldEpic = epics.get(newEpic.getId());
        if (oldEpic == null) {
            System.out.println("Эпик с id " + newEpic.getId() + " не найден.");
            return;
        }

        Epic updatedEpic = new Epic(newEpic.getId(), newEpic.getName(), newEpic.getDescription(),
                oldEpic.getStatus(), oldEpic.getStartTime(), oldEpic.getDuration());

        for (int subtaskId : oldEpic.getSubtaskIds()) {
            updatedEpic.addSubtaskId(subtaskId);
        }

        epics.put(oldEpic.getId(), updatedEpic);

        System.out.println("Эпик c id=" + oldEpic.getId() + " обновлен!");
    }

    protected void updateEpicStatus(Epic epic) {
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

        if (countNew == epic.getSubtaskIds().size()) {
            epic.setStatus(TaskStatus.NEW);
        } else if (countDone == epic.getSubtaskIds().size()) {
            epic.setStatus(TaskStatus.DONE);
        } else {
            epic.setStatus(TaskStatus.IN_PROGRESS);
        }

        calculateEpicTimeParameters(epic);
    }

    private void calculateEpicTimeParameters(Epic epic) {
        Duration totalDuration = Duration.ZERO;
        LocalDateTime startTime = LocalDateTime.MAX;
        LocalDateTime endTime = LocalDateTime.MIN;

        for (int subtaskId : epic.getSubtaskIds()) {
            Subtask subtask = subtasks.get(subtaskId);
            if (subtask.getStartTime().isBefore(startTime)) {
                startTime = subtask.getStartTime();
            }
            LocalDateTime subtaskEndTime = subtask.getStartTime().plus(subtask.getDuration());
            if (subtaskEndTime.isAfter(endTime)) {
                endTime = subtaskEndTime;
            }
            totalDuration = totalDuration.plus(subtask.getDuration());
        }

        startTime = startTime.equals(LocalDateTime.MAX) ? null : startTime;

        Epic updEpic = new Epic(epic.getId(), epic.getName(), epic.getDescription(), epic.getStatus(), startTime,
                totalDuration);

        for (int subtaskId : epic.getSubtaskIds()) {
            updEpic.addSubtaskId(subtaskId);
        }

        epics.put(epic.getId(), updEpic);
    }

    @Override
    public void deleteEpicById(int id) {
        Epic epic = epics.remove(id);
        if (epic == null) {
            throw new NotFoundException("Задача с id " + id + " не найдена.");
        }

        epic.getSubtaskIds().forEach(this::deleteSubtaskById);
        historyManager.remove(id);
        System.out.println("Эпик \"" + epic.getName() + "\" и его саб-задачи удалены!");
    }

    @Override
    public void deleteAllEpics() {
        deleteAllSubtasks();
        epics.clear();

        System.out.println("Все эпики и их саб-задачи удалены!");
    }

    @Override
    public int addNewSubtask(Subtask subtask) {
        if (isIntersectionTasks(subtask)) {
            throw new TaskIntersectionException(
                    "Задача \"" + subtask.getName() + "\" пересекается по времени с другой задачей!");
        }

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

        int subtaskId;
        if (subtask.getId() == -1) {
            subtaskId = idCounter++;
            subtask = new Subtask(subtaskId, subtask.getIdEpic(), subtask.getName(), subtask.getDescription(),
                    subtask.getStatus(), subtask.getStartTime(), subtask.getDuration());
        } else {
            subtaskId = subtask.getId();
        }

        subtasks.put(subtaskId, subtask);
        epic.addSubtaskId(subtaskId);
        updateEpicStatus(epic);

        if (subtask.getEndTime() != null) {
            addToPrioritizedTasks(subtask);
        }

        System.out.println("Саб-задача \"" + subtask.getName() + "\" с id=" + subtaskId + " добавлена!");
        return subtaskId;
    }

    @Override
    public Subtask getSubtask(int id) {
        Subtask subtask = subtasks.get(id);
        if (subtask == null) {
            throw new NotFoundException("Подзадача с id " + id + " не найдена.");
        }

        historyManager.add(subtask);
        return subtask;
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
    public void updateSubtask(Subtask newSubtask) {
        Subtask oldSubtask = subtasks.get(newSubtask.getId());
        if (oldSubtask == null) {
            System.out.println("Подзадача с id " + newSubtask.getId() + " не найдена.");
            return;
        }

        if (isIntersectionTasks(newSubtask)) {
            throw new TaskIntersectionException(
                    "Задача \"" + newSubtask.getName() + "\" пересекается по времени с другой задачей!");
        }

        removePrioritizedTask(oldSubtask);
        addToPrioritizedTasks(newSubtask);
        subtasks.put(newSubtask.getId(), newSubtask);

        Epic epic = epics.get(newSubtask.getIdEpic());
        if (epic != null) {
            updateEpicStatus(epic);
        }

        System.out.println("Саб-задача c id=" + newSubtask.getId() + " обновлена!");
    }

    @Override
    public void deleteSubtaskById(int id) {
        Subtask subtask = subtasks.remove(id);
        if (subtask == null) {
            throw new NotFoundException("Подзадача с id " + id + " не найдена.");
        }

        Epic epic = epics.get(subtask.getIdEpic());
        if (epic != null) {
            historyManager.remove(id);
            removePrioritizedTask(subtask);
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
                removePrioritizedTask(subtasks.get(subtaskId));
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

    private void addToPrioritizedTasks(Task task) {
        if (task.getStartTime() != null) {
            prioritizedTasks.add(task);
        }
    }

    private void removePrioritizedTask(Task task) {
        prioritizedTasks.remove(task);
    }

    public List<Task> getPrioritizedTasks() {
        return List.copyOf(prioritizedTasks);
    }

    private boolean isIntersectionTasks(Task newTask) {
        if (newTask.getStartTime() == null) {
            throw new TaskIntersectionException("В задаче отсутствует время начала или окончания.");
        }

        int idAddedTask = newTask.getId();
        return getPrioritizedTasks().stream()
                .filter(task -> task.getId() != idAddedTask)
                .anyMatch(prioritizedTask -> {
                    LocalDateTime startTime = prioritizedTask.getStartTime();
                    LocalDateTime endTime = prioritizedTask.getEndTime();
                    return newTask.getStartTime().isBefore(endTime) &&
                            newTask.getEndTime().isAfter(startTime);
                });
    }

    private static final Comparator<Task> taskComparator = (o1, o2) -> {
        if (!o1.getStartTime().equals(o2.getStartTime())) {
            return o1.getStartTime().compareTo(o2.getStartTime());
        }

        return Integer.compare(o1.getId(), o2.getId());
    };
}
