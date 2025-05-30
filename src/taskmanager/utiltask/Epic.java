package taskmanager.utiltask;

import java.util.ArrayList;
import java.util.List;

public class Epic extends Task {
    private List<Integer> subtaskIds = new ArrayList<>();

    public Epic(String title, String description) {
        super(title, description);
        this.subtaskIds = new ArrayList<>();
    }

    // Конструктор для обновления эпика
    public Epic(int id, String title, String description, List<Integer> subtaskIds) {
        super(title, description);
        this.subtaskIds = new ArrayList<>(subtaskIds);
        this.setId(id);
    }

    // Конструктор для файла
    public Epic(int id, String title, String description) {
        super(id, title, description, Status.NEW);
    }

    public void addSubtask(int subtaskId) {

        subtaskIds.add(subtaskId);
    }

    public List<Integer> getSubtaskIds() {

        return subtaskIds;
    }

    @Override
    public TaskType getType() {
        return TaskType.EPIC;
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