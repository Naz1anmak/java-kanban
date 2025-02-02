import exception.TaskIntersectionException;
import manager.TaskManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import task.Epic;
import task.Subtask;
import task.Task;
import task.TaskStatus;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.Month;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

abstract class TaskManagerTest<T extends TaskManager> {
    protected T taskManager;
    protected Task task1, task2;
    protected Epic epic1, epic2;
    protected Subtask subtask1, subtask2, subtask3;

    @BeforeEach
    public void setUp() {
        taskManager = createTaskManager();

        task1 = new Task("Первая", "Описание 1", TaskStatus.NEW,
                LocalDateTime.of(2025, Month.JANUARY, 1, 13, 0), Duration.ofMinutes(120));
        task2 = new Task("Вторая", "Описание 2", TaskStatus.DONE,
                LocalDateTime.of(2025, Month.JANUARY, 1, 10, 0), Duration.ofMinutes(180));

        epic1 = new Epic("Первый эпик", "Описание 1");
        epic2 = new Epic("Второй эпик", "Описание 2");

        subtask1 = new Subtask(-1,
                "Первая подзадача", "Описание 1", TaskStatus.NEW,
                LocalDateTime.of(2025, Month.MARCH, 1, 12, 0), Duration.ofMinutes(120));
        subtask2 = new Subtask(-1,
                "Вторая подзадача", "Описание 2", TaskStatus.NEW,
                LocalDateTime.of(2025, Month.JANUARY, 26, 12, 0), Duration.ofMinutes(180));
        subtask3 = new Subtask(-1,
                "Третья подзадача", "Описание 3", TaskStatus.DONE,
                LocalDateTime.of(2025, Month.MARCH, 1, 20, 15), Duration.ofMinutes(150));
    }

    protected abstract T createTaskManager();

    @Test
    void testAddAndGetTasks() {
        int taskId1 = taskManager.addNewTask(task1);
        int taskId2 = taskManager.addNewTask(task2);
        Task savedTask1 = taskManager.getTask(taskId1);
        Task savedTask2 = taskManager.getTask(taskId2);

        assertNotNull(savedTask1, "Задача 1 не найдена.");
        assertNotNull(savedTask2, "Задача 2 не найдена.");

        assertEquals(task1.getName(), savedTask1.getName(), "Названия задачи 1 не совпадают.");
        assertEquals(task1.getDescription(), savedTask1.getDescription(), "Описания задачи 1 не совпадают.");
        assertEquals(task1.getStatus(), savedTask1.getStatus(), "Статусы задачи 1 не совпадают.");
        assertEquals(task1.getStartTime(), savedTask1.getStartTime(), "Время начала задачи 1 не совпадает.");
        assertEquals(task1.getDuration(), savedTask1.getDuration(), "Продолжительность задачи 1 не совпадает.");

        List<Task> tasks = taskManager.getTasks();
        assertNotNull(tasks, "Список задач не должен быть null.");
        assertEquals(2, tasks.size(), "Должны быть две задачи.");
        assertEquals(task1.getName(), tasks.getFirst().getName(), "Название первой задачи не совпадает.");
        assertEquals(task2.getName(), tasks.getLast().getName(), "Название второй задачи не совпадает.");

        Task nonExistentTask = taskManager.getTask(3);
        assertNull(nonExistentTask, "Задача с несуществующим ID должна быть null.");
    }

