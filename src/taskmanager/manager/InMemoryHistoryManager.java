package taskmanager.manager;

import taskmanager.utiltask.Task;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;


public class InMemoryHistoryManager implements HistoryManager {

    private Node head;
    private Node tail;
    private HashMap<Integer, Node> taskMap;

    public InMemoryHistoryManager() {
        taskMap = new HashMap<>();
    }

    @Override
    public void add(Task task) {
        if (task == null) return;
        if (taskMap.containsKey(task.getId())) {
            removeNode(taskMap.get(task.getId()));
        }
        Node newNode = new Node(task);
        linkLast(newNode);
        taskMap.put(task.getId(), newNode);
    }

    @Override
    public void remove(int id) {
        Node nodeToRemove = taskMap.remove(id);
        if (nodeToRemove != null) {
            removeNode(nodeToRemove);
        }
    }

    private void linkLast(Node newNode) {
        if (tail == null) {
            head = tail = newNode;
        } else {
            tail.next = newNode;
            newNode.prev = tail;
            tail = newNode;
        }
    }

    private void removeNode(Node node) {
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
    }

    private List<Task> getTask() {
        List<Task> tasks = new ArrayList<>();
        Node current = head;
        while (current != null) {
            tasks.add(current.task);
            current = current.next;
        }
        return tasks;
    }

    @Override
    public List<Task> getHistory() {
        return getTask();
    }
}
