package taskmanager.utiltask;

public class Subtask extends Task {
    private final int epicId;

    public Subtask(String title, String description, int epicId) {
        super(title, description);
        this.epicId = epicId;
    }

    public int getEpicID()
    {
        return epicId;
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