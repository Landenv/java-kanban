package taskmanager.utiltask;

import java.util.HashMap;

public class Epic extends Task {
    private final HashMap<Integer, Subtask> subtasks;

    public Epic(String title, String description) {
        super(title, description);
        this.subtasks = new HashMap<>();
    }

    public HashMap<Integer, Subtask> getSubtasks() {
        return subtasks;
    }

    public void addSubtask(Subtask subtask) {
        subtasks.put(subtask.getId(), subtask);
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("Эпик ID: ").append(getId()).append("\n")
                .append("Наименование эпика: ").append(getTitle()).append("\n")
                .append("Описание эпика: ").append(getDescription()).append("\n")
                .append("Текущий статус: ").append(getStatus()).append("\n")
                .append("Подзадачи эпика: \n");

        for (Subtask subtask : subtasks.values()) {
            sb.append("  - ").append(subtask.toString()).append("\n");
        }

        return sb.toString();
    }
}