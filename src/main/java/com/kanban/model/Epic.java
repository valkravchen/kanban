package com.kanban.model;

import java.util.*;

public class Epic extends BaseTask {
    private final Set<Integer> subtaskIds = new HashSet<>();

    public Epic(String name, String description) {
        super(name, description, TaskStatus.NEW);
    }

    @Override
    public void setStatus(TaskStatus status) {
        throw new UnsupportedOperationException("Статус эпика рассчитывается автоматически по подзадачам");
    }

    public Set<Integer> getSubtaskIds() {
        return new HashSet<>(subtaskIds);
    }

    public void addSubtaskId(int subtaskId) {
        subtaskIds.add(subtaskId);
    }

    public void removeSubtaskId(int subtaskId) {
        subtaskIds.remove(subtaskId);
    }

    public void clearSubtaskIds() {
        subtaskIds.clear();
    }

    public void applyCalculatedStatus(TaskStatus status) {
        updateStatus(status);
    }
}//
