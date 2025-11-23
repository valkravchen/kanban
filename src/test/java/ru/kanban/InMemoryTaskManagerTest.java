package ru.kanban;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertThrows;

class InMemoryTaskManagerTest {

    private InMemoryTaskManager manager;

    @BeforeEach
    void setUp() {
        HistoryManager historyManager = new InMemoryHistoryManager();
        this.manager = new InMemoryTaskManager(historyManager);
    }

    @Test
    void addTaskShouldAssignId() {
        Task task = new Task("Первая задача",
                "Сделать тест для метода addTask: проверка ID = 1", TaskStatus.NEW);
        manager.addTask(task);
        int expected = 1;
        int result = task.getId();
        assertThat(result).isEqualTo(expected);
    }

    @Test
    void addTaskShouldStoreInList() {
        Task task = new Task("Вторая задача",
                "Сделать тест для метода addTask: проверить, что size = 1", TaskStatus.NEW);
        manager.addTask(task);
        int expected = 1;
        int result = manager.getAllTasks().size();
        assertThat(result).isEqualTo(expected);
    }

    @Test
    void addEpicShouldAssignId() {
        Epic epic = new Epic("Первый эпик",
                "Сделать тест для метода addEpic: проверка ID = 1");
        manager.addEpic(epic);
        int expected = 1;
        int result = epic.getId();
        assertThat(result).isEqualTo(expected);
    }

    @Test
    void addEpicShouldStoreInList() {
        Epic epic = new Epic("Второй эпик",
                "Сделать тест для метода addEpic: проверить, что size = 1");
        manager.addEpic(epic);
        int expected = 1;
        int result = manager.getAllEpics().size();
        assertThat(result).isEqualTo(expected);
    }

    @Test
    void addEpicShouldHaveStatusNew() {
        Epic epic = new Epic("Третий эпик",
                "Сделать тест для метода addEpic: проверить, что статус = NEW");
        manager.addEpic(epic);

        TaskStatus expected = TaskStatus.NEW;
        TaskStatus result = epic.getStatus();
        assertThat(result).isEqualTo(expected);
    }

    @Test
    void addSubtaskShouldAssignId() {
        Epic epic = new Epic("Четвёртый эпик", "Проверить подзадачу по ID");
        manager.addEpic(epic);
        Subtask subtask = new Subtask("Первая подзадача",
                "Сделать тест для метода addSubtask: проверка ID = 2", TaskStatus.NEW, epic.getId());
        manager.addSubtask(subtask);
        int expected = 2;
        int result = subtask.getId();
        assertThat(result).isEqualTo(expected);
    }

    @Test
    void addSubtaskShouldStoreInList() {
        Epic epic = new Epic("Пятый эпик", "Проверить количество подзадач");
        manager.addEpic(epic);
        Subtask subtask = new Subtask("Вторая подзадача",
                "Сделать тест для метода addSubtask: проверить, что size = 1",
                TaskStatus.NEW, epic.getId());
        manager.addSubtask(subtask);
        int expected = 1;
        int result = manager.getAllSubtasks().size();
        assertThat(result).isEqualTo(expected);
    }

    @Test
    void addSubtaskShouldThrowExceptionIfEpicNotFound() {
        Subtask subtask = new Subtask("Третья подзадача",
                "Сделать тест для метода addSubtask: проверка исключения",
                TaskStatus.NEW, 999);

        assertThrows(IllegalArgumentException.class, () -> {
            manager.addSubtask(subtask);
        });
    }

    @Test
    void getAllTasksShouldReturnEmptyListWhenNoTasks() {
        int expected = 0;
        int result = manager.getAllTasks().size();
        assertThat(result).isEqualTo(expected);
    }

    @Test
    void getAllTasksShouldReturnAllTasks() {
        Task task1 = new Task("Задача 1", "Описание 1", TaskStatus.NEW);
        Task task2 = new Task("Задача 2", "Описание 2", TaskStatus.NEW);
        manager.addTask(task1);
        manager.addTask(task2);

        int expected = 2;
        int result = manager.getAllTasks().size();
        assertThat(result).isEqualTo(expected);
    }

    @Test
    void getAllEpicsShouldReturnEmptyListWhenNoEpics() {
        int expected = 0;
        int result = manager.getAllEpics().size();
        assertThat(result).isEqualTo(expected);
    }

    @Test
    void getAllEpicsShouldReturnAllEpics() {
        Epic epic1 = new Epic("Эпик 1", "Описание 1");
        Epic epic2 = new Epic("Эпик 2", "Описание 2");
        manager.addEpic(epic1);
        manager.addEpic(epic2);

        int expected = 2;
        int result = manager.getAllEpics().size();
        assertThat(result).isEqualTo(expected);
    }

    @Test
    void getAllSubtasksShouldReturnEmptyListWhenNoSubtasks() {
        int expected = 0;
        int result = manager.getAllSubtasks().size();
        assertThat(result).isEqualTo(expected);
    }

    @Test
    void getAllSubtasksShouldReturnAllSubtasks() {
        Epic epic = new Epic("Эпик", "Описание");
        manager.addEpic(epic);

        Subtask subtask1 = new Subtask("Подзадача 1", "Описание 1", TaskStatus.NEW, epic.getId());
        Subtask subtask2 = new Subtask("Подзадача 2", "Описание 2", TaskStatus.NEW, epic.getId());
        manager.addSubtask(subtask1);
        manager.addSubtask(subtask2);

        int expected = 2;
        int result = manager.getAllSubtasks().size();
        assertThat(result).isEqualTo(expected);
    }
}