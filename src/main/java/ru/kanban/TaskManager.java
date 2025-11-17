package ru.kanban;

import java.util.*;

public class TaskManager {
    private Map<Integer, Task> tasks = new HashMap<>();
    private Map<Integer, Epic> epics = new HashMap<>();
    private Map<Integer, Subtask> subtasks = new HashMap<>();
    private int nextId = 1;

    public void addTask(Task task) {
        assignId(task);
        tasks.put(task.getId(), task);
    }

    public void addEpic(Epic epic) {
        assignId(epic);
        epics.put(epic.getId(), epic);
    }

    public void addSubtask(Subtask subtask) {
        Epic epic = epics.get(subtask.getEpicId());
        if (epic == null) {
            throw new IllegalArgumentException("Эпик с id=" + subtask.getEpicId() + " не найден");
        }
        assignId(subtask);
        subtasks.put(subtask.getId(), subtask);
        epic.addSubtaskId(subtask.getId());
        updateEpicStatus(epic.getId());
    }

    public List<Task> getAllTasks() {
        return tasks.values()
                .stream()
                .toList();
    }

    public List<Epic> getAllEpics() {
        return epics.values()
                .stream()
                .toList();
    }

    public List<Subtask> getAllSubtasks() {
        return subtasks.values()
                .stream()
                .toList();
    }

    public Optional<Task> getTaskById(int id) {
        return Optional.ofNullable(tasks.get(id));
    }

    public Optional<Epic> getEpicById(int id) {
        return Optional.ofNullable(epics.get(id));
    }

    public Optional<Subtask> getSubtaskById(int id) {
        return Optional.ofNullable(subtasks.get(id));

    }

    public void updateTask(Task newTask) {
        if (newTask == null) {
            return;
        }
        if (!tasks.containsKey(newTask.getId())) {
            return;
        }
        tasks.put(newTask.getId(), newTask);
    }

    public void updateEpic(Epic newEpic) {
        if (newEpic == null) {
            return;
        }
        Epic stored = epics.get(newEpic.getId());
        if (stored == null) {
            return;
        }
        stored.setName(newEpic.getName());
        stored.setDescription(newEpic.getDescription());
        updateEpicStatus(stored.getId());
    }

    public void updateSubtask(Subtask newSubtask) {
        if (newSubtask == null) {
            return;
        }
        if (!subtasks.containsKey(newSubtask.getId())) {
            return;
        }
        Epic epic = epics.get(newSubtask.getEpicId());
        if (epic == null) {
            throw new IllegalArgumentException("Эпик с id=" + newSubtask.getEpicId() + " не найден");
        }
        subtasks.put(newSubtask.getId(), newSubtask);
        updateEpicStatus(epic.getId());
    }

    public void deleteTaskById(int id) {
        tasks.remove(id);
    }

    public void deleteSubtaskById(int id) {
        Subtask subtask = subtasks.remove(id);
        if (subtask == null) {
            return;
        }
        Epic epic = epics.get(subtask.getEpicId());
        if (epic != null) {
            epic.removeSubtaskId(id);
            updateEpicStatus(epic.getId());
        }
    }

    public void deleteEpicById(int id) {
        Epic epic = epics.remove(id);
        if (epic == null) {
            return;
        }
        epic.getSubtaskIds()
                .forEach(subtasks::remove);

    }

    public void deleteAllTasks() {
        tasks.clear();
    }

    public void deleteAllSubtasks() {
        subtasks.clear();
        epics.values()
                .forEach(epic -> {
                    epic.clearSubtaskIds();
                    updateEpicStatus(epic.getId());
                });
    }

    public void deleteAllEpics() {
        epics.clear();
        subtasks.clear();
    }

    public List<Subtask> getSubtasksByEpicId(int epicId) {
        Epic epic = epics.get(epicId);
        if (epic == null) {
            return List.of();
        }
        return epic.getSubtaskIds().stream()
                .map(subtasks::get)
                .filter(Objects::nonNull)
                .toList();
    }

    private int generateId() {
        return nextId++;
    }

    private void assignId(BaseTask task) {
        task.setId(generateId());
    }

    private void updateEpicStatus(int epicId) {
        Epic epic = epics.get(epicId);
        if (epic == null) {
            return;
        }
        List<Subtask> epicSubtasks = getSubtasksByEpicId(epicId);
        if (epicSubtasks.isEmpty()) {
            epic.applyCalculatedStatus(TaskStatus.NEW);
            return;
        }

        boolean allNew = epicSubtasks.stream()
                .allMatch(subtask -> subtask.getStatus() == TaskStatus.NEW);
        boolean allDone = epicSubtasks.stream()
                .allMatch(subtask -> subtask.getStatus() == TaskStatus.DONE);

        if (allNew) {
            epic.applyCalculatedStatus(TaskStatus.NEW);
        } else if (allDone) {
            epic.applyCalculatedStatus(TaskStatus.DONE);
        } else {
            epic.applyCalculatedStatus(TaskStatus.IN_PROGRESS);
        }
    }
}