    @Test
    void testAddAndGetEpics() {
        int epicId1 = taskManager.addNewEpic(epic1);
        int epicId2 = taskManager.addNewEpic(epic2);

        Epic savedEpic1 = taskManager.getEpic(epicId1);
        Epic savedEpic2 = taskManager.getEpic(epicId2);

        assertNotNull(savedEpic1, "Эпик 1 не найден.");
        assertNotNull(savedEpic2, "Эпик 2 не найден.");

        assertEquals(epic1.getName(), savedEpic1.getName(), "Названия эпика 1 не совпадают.");
        assertEquals(epic1.getDescription(), savedEpic1.getDescription(), "Описания эпика 1 не совпадают.");
        assertEquals(TaskStatus.NEW, savedEpic1.getStatus(), "Статус эпика 1 должен быть NEW.");

        List<Epic> epics = taskManager.getEpics();
        assertNotNull(epics, "Список эпиков не должен быть null.");
        assertEquals(2, epics.size(), "Должны быть два эпика.");
        assertEquals(epic1.getName(), epics.getFirst().getName(), "Название первого эпика не совпадает.");
        assertEquals(epic2.getName(), epics.getLast().getName(), "Название второго эпика не совпадает.");

        Epic nonExistentEpic = taskManager.getEpic(3);
        assertNull(nonExistentEpic, "Эпик с несуществующим ID должен быть null.");
    }

    @Test
    void testAddAndGetSubtasks() {
        int epicId1 = taskManager.addNewEpic(epic1);
        int epicId2 = taskManager.addNewEpic(epic2);

        subtask1.setIdEpic(epicId1);
        subtask2.setIdEpic(epicId2);
        int subtaskId1 = taskManager.addNewSubtask(subtask1);
        int subtaskId2 = taskManager.addNewSubtask(subtask2);

        Subtask savedSubtask1 = taskManager.getSubtask(subtaskId1);
        Subtask savedSubtask2 = taskManager.getSubtask(subtaskId2);

        assertNotNull(savedSubtask1, "Подзадача 1 не найдена.");
        assertNotNull(savedSubtask2, "Подзадача 2 не найдена.");

        assertEquals(subtask1.getName(), savedSubtask1.getName(), "Названия подзадачи 1 не совпадают.");
        assertEquals(subtask1.getDescription(), savedSubtask1.getDescription(), "Описания подзадачи 1 не совпадают.");
        assertEquals(subtask1.getStatus(), savedSubtask1.getStatus(), "Статусы подзадачи 1 не совпадают.");
        assertEquals(subtask1.getStartTime(), savedSubtask1.getStartTime(), "Время начала подзадачи 1 не совпадает.");
        assertEquals(subtask1.getDuration(), savedSubtask1.getDuration(), "Продолжительность подзадачи 1 не совпадает.");

        List<Subtask> subtasks = taskManager.getSubtasks();
        assertNotNull(subtasks, "Список подзадач не должен быть null.");
        assertEquals(2, subtasks.size(), "Должны быть две подзадачи.");
        assertEquals(subtask1.getName(), subtasks.getFirst().getName(), "Название первой подзадачи не совпадает.");
        assertEquals(subtask2.getName(), subtasks.getLast().getName(), "Название второй подзадачи не совпадает.");

        Epic epic = taskManager.getEpics().getFirst();
        assertEquals(1, epic.getSubtaskIds().size(),
                "Первый эпик должен иметь одну подзадачу.");
        assertEquals(subtaskId1, epic.getSubtaskIds().getFirst(), "id подзадачи должно совпадать.");

        Subtask nonExistentSubtask = taskManager.getSubtask(5);
        assertNull(nonExistentSubtask, "Подзадача с несуществующим ID должна быть null.");
    }

    @Test
    void testAddTaskWithoutStartTimeOrDuration() {
        Task taskWithoutTime = new Task(
                "Без времени", "Описание", TaskStatus.NEW, null, null);

        TaskIntersectionException exception = assertThrows(TaskIntersectionException.class, () -> {
            taskManager.addNewTask(taskWithoutTime);
        });

        assertEquals("В задаче отсутствует время начала или окончания.", exception.getMessage());
    }

    @Test
    void testTaskIntersectionException() {
        taskManager.addNewTask(task1);

        Task overlappingTask = new Task("Вторая", "Описание 2", TaskStatus.DONE,
                LocalDateTime.of(2025, Month.JANUARY, 1, 13, 30), Duration.ofMinutes(60));

        TaskIntersectionException exception = assertThrows(TaskIntersectionException.class, () -> {
            taskManager.addNewTask(overlappingTask);
        });

        assertEquals("Задача \"Вторая\" пересекается по времени с другой задачей!", exception.getMessage());
    }

