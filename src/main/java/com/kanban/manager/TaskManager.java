package com.kanban.manager;

import com.kanban.model.*;

import java.util.*;

public class TaskManager {
    private Map<Integer, BaseTask> tasks;

    public TaskManager() {
        this.tasks = new HashMap<>();

    }

    public void addTask(Task task) {
        tasks.put(task.getId(), task);
    }

    public BaseTask getById(int id) {
        return tasks.get(id);
    }

    public void deleteTaskById(int id) {
        tasks.remove(id);
    }

    public void deleteAll() {
        tasks.clear();
    }

    public List<Task> getAllTasks() {
        return tasks.values().stream()
                .filter(task -> task instanceof Task)
                .map(task -> (Task) task)
                .toList();
    }

    public List<Subtask> getAllSubtasks() {
        return tasks.values().stream()
                .filter(subtask -> subtask instanceof Subtask)
                .map(subtask -> (Subtask) subtask)
                .toList();
    }

    public List<Epic> getAllEpics() {
        return tasks.values().stream()
                .filter(epic -> epic instanceof Epic)
                .map(epic -> (Epic) epic)
                .toList();
    }
}
