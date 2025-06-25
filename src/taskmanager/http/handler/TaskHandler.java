package taskmanager.http.handler;

import taskmanager.exception.NotFoundException;
import taskmanager.manager.TaskManager;
import taskmanager.utiltask.Task;

import java.util.List;

public class TaskHandler extends AbstractTaskHandler<Task> {
    public TaskHandler(TaskManager taskManager) {
        super(taskManager);
    }

    @Override
    protected String getPathPattern() {
        return "/tasks/\\d+";
    }

    @Override
    protected Task getById(int id) throws NotFoundException {
        return taskManager.getTaskById(id);
    }

    @Override
    protected void create(Task item) {
        taskManager.createTask(item);
    }

    @Override
    protected void update(Task item) {
        taskManager.updateTask(item);
    }

    @Override
    protected void delete(int id) {
        taskManager.deleteTask(id);
    }

    @Override
    protected Class<Task> getType() {
        return Task.class;
    }

    @Override
    protected List<Task> getAllItems() {
        return taskManager.getAllTasks();
    }
}
