package taskmanager.manager;

import java.io.IOException;

public class ManagerSaveException extends RuntimeException {
    public ManagerSaveException(final String message) {
        super(message);
    }

    public ManagerSaveException(String message, IOException e) {
        super(message, e);
    }

    public ManagerSaveException(String message, Throwable cause) {
        super(message, cause);
    }
}