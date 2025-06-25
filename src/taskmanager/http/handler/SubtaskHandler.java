package taskmanager.http.handler;

import taskmanager.exception.NotFoundException;
import taskmanager.manager.TaskManager;
import taskmanager.utiltask.Subtask;

import java.util.List;

public class SubtaskHandler extends AbstractTaskHandler<Subtask> {
    public SubtaskHandler(TaskManager taskManager) {
        super(taskManager);
    }

    @Override
    protected String getPathPattern() {
        return "/subtasks/\\d+";
    }

    @Override
    protected Subtask getById(int id) throws NotFoundException {
        return taskManager.getSubtaskById(id);
    }

    @Override
    protected void create(Subtask item) {
        taskManager.createSubtask(item);
    }

    @Override
    protected void update(Subtask item) {
        taskManager.updateSubtask(item);
    }

    @Override
    protected void delete(int id) {
        taskManager.deleteSubtasks(id);
    }

    @Override
    protected Class<Subtask> getType() {
        return Subtask.class;
    }

    @Override
    protected List<Subtask> getAllItems() {
        return taskManager.getAllSubtasks();
    }
}
