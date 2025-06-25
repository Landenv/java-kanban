package taskmanager.http.handler;

import taskmanager.exception.NotFoundException;
import taskmanager.manager.TaskManager;
import taskmanager.utiltask.Epic;

import java.util.List;

public class EpicHandler extends AbstractTaskHandler<Epic> {
    public EpicHandler(TaskManager taskManager) {
        super(taskManager);
    }

    @Override
    protected String getPathPattern() {
        return "/epics/\\d+";
    }

    @Override
    protected Epic getById(int id) throws NotFoundException {
        return taskManager.getEpicById(id);
    }

    @Override
    protected void create(Epic item) {
        taskManager.createEpic(item);
    }

    @Override
    protected void update(Epic item) {
        taskManager.updateEpic(item);
    }

    @Override
    protected void delete(int id) {
        taskManager.deleteEpic(id);
    }

    @Override
    protected Class<Epic> getType() {
        return Epic.class;
    }

    @Override
    protected List<Epic> getAllItems() {
        return taskManager.getAllEpics();
    }
}
