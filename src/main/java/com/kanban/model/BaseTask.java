package com.kanban.model;

public class BaseTask {
    private String name;
    private String description;
    private final int id;
    private TaskStatus status;
    private static int nextId = 1;

    public BaseTask(String name, String description, TaskStatus status) {
        this.name = name;
        this.description = description;
        this.status = status;
        this.id = nextId++;
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public TaskStatus getStatus() {
        return status;
    }

    public void setStatus(TaskStatus status) {
        this.status = status;
    }

    protected void updateStatus(TaskStatus status) {
        this.status = status;
    }
}//
