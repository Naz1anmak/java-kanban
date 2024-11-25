package test;

import manager.Managers;
import manager.TaskManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import task.Epic;
import task.Subtask;
import task.Task;
import task.TaskStatus;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

public class InMemoryTaskManagerTest {
    private TaskManager taskManager;

    @BeforeEach
    public void beforeEach() {
        taskManager = Managers.getDefault();
    }

    @Test
    void testAddAndGetTasks() {
        Task task1 = new Task("Первая", "Описание 1", TaskStatus.NEW);
        final int taskId = taskManager.addNewTask(task1);
        final Task savedTask = taskManager.getTask(taskId);

        assertNotNull(savedTask, "Задача не найдена.");
        assertEquals(task1, savedTask, "Задачи не совпадают.");

        Task task2 = new Task("Вторая", "Описание 2", TaskStatus.DONE);
        taskManager.addNewTask(task2);

        final List<Task> tasks = taskManager.getTasks();

        assertNotNull(tasks, "Задачи не возвращаются.");
        assertEquals(2, tasks.size(), "Должны быть две задачи.");
        assertEquals(task1, tasks.getFirst(), "Задачи не совпадают.");
    }

    @Test
    void testAddAndGetEpics() {
        Epic epic1 = new Epic("Первый эпик", "Описание 1");
        final int epicId = taskManager.addNewEpic(epic1);
        final Epic savedEpic = taskManager.getEpic(epicId);

        assertNotNull(savedEpic, "Эпик не найден.");
        assertEquals(epic1, savedEpic, "Эпики не совпадают.");

        Epic epic2 = new Epic("Второй эпик", "Описание 2");
        taskManager.addNewEpic(epic2);

        final List<Epic> epics = taskManager.getEpics();

        assertNotNull(epics, "Эпики не возвращаются.");
        assertEquals(2, epics.size(), "Должны быть два эпика.");
        assertEquals(epic1, epics.getFirst(), "Эпики не совпадают.");
    }

    @Test
    void testAddAndGetSubtasks() {
        Epic epic1 = new Epic("Первый эпик", "Описание 1");
        Epic epic2 = new Epic("Второй эпик", "Описание 2");
        taskManager.addNewEpic(epic1);
        taskManager.addNewEpic(epic2);

        Subtask subtask1 = new Subtask(taskManager.getEpics().getFirst().getId(),
                "Первая саб-таска", "Описание1", TaskStatus.NEW);
        final int subtasksId = taskManager.addNewSubtask(subtask1);
        final Subtask savedSubtask = taskManager.getSubtask(subtasksId);

        assertNotNull(subtask1, "Саб-таска не найдена.");
        assertEquals(subtask1, savedSubtask, "Саб-таски не совпадают.");

        Subtask subtask2 = new Subtask(taskManager.getEpics().getFirst().getId(),
                "Вторая саб-таска", "Описание2", TaskStatus.NEW);
        taskManager.addNewSubtask(subtask2);
        Subtask subtask3 = new Subtask(taskManager.getEpics().getLast().getId(),
                "Третья саб-таска", "Описание3", TaskStatus.NEW);
        taskManager.addNewSubtask(subtask3);

        final List<Subtask> subtasks = taskManager.getSubtasks();

        assertNotNull(subtasks, "Саб-задачи не возвращаются.");
        assertEquals(3, subtasks.size(), "Должны быть три подзадачи.");
        assertEquals(subtask1, subtasks.getFirst(), "Саб-задачи не совпадают.");
    }

    @Test
    void shouldNotAllowEpicToBeItsOwnSubtask() {
        Epic epic = new Epic("Первый эпик", "Описание 1");
        int epicId = taskManager.addNewEpic(epic);

        Subtask invalidSubtask = new Subtask(epicId, "Некорректная саб-таска", "Описание", TaskStatus.NEW);
        invalidSubtask.setId(epicId);
        int result = taskManager.addNewSubtask(invalidSubtask);

        assertEquals(-1, result, "Эпик не должен добавляться как собственная подзадача.");
        assertEquals(0, taskManager.getSubtasks().size(), "Саб-задач не должно быть добавлено.");
    }

    @Test
    void shouldNotAllowSubtaskToBeItsOwnEpic() {
        Epic epic = new Epic("Первый эпик", "Описание 1");
        taskManager.addNewEpic(epic);

        Subtask subtask = new Subtask(1,
                "Первая саб-таска", "Описание1", TaskStatus.NEW);
        subtask.setIdEpic(subtask.getId());
        taskManager.addNewSubtask(subtask);

        assertEquals(0, taskManager.getSubtasks().size(),
                "Саб-задача с некорректным ID не должна добавляться.");
    }

    @Test
    void shouldNotConflictBetweenManuallySetAndGeneratedIds() {
        Task manualIdTask = new Task("Первая", "Описание 1", TaskStatus.NEW);
        manualIdTask.setId(5);
        taskManager.addNewTask(manualIdTask);
        int taskId1 = manualIdTask.getId();

        Task generateIdTask = new Task("Вторая", "Описание 2", TaskStatus.DONE);
        int taskId2 = taskManager.addNewTask(generateIdTask);

        assertEquals(manualIdTask, taskManager.getTask(taskId1));
        assertEquals(generateIdTask, taskManager.getTask(taskId2));
    }

    @Test
    void shouldNotModifyTaskFieldsAfterAddingToManager() {
        Task originalTask = new Task("Первая", "Описание 1", TaskStatus.NEW);
        Task copyTask = new Task(originalTask.getName(), originalTask.getDescription(), originalTask.getStatus());

        taskManager.addNewTask(originalTask);

        assertEquals(copyTask.getName(), originalTask.getName());
        assertEquals(copyTask.getDescription(), originalTask.getDescription());
        assertEquals(copyTask.getStatus(), originalTask.getStatus());
    }

    @Test
    void testUpdateEpic() {
        Epic epic = new Epic("Первый эпик", "Описание 1");
        taskManager.addNewEpic(epic);

        Epic epicUpdate = new Epic("Второй эпик", "Описание 2");
        final int epicIdUpdate = taskManager.addNewEpic(epicUpdate);

        taskManager.updateEpicFill(epicUpdate);

        Epic epicAfterUpdate = taskManager.getEpic(epicIdUpdate);

        assertEquals(epicUpdate, epicAfterUpdate, "Эпики не совпадают.");
    }
}
