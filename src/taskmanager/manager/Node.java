package taskmanager.manager;

import taskmanager.utiltask.Task;

class Node {
    Task task;
    Node prev;
    Node next;

    Node(Task task) {
        this.task = task;
    }
}
