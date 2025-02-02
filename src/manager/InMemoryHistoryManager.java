package manager;

import history.HistoryManager;
import history.Node;
import task.Task;

import java.time.LocalDateTime;
import java.util.*;

public class InMemoryHistoryManager implements HistoryManager {
    private final DoublyLinkedList<Task> historyList = new DoublyLinkedList<>();
    private final Map<Integer, Node<Task>> historyMap = new HashMap<>();
    private final Set<Task> prioritizedTasks = new TreeSet<>(taskComparator);

    @Override
    public void add(Task task) {
        if (task == null) return;

        if (historyMap.containsKey(task.getId())) {
            remove(task.getId());
        }

        historyList.linkLast(task);
        historyMap.put(task.getId(), historyList.tail);
    }

    @Override
    public void addToPrioritizedTasks(Task task) {
        if (task.getStartTime() != null) {
            prioritizedTasks.add(task);
        }
    }

    @Override
    public void removePrioritizedTask(Task task) {
        prioritizedTasks.remove(task);
    }

    @Override
    public void remove(int id) {
        Node<Task> taskNode = historyMap.get(id);
        if (taskNode != null) {
            historyList.removeNode(taskNode);
            historyMap.remove(id);
        }
    }

    @Override
    public List<Task> getHistory() {
        System.out.println("История просмотров: ");
        return List.copyOf(historyList.getTasks());
    }

    @Override
    public List<Task> getPrioritizedTasks() {
        return List.copyOf(prioritizedTasks);
    }

    private static class DoublyLinkedList<T> {
        private Node<T> head;
        private Node<T> tail;

        public void linkLast(T task) {
            Node<T> oldTail = tail;
            Node<T> newNode = new Node<>(oldTail, task, null);
            tail = newNode;

            if (oldTail == null)
                head = newNode;
            else
                oldTail.setNext(newNode);
        }

        public List<T> getTasks() {
            List<T> tasks = new ArrayList<>();
            Node<T> current = head;
            while (current != null) {
                tasks.add(current.getData());
                current = current.getNext();
            }
            return tasks;
        }


        public void removeNode(Node<T> node) {
            if (node.getPrev() != null) {
                node.getPrev().setNext(node.getNext());
            } else {
                head = node.getNext();
            }

            if (node.getNext() != null) {
                node.getNext().setPrev(node.getPrev());
            } else {
                tail = node.getPrev();
            }
        }
    }

    private static final Comparator<Task> taskComparator = (o1, o2) -> {
        if (!o1.getStartTime().equals(o2.getStartTime())) {
            return o1.getStartTime().compareTo(o2.getStartTime());
        }

        return Integer.compare(o1.getId(), o2.getId());
    };

    @Override
    public boolean isIntersectionTasks(Task newTask) {
        return getPrioritizedTasks().stream()
                .anyMatch(prioritizedTask -> {
                    LocalDateTime startTime = prioritizedTask.getStartTime();
                    LocalDateTime endTime = prioritizedTask.getEndTime();
                    return newTask.getStartTime().isBefore(endTime) &&
                            newTask.getEndTime().isAfter(startTime);
                });
    }

    @Override
    public boolean isIntersectionTasks(Task newTask, int idAddedTask) {
        return getPrioritizedTasks().stream()
                .filter(task -> task.getId() != idAddedTask)
                .anyMatch(prioritizedTask -> {
                    LocalDateTime startTime = prioritizedTask.getStartTime();
                    LocalDateTime endTime = prioritizedTask.getEndTime();
                    return newTask.getStartTime().isBefore(endTime) &&
                            newTask.getEndTime().isAfter(startTime);
                });
    }
}