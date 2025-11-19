package ru.kanban;

import java.util.*;

public interface TaskManager {
    void addTask(Task task);

    void addEpic(Epic epic);

    void addSubtask(Subtask subtask);

    List<Task> getAllTasks();

    List<Epic> getAllEpics();

    List<Subtask> getAllSubtasks();

    Optional<Task> getTask(int id);

    Optional<Epic> getEpic(int id);

    Optional<Subtask> getSubtask(int id);

    void updateTask(Task newTask);

    void updateEpic(Epic newEpic);

    void updateSubtask(Subtask newSubtask);

    void deleteTask(int id);

    void deleteSubtask(int id);

    void deleteEpic(int id);

    void deleteAllTasks();

    void deleteAllSubtasks();

    void deleteAllEpics();

    List<Subtask> getSubtasksByEpicId(int epicId);

    List<BaseTask> getHistory();
}
