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
import java.time.Duration;
import java.time.LocalDateTime;
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
                        case Epic epic -> fileBackedTaskManager.addNewEpic(epic);
                        case Subtask subtask -> fileBackedTaskManager.addNewSubtask(subtask);
                        default -> fileBackedTaskManager.addNewTask(task);
                    }
                }
            }

        } catch (IOException exception) {
            throw new RuntimeException("Ошибка при загрузке данных из файла", exception);
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

        } catch (IOException exception) {
            throw new ManagerSaveException("Ошибка при сохранении данных в файл", exception);
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
        LocalDateTime startTime = LocalDateTime.parse(fields[5]);
        Duration duration = Duration.parse(fields[6]);
        return new Task(id, name, description, status, startTime, duration);
    }

    private Epic parseEpic(String[] fields) {
        int id = Integer.parseInt(fields[0]);
        String name = fields[2];
        TaskStatus status = TaskStatus.valueOf(fields[3]);
        String description = fields[4];
        Epic epic = new Epic(id, name, description);
        epic.setStatus(status);
        return epic;
    }

    private Subtask parseSubtask(String[] fields) {
        int id = Integer.parseInt(fields[0]);
        String name = fields[2];
        TaskStatus status = TaskStatus.valueOf(fields[3]);
        String description = fields[4];
        int idEpic = Integer.parseInt(fields[5]);
        LocalDateTime startTime = LocalDateTime.parse(fields[6]);
        Duration duration = Duration.parse(fields[7]);
        return new Subtask(id, idEpic, name, description, status, startTime, duration);
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
    public void updateTask(Task newTask) {
        super.updateTask(newTask);
        save();
    }

    @Override
    public void deleteTaskById(int id) {
        super.deleteTaskById(id);
        save();
    }

    @Override
    public void deleteAllTasks() {
        super.deleteAllTasks();
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
    public List<Subtask> getEpicSubtasks(int epicId) {
        return super.getEpicSubtasks(epicId);
    }

    @Override
    public List<Subtask> getSubtasks() {
        return super.getSubtasks();
    }

    @Override
    public void updateSubtask(Subtask newSubtask) {
        super.updateSubtask(newSubtask);
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
