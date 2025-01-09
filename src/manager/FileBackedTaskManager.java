package manager;

import exception.ManagerSaveException;
import history.TasksTypes;
import task.Epic;
import task.Subtask;
import task.Task;
import task.TaskStatus;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

public class FileBackedTaskManager extends InMemoryTaskManager {
    private static final String HISTORY_DIR = "src/history";
    private static final String AUTO_SAVE_FILE = "autoSave.csv";
    private final File autoSaveFile;

    public FileBackedTaskManager(Path autoSavePath) {
        try {
            if (autoSavePath == null || !Files.exists(autoSavePath) || !Files.isRegularFile(autoSavePath)) {
                Path dir = Paths.get(HISTORY_DIR);
                Path defaultFile = dir.resolve(AUTO_SAVE_FILE);

                if (!Files.exists(defaultFile)) {
                    Files.createFile(defaultFile);
                    System.out.println("Создан файл автосохранения: " + defaultFile.toAbsolutePath());
                }
                this.autoSaveFile = defaultFile.toFile();

            } else {
                this.autoSaveFile = autoSavePath.toFile();
            }
        } catch (IOException exception) {
            throw new RuntimeException("Ошибка при создании файла для автосохранения.", exception);
        }
    }

    private FileBackedTaskManager(File autoSaveFile) {
        this.autoSaveFile = autoSaveFile;
    }

    public static FileBackedTaskManager loadFromFile(File file) {
        if (file == null) {
            System.out.println("Файл автосохранения не был передан!");
            return null;
        }

        FileBackedTaskManager fileBackedTaskManager = new FileBackedTaskManager(file);

        try (BufferedReader bufferedReader = new BufferedReader(new FileReader(file))) {
            while (bufferedReader.ready()) {
                String string = bufferedReader.readLine().trim();

                if (!string.isEmpty()) {
                    Task task = fileBackedTaskManager.fromString(string);

                    switch (task) {
                        case null -> {
                            System.out.println("Ошибка. Задача пустая");
                            return null;
                        }
                        case Epic epic -> fileBackedTaskManager.addNewEpicWithId(epic);
                        case Subtask subtask -> fileBackedTaskManager.addNewSubtaskWithId(subtask);
                        default -> fileBackedTaskManager.addNewTaskWithId(task);
                    }
                }
            }

        } catch (IOException e) {
            throw new RuntimeException("Ошибка при загрузке данных из файла", e);
        }

        return fileBackedTaskManager;
    }

