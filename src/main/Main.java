package main;

import manager.FileBackedTaskManager;
import manager.Managers;
import manager.TaskManager;
import task.Epic;
import task.Subtask;
import task.Task;
import task.TaskStatus;

import java.nio.file.Paths;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.Month;
import java.util.Scanner;

public class Main {

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        System.out.println("""
                Запуск группы тестов:\s
                1 - Проверка TaskManager. Вывода истории просмотров и приоритетных задач
                2 - Проверка FileBackedTaskManager
                3 - Запуск из файла
                \s""" +
                "```".repeat(23));

        String input = scanner.next();

        switch (input) {
            case "1" -> case1();
            case "2" -> case2();
            case "3" -> case3();
            default -> case1();
        }
    }

    public static void case1() {
        TaskManager taskManager = Managers.getDefault();

        System.out.println(" ".repeat(18) + "***  Проверка создания задач и вывод ***");
        taskManager.addNewTask(new Task("Первая", "Описание 1", TaskStatus.NEW,
                LocalDateTime.of(2025, Month.JANUARY, 1, 13, 0), Duration.ofMinutes(120)));
        taskManager.addNewTask(new Task("Вторая", "Описание 2", TaskStatus.DONE,
                LocalDateTime.of(2025, Month.JANUARY, 1, 10, 0), Duration.ofMinutes(180)));
        System.out.println(taskManager.getTasks());

        System.out.println(" ".repeat(18) + "***  Проверка создания эпиков  ***");
        taskManager.addNewEpic(new Epic("Первый эпик", "Описание 1"));
        taskManager.addNewEpic(new Epic("Второй эпик", "Описание 2"));

        System.out.println(" ".repeat(18) + "***  Проверка создания саб-задач и вывод ***");
        taskManager.addNewSubtask(new Subtask(taskManager.getEpics().getFirst().getId(),
                "Первая саб-таска", "Описание1", TaskStatus.NEW,
                LocalDateTime.of(2025, Month.MARCH, 1, 12, 0), Duration.ofMinutes(120)));
        taskManager.addNewSubtask(new Subtask(taskManager.getEpics().getLast().getId(),
                "Вторая саб-таска", "Описание2", TaskStatus.NEW,
                LocalDateTime.of(2025, Month.JANUARY, 26, 12, 0), Duration.ofMinutes(180)));
        taskManager.addNewSubtask(new Subtask(taskManager.getEpics().getLast().getId(),
                "Третья саб-таска", "Описание3", TaskStatus.NEW,
                LocalDateTime.of(2025, Month.MARCH, 1, 20, 15), Duration.ofMinutes(150)));
        System.out.println(taskManager.getSubtasks());

        System.out.println(" ".repeat(18) + "***  Проверка обновления задачи и вывод  ***");
        taskManager.updateTask(new Task(taskManager.getTasks().getFirst().getId(),
                "Четвертая", "Обновленная первая", TaskStatus.DONE,
                LocalDateTime.of(2025, Month.JANUARY, 1, 16, 45), Duration.ofMinutes(180)));
        System.out.println(taskManager.getTasks());

        System.out.println(" ".repeat(18) + "***  Проверка обновления саб-задачи  ***");
        System.out.println(taskManager.getEpicSubtasks(taskManager.getEpics().getFirst().getId()));
        taskManager.updateSubtask(new Subtask(
                taskManager.getEpics().getFirst().getSubtaskIds().getFirst(),
                taskManager.getEpics().getFirst().getId(),
                "Четвертая саб-таска", "Обновленная первая", TaskStatus.DONE,
                LocalDateTime.of(2025, Month.MARCH, 1, 11, 50), Duration.ofMinutes(60)));
        System.out.println(taskManager.getEpicSubtasks(taskManager.getEpics().getFirst().getId()));

        System.out.println(" ".repeat(18) + "***  Проверка обновления эпика и вывод  ***");
        System.out.println(taskManager.getEpics().getFirst());
        taskManager.updateEpicFill(
                new Epic(taskManager.getEpics().getFirst().getId(), "Четвертый", "Обновленный первый"));
        System.out.println(taskManager.getEpics().getFirst());

        System.out.println(" ".repeat(18) + "***  Проверка удаления задачи  ***");
        taskManager.deleteTaskById(taskManager.getTasks().getFirst().getId());
        System.out.println(taskManager.getTasks());

        System.out.println(" ".repeat(18) + "***  Проверка удаления эпика  ***");
        taskManager.deleteEpicById(taskManager.getEpics().getFirst().getId());
        System.out.println(taskManager.getEpics());
        System.out.println(taskManager.getSubtasks());

        System.out.println(" ".repeat(18) + "***  Проверка вывода истории просмотров  ***");
        taskManager.deleteAllTasks();
        taskManager.deleteAllEpics();
        System.out.print(taskManager.getTasks());
        System.out.print(taskManager.getEpics());
        System.out.println(taskManager.getSubtasks());

        int idTask1 = taskManager.addNewTask(new Task("Первая", "Описание 3", TaskStatus.DONE,
                LocalDateTime.of(2025, Month.FEBRUARY, 1, 12, 0), Duration.ofMinutes(120)));
        int idTask2 = taskManager.addNewTask(new Task("Вторая", "Описание 4", TaskStatus.DONE,
                LocalDateTime.of(2025, Month.FEBRUARY, 1, 14, 0), Duration.ofMinutes(180)));
        int idTask3 = taskManager.addNewTask(new Task("Третья", "Описание 5", TaskStatus.DONE,
                LocalDateTime.of(2025, Month.JANUARY, 3, 16, 30), Duration.ofMinutes(240)));
        int idEpic1 = taskManager.addNewEpic(new Epic("Первый эпик", "Описание 3"));
        int idSubtask1 = taskManager.addNewSubtask(new Subtask(idEpic1,
                "Первая саб-таска", "Описание4", TaskStatus.IN_PROGRESS,
                LocalDateTime.of(2025, Month.MARCH, 1, 11, 50), Duration.ofMinutes(60)));

        taskManager.getTask(idTask1);
        taskManager.getTask(idTask3);
        taskManager.getTask(idTask2);
        taskManager.getTask(idTask1); //1
        taskManager.getTask(idTask3); //2
        taskManager.getEpic(idEpic1); //3
        taskManager.getSubtask(idSubtask1); //4
        taskManager.getTask(idTask2); //5
        System.out.println(taskManager.getHistory());

        taskManager.deleteTaskById(taskManager.getTasks().getLast().getId());
        taskManager.deleteTaskById(taskManager.getTasks().getLast().getId());
        taskManager.deleteTaskById(taskManager.getTasks().getLast().getId());
        taskManager.deleteEpicById(idEpic1);
        System.out.println(taskManager.getHistory());

        taskManager.addNewTask(new Task("Шестая", "Описание 6", TaskStatus.NEW,
                LocalDateTime.of(2025, Month.JANUARY, 1, 16, 30), Duration.ofMinutes(240)));

        System.out.println(taskManager.getPrioritizedTasks());
    }

    public static void case2() {
        System.out.println(" ".repeat(18) + "***  Проверка FileBackedTaskManager  ***");
        FileBackedTaskManager fileBackedTaskManager = new FileBackedTaskManager(null);
        fileBackedTaskManager.addNewTask(new Task("Первая", "Описание 1", TaskStatus.NEW,
                LocalDateTime.of(2025, Month.JANUARY, 1, 13, 0), Duration.ofMinutes(120)));
        fileBackedTaskManager.addNewTask(new Task("Вторая", "Описание 2", TaskStatus.DONE,
                LocalDateTime.of(2025, Month.JANUARY, 1, 10, 0), Duration.ofMinutes(180)));
        fileBackedTaskManager.addNewEpic(new Epic("Первый эпик", "Описание 1"));
        fileBackedTaskManager.addNewEpic(new Epic("Второй эпик", "Описание 2"));
        fileBackedTaskManager.addNewSubtask(new Subtask(fileBackedTaskManager.getEpics().getFirst().getId(),
                "Первая саб-таска", "Описание1", TaskStatus.NEW,
                LocalDateTime.of(2025, Month.MARCH, 1, 12, 0), Duration.ofMinutes(120)));
        fileBackedTaskManager.addNewSubtask(new Subtask(fileBackedTaskManager.getEpics().getLast().getId(),
                "Вторая саб-таска", "Описание2", TaskStatus.NEW,
                LocalDateTime.of(2025, Month.JANUARY, 26, 12, 0), Duration.ofMinutes(180)));
        fileBackedTaskManager.addNewSubtask(new Subtask(fileBackedTaskManager.getEpics().getLast().getId(),
                "Третья саб-таска", "Описание3", TaskStatus.NEW,
                LocalDateTime.of(2025, Month.MARCH, 1, 20, 15), Duration.ofMinutes(150)));

        fileBackedTaskManager.updateTask(new Task(fileBackedTaskManager.getTasks().getFirst().getId(),
                "Четвертая", "Обновленная первая", TaskStatus.DONE,
                LocalDateTime.of(2025, Month.JANUARY, 1, 16, 45), Duration.ofMinutes(180)));
        fileBackedTaskManager.updateSubtask(new Subtask(
                fileBackedTaskManager.getEpics().getLast().getSubtaskIds().getLast(),
                fileBackedTaskManager.getEpics().getLast().getId(),
                "Третья саб-таска", "Обновленная3", TaskStatus.IN_PROGRESS,
                LocalDateTime.of(2025, Month.MARCH, 1, 20, 15), Duration.ofMinutes(150)));

        fileBackedTaskManager.deleteTaskById(fileBackedTaskManager.getTasks().getLast().getId());
        fileBackedTaskManager.deleteEpicById(fileBackedTaskManager.getEpics().getFirst().getId());

        fileBackedTaskManager.addNewTask(new Task("Пятая", "Описание 5", TaskStatus.NEW,
                LocalDateTime.of(2025, Month.JANUARY, 3, 16, 30), Duration.ofMinutes(240)));

        System.out.println(fileBackedTaskManager.getTasks());
        System.out.println(fileBackedTaskManager.getEpics());
        System.out.println(fileBackedTaskManager.getSubtasks());
    }

    public static void case3() {
        System.out.println(" ".repeat(18) + "***  Проверка FileBackedTaskManager  ***");
        FileBackedTaskManager fileBackedTaskManager =
                FileBackedTaskManager.loadFromFile(Paths.get("src/history/autoSave.csv").toFile());
        System.out.println(fileBackedTaskManager.getTasks());
        System.out.println(fileBackedTaskManager.getEpics());
        System.out.println(fileBackedTaskManager.getSubtasks());

        fileBackedTaskManager.updateTask(new Task(fileBackedTaskManager.getTasks().getFirst().getId(),
                "Снова первая", "Обновленная 4", TaskStatus.DONE,
                LocalDateTime.of(2025, Month.JANUARY, 1, 9, 45), Duration.ofMinutes(180)));

        fileBackedTaskManager.updateSubtask(new Subtask(
                fileBackedTaskManager.getEpics().getFirst().getSubtaskIds().getLast(),
                fileBackedTaskManager.getEpics().getFirst().getId(),
                "Пятая саб-таска", "Описание3", TaskStatus.DONE,
                LocalDateTime.of(2025, Month.MARCH, 1, 23, 15), Duration.ofMinutes(150)));

        System.out.println(fileBackedTaskManager.getTasks());
        System.out.println(fileBackedTaskManager.getEpics());
        System.out.println(fileBackedTaskManager.getSubtasks());
        System.out.println(fileBackedTaskManager.getPrioritizedTasks());
    }
}