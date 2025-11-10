package com.kanban.model;

import java.util.*;

public class Epic extends BaseTask {
    private Set<Integer> subtasksIds;

    public Epic(String name, String description, TaskStatus status) {
        super(name, description, status);
        this.subtasksIds = new HashSet<>();
    }

    public void addSubtask(int subtaskId) {
        subtasksIds.add(subtaskId);
    }

    public void removeSubtask(int subtaskId) {
        subtasksIds.remove(subtaskId);
    }

    public Set<Integer> getSubtasksIds() {
        return new HashSet<>(subtasksIds);
    }

    @Override
    public void setStatus(TaskStatus status) {
        throw new UnsupportedOperationException("Статус эпика рассчитывается автоматически");
    }
}
