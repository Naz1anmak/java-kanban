import java.util.Scanner;

public class Main {

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);

        while (true) {
            printMenu();

            if (!scanner.hasNextInt()) {
                System.out.println("Введена неверная команда. Завершение программы.");
                break;
            }

            int code = scanner.nextInt();
            scanner.nextLine();

            switch (code) {
                case 1:
                    TaskManager.addNewTask(new Task("Первая", "Описание 1"));
                    TaskManager.addNewTask(new Task("Вторая", "Описание 2"));
                    TaskManager.addNewTask(new Task("Третья", "Описание 3"));
                    break;
                case 2:
                    if (TaskManager.isTasksEmpty()) break;
                    TaskManager.getTasks();
                    break;
                case 3:
                    if (TaskManager.isTasksEmpty()) break;
                    System.out.print("Обновить имя/описание (1) или статус (2): ");
                    int choice = scanner.nextInt();
                    if (choice != 1 && choice != 2) {
                        System.out.println("Введено неверное значение!");
                        break;
                    }

                    System.out.print("Введите идентификатор задачи: ");
                    int setId = scanner.nextInt();
                    scanner.nextLine();
                    if (!(TaskManager.isTaskThere(setId))) break;

                    switch (choice) {
                        case 1:
                            System.out.print("Новое имя задачи: ");
                            String newName = scanner.nextLine();
                            System.out.print("Новое описание: ");
                            String newDescription = scanner.nextLine();
                            TaskManager.updateTaskFill(setId, newName, newDescription);
                            break;
                        case 2:
                            TaskManager.updateTaskStatus(setId);
                            break;
                    }
                    break;
                case 4:
                    if (TaskManager.isTasksEmpty()) break;
                    System.out.print("Введите идентификатор задачи: ");
                    int id = scanner.nextInt();
                    TaskManager.findTask(id);
                    break;
                case 5:
                    if (TaskManager.isTasksEmpty()) break;
                    System.out.print("Введите id задачи: ");
                    int deleteId = scanner.nextInt();
                    if (!(TaskManager.isTaskThere(deleteId))) break;

                    TaskManager.deleteTaskById(deleteId);
                    break;
                case 6:
                    TaskManager.deleteTasks();
                    break;
                case 7:
                    if (TaskManager.isEpicsEmpty()) break;
                    System.out.print("В какой эпик добавляем саб-задачу? Введите его id: ");
                    int idOfEpic = scanner.nextInt();
                    scanner.nextLine();
                    if (!(TaskManager.isEpicThere(idOfEpic))) break;

                    System.out.print("Название саб-задачи: ");
                    String name = scanner.nextLine();
                    System.out.print("Введите описание: ");
                    String description = scanner.nextLine();
                    TaskManager.addNewSubtask(idOfEpic, new Subtask(idOfEpic, name, description));
                    break;
                case 8:
                    if (TaskManager.isEpicsEmpty()) break;
                    if (TaskManager.isSubsEmpty()) break;

                    System.out.print("Введите id нужного эпика: ");
                    int idEpic = scanner.nextInt();
                    scanner.nextLine();
                    if (!(TaskManager.isEpicThere(idEpic))) break;

                    TaskManager.getSubtasks(idEpic);
                    break;
                case 9:
                    if (TaskManager.isEpicsEmpty()) break;
                    if (TaskManager.isSubsEmpty()) break;

                    System.out.print("Обновить имя/описание (1) или статус (2): ");
                    choice = scanner.nextInt();
                    if (choice != 1 && choice != 2) {
                        System.out.println("Введено неверное значение!");
                        break;
                    }

                    System.out.print("Введите id саб-задачи: ");
                    setId = scanner.nextInt();
                    scanner.nextLine();
                    if (!(TaskManager.isSubThere(setId))) break;

                    switch (choice) {
                        case 1:
                            System.out.print("Новое имя саб-задачи: ");
                            String newName = scanner.nextLine();
                            System.out.print("Новое описание: ");
                            String newDescription = scanner.nextLine();
                            TaskManager.updateSubFill(setId, newName, newDescription);
                            break;
                        case 2:
                            TaskManager.updateSubStatus(setId);
                            break;
                    }
                    break;
                case 10:
                    if (TaskManager.isEpicsEmpty()) break;
                    if (TaskManager.isSubsEmpty()) break;

                    System.out.print("Введите идентификатор саб-задачи: ");
                    id = scanner.nextInt();
                    TaskManager.findSub(id);
                    break;
                case 11:
                    if (TaskManager.isEpicsEmpty()) break;
                    if (TaskManager.isSubsEmpty()) break;

                    System.out.print("Введите id саб-задачи: ");
                    id = scanner.nextInt();
                    if (!(TaskManager.isSubThere(id))) break;

                    TaskManager.deleteSubtaskById(id);
                    break;
                case 12:
                    TaskManager.deleteAllSubtasks();
                    break;
                case 13:
                    TaskManager.addNewEpic(new Epic("Первый эпик", "Описание 1"));
                    TaskManager.addNewEpic(new Epic("Второй эпик", "Описание 2"));
                    break;
                case 14:
                    if (TaskManager.isEpicsEmpty()) break;
                    TaskManager.getEpics();
                    break;
                case 15:
                    if (TaskManager.isEpicsEmpty()) break;

                    System.out.print("Введите id эпика: ");
                    int setIdEpic = scanner.nextInt();
                    scanner.nextLine();
                    if (!(TaskManager.isEpicThere(setIdEpic))) break;
                    System.out.print("Новое имя эпика: ");
                    String newName = scanner.nextLine();
                    System.out.print("Новое описание: ");
                    String newDescription = scanner.nextLine();

                    TaskManager.updateEpicFill(setIdEpic, newName, newDescription);
                    break;
                case 16:
                    if (TaskManager.isEpicsEmpty()) break;
                    System.out.print("Введите идентификатор эпика: ");
                    id = scanner.nextInt();
                    TaskManager.findEpic(id);
                    break;
                case 17:
                    if (TaskManager.isEpicsEmpty()) break;

                    System.out.print("Введите id эпика: ");
                    id = scanner.nextInt();
                    if (!(TaskManager.isEpicThere(id))) break;

                    TaskManager.deleteEpicById(id);
                    break;
                case 18:
                    TaskManager.deleteAllEpics();
                    break;
                default:
                    System.out.println("Введена неверная команда. Завершение программы.");
                    return;
            }
        }
    }

    static void printMenu() {
        System.out.println("-".repeat(38));
        System.out.println("1. Добавить задачу");
        System.out.println("2. Посмотреть задачи");
        System.out.println("3. Обновление задачи");
        System.out.println("4. Найти задачу по идентификатору");
        System.out.println("5. Удаление задачи по идентификатору");
        System.out.println("6. Удалить все задачи");
        System.out.println("             -     -     -");
        System.out.println("7. Добавить саб-задачу");
        System.out.println("8. Посмотреть саб-задачи");
        System.out.println("9. Обновление саб-задачи");
        System.out.println("10. Найти саб-задачу по идентификатору");
        System.out.println("11. Удаление саб-задачи по идентификатору");
        System.out.println("12. Удалить все саб-задачи");
        System.out.println("             -     -     -");
        System.out.println("13. Добавить эпик");
        System.out.println("14. Посмотреть эпики");
        System.out.println("15. Обновление эпика");
        System.out.println("16. Найти эпик по идентификатору");
        System.out.println("17. Удаление эпика по идентификатору");
        System.out.println("18. Удалить все эпики");
    }
}
