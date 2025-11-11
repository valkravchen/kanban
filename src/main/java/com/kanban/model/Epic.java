package com.kanban.model;

import java.util.*;

public class Epic extends BaseTask {
    private final Set<Integer> subtasksIds = new HashSet<>();

    public Epic(String name, String description, TaskStatus status) {
        super(name, description, status);
    }

    @Override
    public void setStatus(TaskStatus status) {
        throw new UnsupportedOperationException("Статус эпика рассчитывается автоматически по подзадачам");
    }

    public Set<Integer> getSubtasksIds() {
        return new HashSet<>(subtasksIds);
    }

    public void addSubtaskId(int subtaskId) {
        subtasksIds.add(subtaskId);
    }

    public void removeSubtaskId(int subtaskId) {
        subtasksIds.remove(subtaskId);
    }

    public void applyCalculatedStatus(TaskStatus status) {
        updateStatus(status);
    }
}
