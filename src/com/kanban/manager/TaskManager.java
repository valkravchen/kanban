package com.kanban.manager;

import com.kanban.model.*;

import java.util.*;

public class TaskManager {
    private Map<Integer, Task> taskMap;
    private Map<Integer, Subtask> subtaskMap;
    private Map<Integer, Epic> epicMap;

    public TaskManager() {
        this.taskMap = new HashMap<>();
        this.subtaskMap = new HashMap<>();
        this.epicMap = new HashMap<>();
    }

    public void createTask(Task task) {
        taskMap.put(task.getId(), task);
    }

    public Task getTaskById(int id) {
        return taskMap.get(id);
    }

    public List<Task> getAllTasks() {
        return new ArrayList<>(taskMap.values());
    }

    public void deleteAllTasks() {
        taskMap.clear();
    }

    public void updateTask(int id, Task task) {
        taskMap.put(task.getId(), task);
    }
}
