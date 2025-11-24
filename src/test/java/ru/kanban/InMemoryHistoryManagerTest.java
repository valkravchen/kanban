package ru.kanban;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.*;

class InMemoryHistoryManagerTest {

    private InMemoryHistoryManager historyManager;

    @BeforeEach
    void setUp() {
        historyManager = new InMemoryHistoryManager();
    }

    @Test
    void getHistoryShouldReturnEmptyListWhenNoTasksAdded() {
        assertThat(historyManager.getHistory()).isEmpty();
    }

    @Test
    void addToHistoryShouldAddTask() {
        Task task = new Task("Задача", "Описание", TaskStatus.NEW);
        historyManager.addToHistory(task);

        assertThat(historyManager.getHistory()).hasSize(1);
        assertThat(historyManager.getHistory().get(0)).isEqualTo(task);
    }

    @Test
    void addToHistoryShouldAddDifferentTaskTypes() {
        Task task = new Task("Задача", "Описание", TaskStatus.NEW);
        Epic epic = new Epic("Эпик", "Описание");
        Subtask subtask = new Subtask("Подзадача", "Описание", TaskStatus.NEW, 1);

        historyManager.addToHistory(task);
        historyManager.addToHistory(epic);
        historyManager.addToHistory(subtask);

        assertThat(historyManager.getHistory()).hasSize(3);
    }

    @Test
    void addToHistoryShouldPreserveOrder() {
        Task task1 = new Task("Задача 1", "Описание", TaskStatus.NEW);
        Task task2 = new Task("Задача 2", "Описание", TaskStatus.NEW);
        Task task3 = new Task("Задача 3", "Описание", TaskStatus.NEW);
        task1.setId(1);
        task2.setId(2);
        task3.setId(3);
        historyManager.addToHistory(task1);
        historyManager.addToHistory(task2);
        historyManager.addToHistory(task3);
        List<BaseTask> history = historyManager.getHistory();
        assertThat(history.get(0)).isEqualTo(task1);
        assertThat(history.get(1)).isEqualTo(task2);
        assertThat(history.get(2)).isEqualTo(task3);
    }

    @Test
    void addToHistoryShouldNotExceedMaxSize() {
        for (int i = 0; i < 15; i++) {
            Task task = new Task("Задача " + i, "Описание", TaskStatus.NEW);
            historyManager.addToHistory(task);
        }

        assertThat(historyManager.getHistory()).hasSize(10);
    }

    @Test
    void addToHistoryShouldRemoveOldestWhenMaxSizeReached() {
        Task firstTask = new Task("Первая задача", "Описание", TaskStatus.NEW);
        firstTask.setId(1);
        historyManager.addToHistory(firstTask);
        for (int i = 2; i <= 10; i++) {
            Task task = new Task("Задача " + i, "Описание", TaskStatus.NEW);
            task.setId(i);
            historyManager.addToHistory(task);
        }
        assertThat(historyManager.getHistory()).contains(firstTask);
        Task eleventhTask = new Task("Одиннадцатая задача", "Описание", TaskStatus.NEW);
        eleventhTask.setId(11);
        historyManager.addToHistory(eleventhTask);
        assertThat(historyManager.getHistory()).doesNotContain(firstTask);
        assertThat(historyManager.getHistory()).contains(eleventhTask);
        assertThat(historyManager.getHistory()).hasSize(10);
    }

    @Test
    void getHistoryShouldReturnCopyOfList() {
        Task task = new Task("Задача", "Описание", TaskStatus.NEW);
        historyManager.addToHistory(task);
        List<BaseTask> history1 = historyManager.getHistory();
        List<BaseTask> history2 = historyManager.getHistory();
        assertThat(history1).isNotSameAs(history2);
    }
}