package taskmanager.manager;

import taskmanager.utiltask.*;

import java.io.*;
import java.nio.file.Path;

public class FileBackedTaskManager extends InMemoryTaskManager {
    private final Path taskListFile;

    public FileBackedTaskManager(Path taskListFile) {
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
        return String.format("%d,%s,%s,%s,%s,%s",
                task.getId(),
                getType(task),
                task.getTitle(),
                task.getStatus(),
                task.getDescription(),
                epicValue);
    }

    private String getType(Task task) {
        if (task instanceof Epic) {
            return "EPIC";
        } else if (task instanceof Subtask) {
            return "SUBTASK";
        }
        return "TASK";
    }

    private String getEpicValue(Task task) {
        if (task instanceof Subtask) {
            return String.valueOf(((Subtask) task).getEpicID());
        }
        return "null";
    }

    protected void save() {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(taskListFile.toFile()))) {
            writer.write("id,type,title,status,description,epic");
            writer.newLine();

            for (Task task : getAllTasks()) {
                writer.write(formatString(task));
                writer.newLine();
            }
            for (Epic epic : getAllEpics()) {
                writer.write(formatString(epic));
                writer.newLine();
            }
            for (Subtask subtask : getAllSubtasks()) {
                writer.write(formatString(subtask));
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

            System.out.println("Полученный заголовок: '" + header + "'");

            if (header == null || header.isBlank()) {
                return manager;
            }

            if (!header.trim().equals("id,type,title,status,description,epic")) {
                throw new ManagerSaveException("Неверный формат заголовка файла.");
            }

            String line;
            while ((line = reader.readLine()) != null) {
                if (line.isBlank()) continue;

                String[] parts = line.split(",", -1);

                if (parts.length != 6) {
                    throw new ManagerSaveException("Неверный формат строки: " + line);
                }

                int id = Integer.parseInt(parts[0]);
                String type = parts[1];
                String title = parts[2];
                Status status = Status.valueOf(parts[3]);
                String description = parts[4];

                if ("TASK".equals(type) || "EPIC".equals(type)) {
                    if (!parts[5].isBlank()) {
                        throw new ManagerSaveException("Поле epic не должно содержать значение для типа " + type);
                    }
                } else {
                    if (parts[5].isBlank()) {
                        throw new ManagerSaveException("Поле epic не должно быть пустым для типа " + type);
                    }
                }

                Integer epicId = parts[5].isBlank() ? null : Integer.parseInt(parts[5]);

                Task task;
                switch (type) {
                    case "TASK":
                        task = new Task(id, title, description, status);
                        break;
                    case "EPIC":
                        task = new Epic(id, title, description);
                        break;
                    case "SUBTASK":
                        if (epicId == null) {
                            throw new ManagerSaveException("Для подзадачи требуется валидный epicId");
                        }
                        task = new Subtask(id, title, description, status, epicId);
                        break;
                    default:
                        throw new ManagerSaveException("Неизвестный тип задачи: " + type);
                }


                switch (type) {
                    case "TASK":
                        manager.tasks.put(id, task);
                        break;
                    case "EPIC":
                        manager.epics.put(id, (Epic) task);
                        break;
                    case "SUBTASK":
                        Subtask subtask = (Subtask) task;
                        Epic epic = manager.epics.get(subtask.getEpicID());
                        if (epic == null) {
                            throw new ManagerSaveException("Эпик с ID " + subtask.getEpicID() + " не найден.");
                        }
                        manager.subtasks.put(subtask.getId(), subtask);
                        epic.addSubtask(subtask.getId());
                        break;
                }
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