    @Test
    void testHistoryAfterGettingTasks() {
        int taskId1 = taskManager.addNewTask(task1);
        int taskId2 = taskManager.addNewTask(task2);

        taskManager.getTask(taskId1);
        taskManager.getTask(taskId2);
        taskManager.getTask(taskId1);

        List<Task> history = taskManager.getHistory();
        assertNotNull(history, "История просмотров не должна быть null.");
        assertEquals(2, history.size(), "История должна содержать две задачи.");
        assertEquals(task1.getName(), history.getLast().getName(),
                "Последняя просмотренная задача должна быть последней в истории.");
        assertEquals(task2.getName(), history.getFirst().getName(),
                "Первая просмотренная задача должна быть первой в истории.");
    }

    @Test
    void shouldNotAllowEpicToBeItsOwnSubtask() {
        int epic1Id = taskManager.addNewEpic(epic1);
        Subtask invalidSubtask = new Subtask(
                epic1Id, "Некорректная подзадача", "Описание", TaskStatus.NEW,
                LocalDateTime.of(2025, Month.JANUARY, 1, 13, 30), Duration.ofMinutes(60));
        invalidSubtask.setId(epic1Id);
        int result = taskManager.addNewSubtask(invalidSubtask);

        assertEquals(-1, result, "Подзадача не должна быть добавлена, если её id совпадает с id эпика.");
        assertEquals(0, taskManager.getSubtasks().size(), "Подзадач не должно быть добавлено.");
    }

    @Test
    void shouldNotAllowSubtaskToBeItsOwnEpic() {
        subtask1.setIdEpic(subtask1.getId());
        taskManager.addNewSubtask(subtask1);

        assertEquals(0, taskManager.getSubtasks().size(),
                "Подзадача с некорректным ID не должна добавляться.");
    }

    @Test
    void testEpicStatusWhenAllSubtasksAreNew() {
        int epicId = taskManager.addNewEpic(epic1);
        subtask1.setIdEpic(epicId);
        subtask2.setIdEpic(epicId);
        taskManager.addNewSubtask(subtask1);
        taskManager.addNewSubtask(subtask2);

        Epic updatedEpic = taskManager.getEpic(epicId);

        assertEquals(TaskStatus.NEW, updatedEpic.getStatus(),
                "Статус эпика должен быть NEW, если все подзадачи имеют статус NEW.");
    }

    @Test
    void testEpicStatusWhenAllSubtasksAreDone() {
        int epicId = taskManager.addNewEpic(epic1);
        subtask3.setIdEpic(epicId);
        Subtask subtask4 = new Subtask(epicId, "Дополнительная подзадача", "Описание 1", TaskStatus.DONE,
                LocalDateTime.of(2025, Month.MARCH, 1, 12, 0), Duration.ofMinutes(120));
        taskManager.addNewSubtask(subtask3);
        taskManager.addNewSubtask(subtask4);

        Epic updatedEpic = taskManager.getEpic(epicId);

        assertEquals(TaskStatus.DONE, updatedEpic.getStatus(),
                "Статус эпика должен быть DONE, если все подзадачи имеют статус DONE.");
    }

    @Test
    void testEpicStatusWhenSubtasksAreNewAndDone() {
        int epicId = taskManager.addNewEpic(epic1);
        subtask2.setIdEpic(epicId);
        subtask3.setIdEpic(epicId);
        taskManager.addNewSubtask(subtask2);
        taskManager.addNewSubtask(subtask3);

        Epic updatedEpic = taskManager.getEpic(epicId);

        assertEquals(TaskStatus.IN_PROGRESS, updatedEpic.getStatus(),
                "Статус эпика должен быть IN_PROGRESS, если подзадачи имеют статусы NEW и DONE.");
    }

