package com.kanban.manager;

import com.kanban.model.*;

import java.util.*;

public class TaskManager {
    private Map<Integer, BaseTask> tasks;

    public TaskManager() {
        this.tasks = new HashMap<>();

    }

    public void add(BaseTask task) {
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
        return getTasksByType(Task.class);
    }

    public List<Subtask> getAllSubtasks() {
        return getTasksByType(Subtask.class);
    }

    public List<Epic> getAllEpics() {
        return getTasksByType(Epic.class);
    }

    private <T extends BaseTask> List<T> getTasksByType(Class<T> type) {
        return tasks.values().stream()
                .filter(type::isInstance)
                .map(type::cast)
                .toList();
    }
}
