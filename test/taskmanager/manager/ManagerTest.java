package taskmanager.manager;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class ManagerTest {

    // Проверка, что метод getDefault инициализирует InMemoryTaskManager
    @Test
    void getDefaultShouldInitializeInMemoryTaskManager() {
        assertInstanceOf(InMemoryTaskManager.class, Manager.getDefault());
    }

    // Проверка, что метод getDefaultHistory инициализирует InMemoryHistoryManager
    @Test
    void getDefaultHistoryShouldInitializeInMemoryHistoryManager() {
        assertInstanceOf(InMemoryHistoryManager.class, Manager.getDefaultHistory());
    }

    // Проверка, что утилитарный класс всегда возвращает проинициализированные и готовые к работе экземпляры менеджеров
    @Test
    void utilityClassReturnsInitializedManagers() {
        TaskManager taskManager = Manager.getDefault();
        assertNotNull(taskManager);
        HistoryManager historyManager = Manager.getDefaultHistory();
        assertNotNull(historyManager);
    }
}