    @Test
    void testEpicStatusWhenSubtasksAreInProgress() {
        int epicId = taskManager.addNewEpic(epic1);
        subtask2.setIdEpic(epicId);
        subtask3.setIdEpic(epicId);
        taskManager.addNewSubtask(subtask2);
        taskManager.addNewSubtask(subtask3);

        Epic updatedEpic = taskManager.getEpic(epicId);

        assertEquals(TaskStatus.IN_PROGRESS, updatedEpic.getStatus(),
                "Статус эпика должен быть IN_PROGRESS, если подзадачи имеют статус IN_PROGRESS.");
    }

    @Test
    void shouldRemoveSubtaskAndClearItsIdFromEpic() {
        int epicId = taskManager.addNewEpic(epic1);
        subtask1.setIdEpic(epicId);
        int subtaskId = taskManager.addNewSubtask(subtask1);

        Epic epicBeforeDelete = taskManager.getEpic(epicId);
        assertTrue(epicBeforeDelete.getSubtaskIds().contains(subtaskId),
                "Id подзадачи должен быть в эпике.");

        taskManager.deleteSubtaskById(subtaskId);

        Epic epicAfterDelete = taskManager.getEpic(epicId);
        assertEquals(0, taskManager.getSubtasks().size(), "Список подзадач должен быть пуст.");
        assertFalse(epicAfterDelete.getSubtaskIds().contains(subtaskId),
                "Id подзадачи не должен оставаться в эпике.");
    }

    @Test
    void shouldNotAffectTaskManagerWhenTaskIsModifiedAfterAdding() {
        int taskId = taskManager.addNewTask(task1);
        task1.setName("Изменённое название");
        task1.setDescription("Изменённое описание");
        task1.setStatus(TaskStatus.DONE);

        Task savedTask = taskManager.getTask(taskId);

        assertEquals("Первая", savedTask.getName(), "Название задачи не должно измениться.");
        assertEquals("Описание 1", savedTask.getDescription(), "Описание задачи не должно измениться.");
        assertEquals(TaskStatus.NEW, savedTask.getStatus(), "Статус задачи не должен измениться.");
    }

    @Test
    void shouldPreserveSubtasksAfterEpicUpdate() {
        int epicId = taskManager.addNewEpic(epic1);
        subtask2.setIdEpic(epicId);
        subtask3.setIdEpic(epicId);
        int subtask2Id = taskManager.addNewSubtask(subtask2);
        int subtask3Id = taskManager.addNewSubtask(subtask3);
        epic1 = taskManager.getEpic(epicId);

        taskManager.updateEpicFill(epic1, new Epic("Обновлённый эпик", "Новое описание"));

        Epic savedEpic = taskManager.getEpic(epicId);

        assertEquals("Обновлённый эпик", savedEpic.getName(), "Название эпика не обновилось.");
        assertEquals(TaskStatus.IN_PROGRESS, savedEpic.getStatus(), "Статус эпика должен быть IN_PROGRESS.");
        assertEquals(2, savedEpic.getSubtaskIds().size(), "Количество подзадач не должно измениться.");
        assertTrue(savedEpic.getSubtaskIds().contains(subtask2Id),
                "Вторая подзадача должна быть привязана к эпику.");
        assertTrue(savedEpic.getSubtaskIds().contains(subtask3Id),
                "Третья подзадача должна быть привязана к эпику.");
    }

    @Test
    void shouldRemoveAllSubtasksWhenEpicIsDeleted() {
        int epicId = taskManager.addNewEpic(epic1);
        subtask2.setIdEpic(epicId);
        subtask3.setIdEpic(epicId);
        taskManager.addNewSubtask(subtask2);
        taskManager.addNewSubtask(subtask3);

        taskManager.deleteEpicById(epicId);

        List<Subtask> subtasks = taskManager.getSubtasks();
        List<Epic> epics = taskManager.getEpics();
        assertTrue(subtasks.isEmpty(), "Список подзадач должен быть пустым.");
        assertTrue(epics.isEmpty(), "Список эпиков должен быть пустым.");
    }
}
