package ru.kanban;

import java.util.*;

public class InMemoryTaskManager implements TaskManager {
    private Map<Integer, Task> tasks = new HashMap<>();
    private Map<Integer, Epic> epics = new HashMap<>();
    private Map<Integer, Subtask> subtasks = new HashMap<>();
    private int nextId = 1;
    private List<BaseTask> history = new ArrayList<>();

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
        Task task = tasks.get(id);
        if (task == null) {
            return Optional.empty();
        }
        addToHistory(task);
        return Optional.of(task);
    }

    public Optional<Epic> getEpicById(int id) {
        Epic epic = epics.get(id);
        if (epic == null) {
            return Optional.empty();
        }
        addToHistory(epic);
        return Optional.of(epic);
    }

    public Optional<Subtask> getSubtaskById(int id) {
        Subtask subtask = subtasks.get(id);
        if (subtask == null) {
            return Optional.empty();
        }
        addToHistory(subtask);
        return Optional.of(subtask);
    }

    public void updateTask(Task newTask) {
        if (newTask == null) {
            throw new IllegalArgumentException("Невозможно обновить: задача == null");
        }
        if (!tasks.containsKey(newTask.getId())) {
            throw new NoSuchElementException("Задача с id=" + newTask.getId() + " не найдена");
        }
        tasks.put(newTask.getId(), newTask);
    }

    public void updateEpic(Epic newEpic) {
        if (newEpic == null) {
            throw new IllegalArgumentException("Невозможно обновить: эпик == null");
        }
        Epic stored = epics.get(newEpic.getId());
        if (stored == null) {
            throw new NoSuchElementException("Эпик с id=" + newEpic.getId() + " не найден");
        }
        stored.setName(newEpic.getName());
        stored.setDescription(newEpic.getDescription());
        updateEpicStatus(stored.getId());
    }

    public void updateSubtask(Subtask newSubtask) {
        if (newSubtask == null) {
            throw new IllegalArgumentException("Невозможно обновить: подзадача == null");
        }
        if (!subtasks.containsKey(newSubtask.getId())) {
            throw new NoSuchElementException("Подзадача с id=" + newSubtask.getId() + " не найдена");
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
            throw new NoSuchElementException("Эпик с id=" + epicId + " не найден");
        }
        List<Subtask> epicSubtasks = getSubtasksByEpicId(epicId);
        if (epicSubtasks.isEmpty()) {
            epic.applyCalculatedStatus(TaskStatus.NEW);
            return;
        }
        boolean allNew = true;
        boolean allDone = true;
        for (Subtask subtask : epicSubtasks) {
            TaskStatus status = subtask.getStatus();

            if (status != TaskStatus.NEW) {
                allNew = false;
            }

            if (status != TaskStatus.DONE) {
                allDone = false;
            }

            if (!allNew && !allDone) {
                break;
            }
        }

        if (allNew) {
            epic.applyCalculatedStatus(TaskStatus.NEW);
        } else if (allDone) {
            epic.applyCalculatedStatus(TaskStatus.DONE);
        } else {
            epic.applyCalculatedStatus(TaskStatus.IN_PROGRESS);
        }
    }

    private void addToHistory(BaseTask task) {
        if (history.size() == 10) {
            history.remove(0);
        }
        history.add(task);
    }

    public List<BaseTask> getHistory() {
        return new ArrayList<>(history);
    }
}




