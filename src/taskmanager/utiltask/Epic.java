package taskmanager.utiltask;

import java.util.ArrayList;
import java.util.List;
import java.time.Duration;
import java.time.LocalDateTime;

public class Epic extends Task {
    private List<Integer> subtaskIds = new ArrayList<>();
    private Duration duration = Duration.ZERO;     // храним актуальное значение
    private LocalDateTime startTime = null;
    private LocalDateTime endTime = null;

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

    @Override
    public Duration getDuration() {
        return duration;
    }

    @Override
    public LocalDateTime getStartTime() {
        return startTime;
    }

    @Override
    public LocalDateTime getEndTime() {
        return endTime;
    }

    // Метод, вызываемый для пересчёта расчётных полей на основе данных подзадач
    public void recalculate(List<Subtask> subtaskList) {
        if (subtaskList.isEmpty()) {
            this.duration = Duration.ZERO;
            this.startTime = null;
            this.endTime = null;
            return;
        }
        // Вычисляем duration, startTime и endTime по всем подзадачам
        Duration total = Duration.ZERO;
        LocalDateTime minStart = null;
        LocalDateTime maxEnd = null;

        for (Subtask sub : subtaskList) {
            if (sub.getStartTime() != null && sub.getDuration() != null) {
                total = total.plus(sub.getDuration());
                if (minStart == null || sub.getStartTime().isBefore(minStart)) {
                    minStart = sub.getStartTime();
                }
                LocalDateTime subEnd = sub.getEndTime();
                if (subEnd != null && (maxEnd == null || subEnd.isAfter(maxEnd))) {
                    maxEnd = subEnd;
                }
            }
        }
        this.duration = total;
        this.startTime = minStart;
        this.endTime = maxEnd;
    }

    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("Эпик ID: ").append(getId()).append("\n").append("Наименование эпика: ").append(getTitle()).append("\n").append("Начало: ").append(getStartTime()).append("\n").append("Длительность (мин): ").append(getDuration().toMinutes()).append("\n").append("Завершение: ").append(getEndTime()).append("\n").append("Описание эпика: ").append(getDescription()).append("\n").append("Текущий статус: ").append(getStatus()).append("\n").append("Подзадачи эпика: \n");

        for (Integer subtaskId : subtaskIds) {
            sb.append("  - Подзадача ID: ").append(subtaskId).append("\n");
        }

        return sb.toString();
    }
}