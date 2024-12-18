package manager;

import history.HistoryManager;
import history.Node;
import task.Task;

import java.util.*;

public class InMemoryHistoryManager implements HistoryManager {
    private final DoublyLinkedList<Task> historyList = new DoublyLinkedList<>();
    private final Map<Integer, Node<Task>> historyMap = new HashMap<>();

    public void add(Task task) {
        if (task == null) return;

        if (historyMap.containsKey(task.getId())) {
            remove(task.getId());
        }

        historyList.linkLast(task);
        historyMap.put(task.getId(), historyList.tail);
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

    public static class DoublyLinkedList<T> {
        public Node<T> head;
        public Node<T> tail;
        private int size = 0;

        public void linkLast(T task) {
            Node<T> oldTail = tail;
            Node<T> newNode = new Node<>(oldTail, task, null);
            tail = newNode;

            if (oldTail == null)
                head = newNode;
            else
                oldTail.next = newNode;
            size++;
        }

        public List<T> getTasks() {
            List<T> tasks = new ArrayList<>(size);
            Node<T> current = head;
            while (current != null) {
                tasks.add(current.data);
                current = current.next;
            }
            return tasks;
        }


        public void removeNode(Node<T> node) {
            if (node.prev != null) {
                node.prev.next = node.next;
            } else {
                head = node.next;
            }

            if (node.next != null) {
                node.next.prev = node.prev;
            } else {
                tail = node.prev;
            }

            size--;
        }
    }
}