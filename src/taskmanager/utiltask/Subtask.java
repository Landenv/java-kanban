package taskmanager.utiltask;

public class Subtask extends Task {
    private final int epicId;

    public Subtask(String title, String description, int epicId) {
        super(title, description);
        this.epicId = epicId;
    }

    // Конструктор для файла
    public Subtask(int id, String title, String description, Status status, Integer epicId) {
        super(id, title, description, status);
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