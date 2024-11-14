package main;

import manager.TaskManager;
import task.Epic;
import task.Subtask;
import task.Task;
import task.TaskStatus;

import java.util.ArrayList;

public class Main {

    public static void main(String[] args) {
        TaskManager taskManager = new TaskManager();

        System.out.println(" ".repeat(18) + "***  Проверка создания задач и вывод ***");
        Task task1 = new Task("Первая", "Описание 1", TaskStatus.NEW);
        taskManager.addNewTask(task1);
        Task task2 = new Task("Вторая", "Описание 2", TaskStatus.DONE);
        taskManager.addNewTask(task2);

        ArrayList<Task> tasks = taskManager.getTasks();
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
        Task task = new Task(taskManager.getEpics().getFirst().getSubtaskIds().getFirst(),
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
    }
}

