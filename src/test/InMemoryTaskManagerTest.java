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

import static org.junit.jupiter.api.Assertions.*;

public class InMemoryTaskManagerTest {
    private TaskManager taskManager;
    private Epic epic1;
    private int epic1Id;
    private Epic epic2;

    @BeforeEach
    void beforeEach() {
        taskManager = Managers.getDefault();
        epic1 = new Epic("Первый эпик", "Описание 1");
        epic1Id = taskManager.addNewEpic(epic1);
        epic2 = new Epic("Второй эпик", "Описание 2");
        taskManager.addNewEpic(epic2);
    }

    @Test
    void testAddAndGetTasks() {
        Task task1 = new Task("Первая", "Описание 1", TaskStatus.NEW);
        final int taskId = taskManager.addNewTask(task1);
        final Task savedTask = taskManager.getTask(taskId);

        assertNotNull(savedTask, "Задача не найдена.");
        assertEquals(task1.getName(), savedTask.getName(), "Названия задач не совпадают.");
        assertEquals(task1.getDescription(), savedTask.getDescription(), "Описания задач не совпадают.");
        assertEquals(task1.getStatus(), savedTask.getStatus(), "Статусы задач не совпадают.");

        Task task2 = new Task("Вторая", "Описание 2", TaskStatus.DONE);
        taskManager.addNewTask(task2);

        final List<Task> tasks = taskManager.getTasks();

        assertNotNull(tasks, "Задачи не возвращаются.");
        assertEquals(2, tasks.size(), "Должны быть две задачи.");
        assertEquals(task1.getName(), tasks.getFirst().getName(), "Названия первой задачи не совпадают.");
    }

    @Test
    void testAddAndGetEpics() {
        final Epic savedEpic = taskManager.getEpic(epic1Id);

        assertNotNull(savedEpic, "Эпик не найден.");
        assertEquals(epic1.getName(), savedEpic.getName(), "Названия эпиков не совпадают.");
        assertEquals(epic1.getDescription(), savedEpic.getDescription(), "Описания эпиков не совпадают.");

        final List<Epic> epics = taskManager.getEpics();

        assertNotNull(epics, "Эпики не возвращаются.");
        assertEquals(2, epics.size(), "Должны быть два эпика.");
        assertEquals(epic1.getName(), epics.getFirst().getName(), "Названия первого эпика не совпадают.");
    }

    @Test
    void testAddAndGetSubtasks() {
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
        assertEquals(subtask1.getName(), subtasks.getFirst().getName(),
                "Названия первой саб-задачи не совпадают.");
    }

    @Test
    void shouldNotAllowEpicToBeItsOwnSubtask() {
        Subtask invalidSubtask = new Subtask(epic1Id, "Некорректная саб-таска", "Описание", TaskStatus.NEW);
        invalidSubtask.setId(epic1Id);
        int result = taskManager.addNewSubtask(invalidSubtask);

        assertEquals(-1, result, "Эпик не должен добавляться как собственная подзадача.");
        assertEquals(0, taskManager.getSubtasks().size(), "Саб-задач не должно быть добавлено.");
    }

    @Test
    void shouldNotAllowSubtaskToBeItsOwnEpic() {
        Subtask subtask = new Subtask(1,
                "Первая саб-таска", "Описание1", TaskStatus.NEW);
        subtask.setIdEpic(subtask.getId());
        taskManager.addNewSubtask(subtask);

        assertEquals(0, taskManager.getSubtasks().size(),
                "Саб-задача с некорректным ID не должна добавляться.");
    }

    @Test
    void shouldNotAllowSubtaskWithInvalidEpicId() {
        Subtask subtask = new Subtask(999, "Сабтаска", "Описание", TaskStatus.NEW);
        int result = taskManager.addNewSubtask(subtask);

        assertEquals(-1, result, "Саб-таска с несуществующим эпиком не должна добавляться.");
        assertTrue(taskManager.getSubtasks().isEmpty(), "Список саб-тасок должен быть пустым.");
    }

