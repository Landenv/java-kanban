package taskmanager.utiltask;

import java.util.ArrayList;

public class Epic extends Task {
    private final ArrayList<Integer> subtaskIds;

    public Epic(String title, String description) {
        super(title, description);
        this.subtaskIds = new ArrayList<>();
    }

    public void addSubtask(int subtaskId) {
        subtaskIds.add(subtaskId);
    }

    public ArrayList<Integer> getSubtaskIds() {
        return subtaskIds;
    }


    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("Эпик ID: ").append(getId()).append("\n")
                .append("Наименование эпика: ").append(getTitle()).append("\n")
                .append("Описание эпика: ").append(getDescription()).append("\n")
                .append("Текущий статус: ").append(getStatus()).append("\n")
                .append("Подзадачи эпика: \n");

        for (Integer subtaskId : subtaskIds) {
            sb.append("  - Подзадача ID: ").append(subtaskId).append("\n");
        }

        return sb.toString();
    }
}