    private void save() {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(autoSaveFile))) {
            for (Task task : getTasks()) {
                writer.write(task.toString());
                writer.newLine();
            }

            for (Epic epic : getEpics()) {
                writer.write(epic.toString());
                writer.newLine();
            }

            for (Subtask subtask : getSubtasks()) {
                writer.write(subtask.toString());
                writer.newLine();
            }

        } catch (IOException e) {
            throw new ManagerSaveException("Ошибка при сохранении данных в файл", e);
        }
    }

    private Task fromString(String value) {
        String[] fields = value.split(",");
        TasksTypes tasksTypes = TasksTypes.valueOf(fields[1]);

        return switch (tasksTypes) {
            case TASK -> parseTask(fields);
            case EPIC -> parseEpic(fields);
            case SUBTASK -> parseSubtask(fields);
        };
    }

    private Task parseTask(String[] fields) {
        int id = Integer.parseInt(fields[0]);
        String name = fields[2];
        TaskStatus status = TaskStatus.valueOf(fields[3]);
        String description = fields[4];
        return new Task(id, name, description, status);
    }

    private Epic parseEpic(String[] fields) {
        int id = Integer.parseInt(fields[0]);
        String name = fields[2];
        TaskStatus status = TaskStatus.valueOf(fields[3]);
        String description = fields[4];
        Epic epic = new Epic(name, description);
        epic.setId(id);
        epic.setStatus(status);
        return epic;
    }

    private Subtask parseSubtask(String[] fields) {
        int id = Integer.parseInt(fields[0]);
        String name = fields[2];
        TaskStatus status = TaskStatus.valueOf(fields[3]);
        String description = fields[4];
        int idEpic = Integer.parseInt(fields[5]);
        Subtask subtask = new Subtask(idEpic, name, description, status);
        subtask.setId(id);
        return subtask;
    }

    public void addNewTaskWithId(Task task) {
        int taskId = task.getId();
        Task taskCopy = new Task(task.getName(), task.getDescription(), task.getStatus());
        taskCopy.setId(taskId);
        tasks.put(taskId, taskCopy);

        if (taskCopy.getId() >= idCounter) {
            idCounter = taskCopy.getId() + 1;
        }

        System.out.println("Таска загружена: " + taskCopy);
    }

    public void addNewEpicWithId(Epic epic) {
        int epicId = epic.getId();
        Epic epicCopy = new Epic(epic.getName(), epic.getDescription());
        epicCopy.setId(epicId);
        epics.put(epicId, epicCopy);

        if (epicCopy.getId() >= idCounter) {
            idCounter = epicCopy.getId() + 1;
        }

        System.out.println("Эпик загружен: " + epicCopy);
    }

    public void addNewSubtaskWithId(Subtask subtask) {
        Epic epic = epics.get(subtask.getIdEpic());

        int subtaskId = subtask.getId();
        Subtask subtaskCopy =
                new Subtask(subtask.getIdEpic(), subtask.getName(), subtask.getDescription(), subtask.getStatus());
        subtaskCopy.setId(subtaskId);
        subtasks.put(subtaskId, subtaskCopy);
        epic.addSubtaskId(subtaskId);
        updateEpicStatus(epic);

        if (subtaskCopy.getId() >= idCounter) {
            idCounter = subtaskCopy.getId() + 1;
        }

        System.out.println("Сабтаска загружена: " + subtaskCopy);
    }

    @Override
    public int addNewTask(Task task) {
        int addedNewTask = super.addNewTask(task);
        save();
        return addedNewTask;
    }

    @Override
    public Task getTask(int id) {
        return super.getTask(id);
    }

    @Override
    public List<Task> getTasks() {
        return super.getTasks();
    }

    @Override
    public void updateTask(Task task) {
        super.updateTask(task);
        save();
    }

    @Override
    public void deleteTaskById(int id) {
        super.deleteTaskById(id);
        save();
    }

    @Override
    public void deleteTasks() {
        super.deleteTasks();
        save();
    }

    @Override
    public int addNewEpic(Epic epic) {
        int addedNewEpic = super.addNewEpic(epic);
        save();
        return addedNewEpic;
    }

    @Override
    public Epic getEpic(int id) {
        return super.getEpic(id);
    }

    @Override
    public List<Epic> getEpics() {
        return super.getEpics();
    }

    @Override
    public void updateEpicFill(Epic newEpic) {
        super.updateEpicFill(newEpic);
        save();
    }

    @Override
    public void updateEpicStatus(Epic epic) {
        super.updateEpicStatus(epic);
        save();
    }

    @Override
    public void deleteEpicById(int id) {
        super.deleteEpicById(id);
        save();
    }

    @Override
    public void deleteAllEpics() {
        super.deleteAllEpics();
        save();
    }

    @Override
    public int addNewSubtask(Subtask subtask) {
        int addedNewSubtask = super.addNewSubtask(subtask);
        save();
        return addedNewSubtask;
    }

    @Override
    public Subtask getSubtask(int id) {
        return super.getSubtask(id);
    }

    @Override
    public List<Subtask> getAllLinkedSubtasks(int epicId) {
        return super.getAllLinkedSubtasks(epicId);
    }

    @Override
    public List<Subtask> getSubtasks() {
        return super.getSubtasks();
    }

    @Override
    public void updateSub(Subtask oldSubtask, Subtask newSubtask) {
        super.updateSub(oldSubtask, newSubtask);
        save();
    }

    @Override
    public void deleteSubtaskById(int id) {
        super.deleteSubtaskById(id);
        save();
    }

    @Override
    public void deleteAllSubtasks() {
        super.deleteAllSubtasks();
        save();
    }

    @Override
    public List<Task> getHistory() {
        return super.getHistory();
    }
}
