package taskmanager.utiltask;

import java.time.Duration;
import java.time.LocalDateTime;

public class Subtask extends Task {
    private final int epicId;

    public Subtask(String title, String description, int epicId) {
        super(title, description);
        this.epicId = epicId;
    }

    // Новый унифицированный конструктор
    public Subtask(String title, String description, int epicId, Duration duration, LocalDateTime startTime) {
        super(title, description, duration, startTime);
        this.epicId = epicId;
    }

    // Конструктор для файла загрузки
    public Subtask(int id, String title, String description, Status status, Integer epicId, Duration duration, LocalDateTime startTime) {
        super(id, title, description, status, duration, startTime);
        this.epicId = epicId;
    }

    public int getEpicID() {
        return epicId;
    }

    @Override
    public TaskType getType() {
        return TaskType.SUBTASK;
    }

    @Override
    public String toString() {
        return "Подзадача ID: " + getId() +
                ", Наименование подзадачи: " +
                getTitle() +
                ", Описание подзадачи: " +
                getDescription() +
                ", Текущий статус: " +
                getStatus();
    }
}