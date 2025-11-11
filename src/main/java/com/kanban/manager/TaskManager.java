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
        if (task instanceof Subtask subtask) {
            Epic epic = (Epic) getById(subtask.getEpicId());
            epic.addSubtask(subtask.getId());
            updateEpicStatus(subtask.getEpicId());
        }
    }

    public BaseTask getById(int id) {
        return tasks.get(id);
    }

    public void deleteTaskById(int id) {
        BaseTask task = getById(id);
        if (task instanceof Subtask subtask) {
            Epic epic = (Epic) getById(subtask.getEpicId());
            epic.removeSubtask(subtask.getId());
            updateEpicStatus(subtask.getEpicId());
        }
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

    public List<Subtask> getSubtasksByEpicId(int epicId) {
        return getAllSubtasks().stream()
                .filter(subtask -> subtask.getEpicId() == epicId)
                .toList();
    }

    public void updateEpicStatus(int epicId) {
        List<TaskStatus> statuses = getAllSubtasks().stream()
                .filter(subtask -> subtask.getEpicId() == epicId)
                .map(BaseTask::getStatus)
                .toList();
        TaskStatus newStatus;
        if (statuses.isEmpty() || statuses.stream()
                .allMatch(status -> status == TaskStatus.NEW)) {
            newStatus = TaskStatus.NEW;
        } else if (statuses.stream()
                .allMatch(status -> status == TaskStatus.DONE)) {
            newStatus = TaskStatus.DONE;
        } else {
            newStatus = TaskStatus.IN_PROGRESS;
        }
        Epic epic = (Epic) getById(epicId);
        epic.updateStatus(newStatus);
    }

    public void update(BaseTask task) {
        tasks.put(task.getId(), task);  // заменяем старую задачу новой

        if (task instanceof Subtask subtask) {
            updateEpicStatus(subtask.getEpicId());  // пересчитываем статус Epic
        }
    }
}
