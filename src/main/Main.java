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

        System.out.println(" ".repeat(18) + "***  Проверка создания задач  ***");
        taskManager.addNewTask("Первая", "Описание 1", TaskStatus.NEW);
        taskManager.addNewTask("Вторая", "Описание 2", TaskStatus.DONE);

        System.out.println(" ".repeat(18) + "  ***Проверка вывода тасок  ***");
        ArrayList<Task> tasks = taskManager.getTasks();
        System.out.println(tasks);

        System.out.println(" ".repeat(18) + "***  Проверка создания эпиков  ***");
        taskManager.addNewEpic("Первый эпик", "Описание 1");
        taskManager.addNewEpic("Второй эпик", "Описание 2");

        System.out.println(" ".repeat(18) + "***  Проверка создания саб-задач  ***");
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
        System.out.println(taskManager.getSubtasks(3));
        Subtask subtask = new Subtask(3, 5, "Четвертая саб-таска", "Обновленная", TaskStatus.DONE);
        taskManager.updateSub(subtask);
        System.out.println(taskManager.getSubtasks(3));
        System.out.println(taskManager.getEpics());

        System.out.println(" ".repeat(18) + "***  Проверка удаления задачи  ***");
        taskManager.deleteTaskById(taskManager.getTasks().getFirst().getId());
        System.out.println(taskManager.getTasks());

        System.out.println(" ".repeat(18) + "***  Проверка удаления эпика  ***");
        taskManager.deleteEpicById(taskManager.getEpics().getFirst().getId());
        System.out.println(taskManager.getEpics());
    }
}