    @Test
    void shouldNotConflictBetweenManuallySetAndGeneratedIds() {
        Task manualIdTask = new Task("Первая", "Описание 1", TaskStatus.NEW);
        int taskId1 =taskManager.addNewTask(manualIdTask);

        Task generateIdTask = new Task("Вторая", "Описание 2", TaskStatus.DONE);
        int taskId2 = taskManager.addNewTask(generateIdTask);

        assertEquals("Первая", taskManager.getTask(taskId1).getName(),
                "Название первой задачи не совпадает.");
        assertEquals("Вторая", taskManager.getTask(taskId2).getName(),
                "Название второй задачи не совпадает.");
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
        Epic epicUpdate = new Epic("Третий эпик", "Описание 3");
        final int epicIdUpdate = taskManager.addNewEpic(epicUpdate);

        taskManager.updateEpicFill(epicUpdate);

        Epic epicAfterUpdate = taskManager.getEpic(epicIdUpdate);

        assertEquals(epicUpdate.getName(), epicAfterUpdate.getName(),
                "Эпики не совпадают по названию.");
        assertEquals(epicUpdate.getDescription(), epicAfterUpdate.getDescription(),
                "Эпики не совпадают по описанию");
        assertEquals(epicUpdate.getStatus(), epicAfterUpdate.getStatus(),
                "Эпики не совпадают по статусу");
        assertEquals(epicUpdate.getSubtaskIds(), epicAfterUpdate.getSubtaskIds(),
                "Саб-задачи эпиков не совпадают");
    }

    @Test
    void shouldRemoveSubtaskAndClearItsIdFromEpic() {
        Subtask subtask = new Subtask(epic1Id, "Первая саб-таска", "Описание", TaskStatus.NEW);
        int subtaskId = taskManager.addNewSubtask(subtask);

        Epic epicBeforeDelete = taskManager.getEpic(epic1Id);
        assertTrue(epicBeforeDelete.getSubtaskIds().contains(subtaskId),
                "ID сабтаски должен быть в эпике.");

        taskManager.deleteSubtaskById(subtaskId);

        Epic epicAfterDelete = taskManager.getEpic(epic1Id);
        assertFalse(epicAfterDelete.getSubtaskIds().contains(subtaskId),
                "ID сабтаски не должен оставаться в эпике.");
    }

    @Test
    void shouldNotAffectTaskManagerWhenTaskIsModifiedAfterAdding() {
        Task task = new Task("Задача", "Описание", TaskStatus.NEW);
        int taskId = taskManager.addNewTask(task);

        task.setName("Изменённое название");
        task.setDescription("Изменённое описание");
        task.setStatus(TaskStatus.DONE);

        Task savedTask = taskManager.getTask(taskId);

        assertEquals("Задача", savedTask.getName(), "Название задачи не должно измениться.");
        assertEquals("Описание", savedTask.getDescription(), "Описание задачи не должно измениться.");
        assertEquals(TaskStatus.NEW, savedTask.getStatus(), "Статус задачи не должен измениться.");
    }

    @Test
    void shouldPreserveSubtasksAfterEpicUpdate() {
        Subtask subtask1 = new Subtask(epic1Id, "Саб-таска 1", "Описание 1", TaskStatus.NEW);
        Subtask subtask2 = new Subtask(epic1Id, "Саб-таска 2", "Описание 2", TaskStatus.NEW);
        int subtask1Id = taskManager.addNewSubtask(subtask1);
        int subtask2Id = taskManager.addNewSubtask(subtask2);

        Epic epicToUpdate = taskManager.getEpic(epic1Id);
        epicToUpdate.setName("Обновлённый эпик");
        epicToUpdate.setDescription("Новое описание");
        taskManager.updateEpicFill(epicToUpdate);

        Epic savedEpic = taskManager.getEpic(epic1Id);
        assertEquals("Обновлённый эпик", savedEpic.getName(), "Название эпика не обновилось.");
        assertEquals(2, savedEpic.getSubtaskIds().size(), "Количество саб-тасок не должно измениться.");
        assertTrue(savedEpic.getSubtaskIds().contains(subtask1Id),
                "Саб-таска 1 должна быть привязана к эпику.");
        assertTrue(savedEpic.getSubtaskIds().contains(subtask2Id),
                "Саб-таска 2 должна быть привязана к эпику.");
    }

    @Test
    void shouldRemoveAllSubtasksWhenEpicIsDeleted() {
        Subtask subtask1 = new Subtask(epic1Id, "Саб-таска 1", "Описание 1", TaskStatus.NEW);
        Subtask subtask2 = new Subtask(epic1Id, "Саб-таска 2", "Описание 2", TaskStatus.NEW);
        taskManager.addNewSubtask(subtask1);
        taskManager.addNewSubtask(subtask2);

        taskManager.deleteEpicById(epic1Id);

        List<Subtask> subtasks = taskManager.getSubtasks();
        assertTrue(subtasks.isEmpty(), "Список саб-тасок должен быть пустым.");

        assertNull(taskManager.getEpic(epic1Id), "Эпик должен быть удалён.");
    }
}
