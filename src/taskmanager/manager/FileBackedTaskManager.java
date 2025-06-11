package taskmanager.manager;

import taskmanager.utiltask.*;

import java.io.*;
import java.nio.file.Path;
import java.time.Duration;
import java.time.LocalDateTime;

public class FileBackedTaskManager extends InMemoryTaskManager {
    private final Path taskListFile;
    private static final int CSV_FIELD_COUNT = 9;

    public FileBackedTaskManager(Path taskListFile) {
        super();
        this.taskListFile = taskListFile;
    }

    @Override
    public void createTask(Task task) {
        super.createTask(task);
        save();
    }

    @Override
    public void createSubtask(Subtask subtask) {
        super.createSubtask(subtask);
        save();
    }

    @Override
    public void createEpic(Epic epic) {
        super.createEpic(epic);
        save();
    }

    @Override
    public void updateTask(Task task) {
        super.updateTask(task);
        save();
    }

    @Override
    public void updateSubtask(Subtask subtask) {
        super.updateSubtask(subtask);
        save();
    }

    @Override
    public void updateEpic(Epic epic) {
        super.updateEpic(epic);
        save();
    }

    @Override
    public void deleteTask(int id) {
        super.deleteTask(id);
        save();
    }

    @Override
    public void deleteSubtasks(int id) {
        super.deleteSubtasks(id);
        save();
    }

    @Override
    public void deleteEpic(int id) {
        super.deleteEpic(id);
        save();
    }

    @Override
    public void deleteAllTasks() {
        super.deleteAllTasks();
        save();
    }

    @Override
    public void deleteAllSubtasks() {
        super.deleteAllSubtasks();
        save();
    }

    @Override
    public void deleteAllEpics() {
        super.deleteAllEpics();
        save();
    }

    private String formatString(Task task) {
        String epicValue = getEpicValue(task);
        String durationValue = (task.getDuration() == null) ? "" : String.valueOf(task.getDuration().toMinutes());
        String startTimeValue = (task.getStartTime() == null) ? "" : task.getStartTime().toString();
        String endTimeValue = (task.getEndTime() == null) ? "" : task.getEndTime().toString();
        return String.format("%d,%s,%s,%s,%s,%s,%s,%s,%s",
                task.getId(),
                task.getType(),
                task.getTitle(),
                task.getStatus(),
                task.getDescription(),
                epicValue,
                durationValue,
                startTimeValue,
                endTimeValue
        );
    }

    private String getEpicValue(Task task) {
        if (task instanceof Subtask) {
            return String.valueOf(((Subtask) task).getEpicID());
        }
        return "null";
    }

    protected void save() {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(taskListFile.toFile()))) {
            writer.write("id,type,title,status,description,epic,duration,startTime,endTime");
            writer.newLine();

            // Используем отсортированный prioritizedTasks — они содержат только TASK и SUBTASK c startTime != null
            for (Task task : prioritizedTasks) {
                writer.write(formatString(task));
                writer.newLine();
            }

            // Остальные TASK/SUBTASK без startTime
            tasks.values().stream()
                    .filter(task -> task.getStartTime() == null)
                    .forEach(task -> {
                        try {
                            writer.write(formatString(task));
                            writer.newLine();
                        } catch (IOException e) {
                            throw new UncheckedIOException(e);
                        }
                    });
            subtasks.values().stream()
                    .filter(subtask -> subtask.getStartTime() == null)
                    .forEach(subtask -> {
                        try {
                            writer.write(formatString(subtask));
                            writer.newLine();
                        } catch (IOException e) {
                            throw new UncheckedIOException(e);
                        }
                    });
            // Эпики
            for (Epic epic : getAllEpics()) {
                writer.write(formatString(epic));
                writer.newLine();
            }
        } catch (IOException e) {
            throw new ManagerSaveException("Ошибка при сохранении в файл", e);
        }
    }

    public static FileBackedTaskManager loadFromFile(Path taskListFile) {
        FileBackedTaskManager manager = new FileBackedTaskManager(taskListFile);

        try (BufferedReader reader = new BufferedReader(new FileReader(taskListFile.toFile()))) {
            String header = reader.readLine();
            if (header == null || header.isBlank()) {
                return manager;
            }

            if (!header.trim().equals("id,type,title,status,description,epic,duration,startTime,endTime")) {
                throw new ManagerSaveException("Неверный формат заголовка файла.");
            }

            String line;
            while ((line = reader.readLine()) != null) {
                if (line.isBlank()) continue;

                String[] parts = line.split(",", -1);

                if (parts.length != CSV_FIELD_COUNT) {
                    throw new ManagerSaveException("Неверный формат строки: " + line);
                }

                int id = Integer.parseInt(parts[0]);
                String typeString = parts[1];
                String title = parts[2];
                Status status = Status.valueOf(parts[3]);
                String description = parts[4];
                Integer epicId = ("null".equals(parts[5]) || parts[5].isBlank()) ? null : Integer.parseInt(parts[5]);
                Duration duration = parts[6].isBlank() ? null : Duration.ofMinutes(Long.parseLong(parts[6]));
                LocalDateTime startTime = parts[7].isBlank() ? null : LocalDateTime.parse(parts[7]);

                Task task;
                TaskType type = TaskType.valueOf(typeString);

                switch (type) {
                    case TASK:
                        task = new Task(id, title, description, status, duration, startTime);
                        manager.tasks.put(id, task);
                        if (startTime != null) manager.prioritizedTasks.add(task);
                        break;
                    case EPIC:
                        task = new Epic(id, title, description);
                        manager.epics.put(id, (Epic) task);
                        break;
                    case SUBTASK:
                        if (epicId == null) throw new ManagerSaveException("Для подзадачи требуется валидный epicId");
                        Subtask subtask = new Subtask(id, title, description, status, epicId, duration, startTime);
                        Epic epic = manager.epics.get(subtask.getEpicID());
                        if (epic == null)
                            throw new ManagerSaveException("Эпик с ID " + subtask.getEpicID() + " не найден.");
                        manager.subtasks.put(subtask.getId(), subtask);
                        if (startTime != null) manager.prioritizedTasks.add(subtask);
                        epic.addSubtask(subtask.getId());
                        break;
                    default:
                        throw new ManagerSaveException("Неизвестный тип задачи: " + type);
                }
                manager.updateNextId(id);
            }

            for (Epic epic : manager.epics.values()) {
                manager.updateEpicStatus(epic.getId());
            }
        } catch (IOException | IllegalArgumentException e) {
            throw new ManagerSaveException("Ошибка при загрузке из файла: " + e.getMessage(), e);
        }

        return manager;
    }
}