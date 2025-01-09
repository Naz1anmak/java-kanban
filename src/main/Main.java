package main;

import manager.FileBackedTaskManager;
import manager.Managers;
import manager.TaskManager;
import task.Epic;
import task.Subtask;
import task.Task;
import task.TaskStatus;

import java.nio.file.Paths;
import java.util.List;
import java.util.Scanner;

public class Main {

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        System.out.print("Запуск группы тестов ( 1 / 2 / 3 ): ");
        int choice = scanner.nextInt();

        switch (choice) {
            case 1 -> case1();
            case 2 -> case2();
            case 3 -> case3();
        }
    }

    public static void case1() {
        TaskManager taskManager = Managers.getDefault();

        System.out.println(" ".repeat(18) + "***  Проверка создания задач и вывод ***");
        Task task1 = new Task("Первая", "Описание 1", TaskStatus.NEW);
        taskManager.addNewTask(task1);
        Task task2 = new Task("Вторая", "Описание 2", TaskStatus.DONE);
        taskManager.addNewTask(task2);

        List<Task> tasks = taskManager.getTasks();
        System.out.println(tasks);

        System.out.println(" ".repeat(18) + "***  Проверка создания эпиков  ***");
        Epic epic1 = new Epic("Первый эпик", "Описание 1");
        taskManager.addNewEpic(epic1);
        Epic epic2 = new Epic("Второй эпик", "Описание 2");
        taskManager.addNewEpic(epic2);

        System.out.println(" ".repeat(18) + "***  Проверка создания саб-задач и вывод ***");
        Subtask subtask1 = new Subtask(taskManager.getEpics().getFirst().getId(),
                "Первая саб-таска", "Описание1", TaskStatus.NEW);
        taskManager.addNewSubtask(subtask1);
        Subtask subtask2 = new Subtask(taskManager.getEpics().getFirst().getId(),
                "Вторая саб-таска", "Описание2", TaskStatus.NEW);
        taskManager.addNewSubtask(subtask2);
        Subtask subtask3 = new Subtask(taskManager.getEpics().getLast().getId(),
                "Третья саб-таска", "Описание3", TaskStatus.NEW);
        taskManager.addNewSubtask(subtask3);
        System.out.println(taskManager.getEpics());

        System.out.println(" ".repeat(18) + "***  Проверка обновления задачи и вывод  ***");
        Task task = new Task(taskManager.getTasks().getFirst().getId(),
                "Четвертая", "Обновленная", TaskStatus.DONE);
        taskManager.updateTask(task);
        System.out.println(taskManager.getTasks());

        System.out.println(" ".repeat(18) + "***  Проверка обновления саб-задачи  ***");
        System.out.println(taskManager.getEpics());
        System.out.println(taskManager.getAllLinkedSubtasks(taskManager.getEpics().getFirst().getId()));
        Subtask newSubtask = new Subtask(taskManager.getEpics().getFirst().getSubtaskIds().getFirst(),
                "Четвертая саб-таска", "Обновленная", TaskStatus.DONE);
        taskManager.updateSub(subtask1, newSubtask);
        System.out.println(taskManager.getAllLinkedSubtasks(taskManager.getEpics().getFirst().getId()));
        System.out.println(taskManager.getEpics());

        System.out.println(" ".repeat(18) + "***  Проверка удаления задачи  ***");
        taskManager.deleteTaskById(taskManager.getTasks().getFirst().getId());
        System.out.println(taskManager.getTasks());

        System.out.println(" ".repeat(18) + "***  Проверка удаления эпика  ***");
        taskManager.deleteEpicById(taskManager.getEpics().getFirst().getId());
        System.out.println(taskManager.getEpics());

        System.out.println(" ".repeat(18) + "***  Проверка вывода истории  ***");
        Task task3 = new Task("Третья", "Описание 3", TaskStatus.DONE);
        taskManager.addNewTask(task3);
        Task task4 = new Task("Четвертая", "Описание 4", TaskStatus.DONE);
        taskManager.addNewTask(task4);
        Task task5 = new Task("Пятая", "Описание 5", TaskStatus.DONE);
        taskManager.addNewTask(task5);
        Epic epic3 = new Epic("Третий эпик", "Описание 3");
        taskManager.addNewEpic(epic3);
        Subtask subtask4 = new Subtask(epic3.getId(),
                "Четвертая саб-таска", "Описание4", TaskStatus.NEW);
        taskManager.addNewSubtask(subtask4);
        taskManager.getTask(taskManager.getTasks().getFirst().getId());
        taskManager.getTask(taskManager.getTasks().getLast().getId());
        taskManager.getTask(taskManager.getTasks().getLast().getId() - 1);
        taskManager.getTask(taskManager.getTasks().getLast().getId() - 2);
        taskManager.getTask(taskManager.getTasks().getFirst().getId());
        taskManager.getTask(taskManager.getTasks().getLast().getId());
        taskManager.getEpic(epic3.getId());
        taskManager.getSubtask(subtask4.getId());
        System.out.println(taskManager.getHistory());

        taskManager.deleteTaskById(taskManager.getTasks().getLast().getId());
        taskManager.deleteTaskById(taskManager.getTasks().getLast().getId());
        taskManager.deleteTaskById(taskManager.getTasks().getLast().getId());
        taskManager.deleteEpicById(epic3.getId());
        System.out.println(taskManager.getHistory());
    }

    public static void case2() {
        System.out.println(" ".repeat(18) + "***  Проверка FileBackedTaskManager  ***");
        /*Path saveFilePath = Paths.get("src/history/autoSave.csv");
        FileBackedTaskManager fileBackedTaskManager = new FileBackedTaskManager(saveFilePath);*/

        FileBackedTaskManager fileBackedTaskManager = new FileBackedTaskManager(null);
        Task task1 = new Task("Первая", "Описание 1", TaskStatus.NEW);
        fileBackedTaskManager.addNewTask(task1);
        Task task2 = new Task("Вторая", "Описание 2", TaskStatus.DONE);
        fileBackedTaskManager.addNewTask(task2);
        Epic epic1 = new Epic("Первый эпик", "Описание 1");
        fileBackedTaskManager.addNewEpic(epic1);
        Epic epic2 = new Epic("Второй эпик", "Описание 2");
        fileBackedTaskManager.addNewEpic(epic2);
        Subtask subtask1 = new Subtask(fileBackedTaskManager.getEpics().getFirst().getId(),
                "Первая саб-таска", "Описание1", TaskStatus.NEW);
        fileBackedTaskManager.addNewSubtask(subtask1);
        Subtask subtask2 = new Subtask(fileBackedTaskManager.getEpics().getFirst().getId(),
                "Вторая саб-таска", "Описание2", TaskStatus.NEW);
        fileBackedTaskManager.addNewSubtask(subtask2);
        Subtask subtask3 = new Subtask(fileBackedTaskManager.getEpics().getLast().getId(),
                "Третья саб-таска", "Описание3", TaskStatus.NEW);
        fileBackedTaskManager.addNewSubtask(subtask3);

        Task task = new Task(fileBackedTaskManager.getTasks().getFirst().getId(),
                "Четвертая", "Обновленная", TaskStatus.DONE);
        fileBackedTaskManager.updateTask(task);

        fileBackedTaskManager.deleteTaskById(fileBackedTaskManager.getTasks().getLast().getId());
        fileBackedTaskManager.deleteEpicById(fileBackedTaskManager.getEpics().getFirst().getId());

        Task task5 = new Task("Пятая", "Описание 5", TaskStatus.NEW);
        fileBackedTaskManager.addNewTask(task5);

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
    }
}