package taskmanager.utiltask;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class EpicTest {

    // Проверка, что наследники класса Task равны друг другу, если равен их id
    @Test
    void epicSubclassesEqualById() {
        Epic epic1 = new Epic("Эпик 1", "Описание 1");
        Epic epic2 = new Epic("Эпик 2", "Описание 2");
        epic1.setId(1);
        epic2.setId(1);
        assertEquals(epic1, epic2);
    }

}