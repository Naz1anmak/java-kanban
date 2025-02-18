package manager;

import history.HistoryManager;
import history.Node;
import task.Task;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class InMemoryHistoryManager implements HistoryManager {
    private final DoublyLinkedList<Task> historyList = new DoublyLinkedList<>();
    private final Map<Integer, Node<Task>> historyMap = new HashMap<>();

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
    public void remove(int id) {
        Node<Task> taskNode = historyMap.get(id);
        if (taskNode != null) {
            historyList.removeNode(taskNode);
            historyMap.remove(id);
        }
    }

    @Override
    public List<Task> getHistory() {
        return List.copyOf(historyList.getTasks());
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
}