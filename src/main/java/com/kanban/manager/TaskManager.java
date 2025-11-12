package com.kanban.manager;

import com.kanban.model.*;

import java.util.*;

public class TaskManager {
    public Map<Integer, Task> tasks = new HashMap<>();
    public Map<Integer, Epic> epics = new HashMap<>();
    public Map<Integer, Subtask> subtasks = new HashMap<>();

    public void addTask(Task task) {
        tasks.put(task.getId(), task);
    }

    public void addEpic(Epic epic) {
        epics.put(epic.getId(), epic);
    }

    public void addSubtask(Subtask subtask) {
        Epic epic = epics.get(subtask.getEpicId());
        if (epic == null) {
            throw new IllegalArgumentException("Эпик с id=" + subtask.getEpicId() + " не найден");
        }
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

    public Task getTaskById(int id) {
        return tasks.get(id);
    }

    public Epic getEpicById(int id) {
        return epics.get(id);
    }

    public Subtask getSubtaskById(int id) {
        return subtasks.get(id);
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
                .forEach(Epic::clearSubtaskIds);
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

    public static void main(String[] args) {
        TaskManager manager = new TaskManager();
        System.out.println("===== Сценарий 1: Базовое создание задач =====");
        scenario1_createTasks(manager);
    }

    private static void scenario1_createTasks(TaskManager manager) {
        Task task1 = new Task("Переезд", "Собрать вещи", TaskStatus.NEW);
        Task task2 = new Task("Обучение Java", "Завершить модуль по ООП", TaskStatus.IN_PROGRESS);
        manager.addTask(task1);
        manager.addTask(task2);
        Epic epic1 = new Epic("Запуск проекта Kanban", "Сделать первую версию трекера");
        Epic epic2 = new Epic("Подготовка к отпуску", "Все спланировать");
        Subtask s1 = new Subtask("Сделать модель задач", "Task/Epic/Subtask/Status", TaskStatus.NEW, epic1.getId());
        Subtask s2 = new Subtask("Реализовать менеджер задач", "CRUD + правила эпика", TaskStatus.NEW, epic1.getId());
        Subtask s3 = new Subtask("Собрать вещи", "Сумка, документы", TaskStatus.NEW, epic2.getId());
        manager.addSubtask(s1);
        manager.addSubtask(s2);
        manager.addSubtask(s3);
        printAll(manager);
        System.out.println("\nПроверка get*ById:");
        System.out.println("Task по id=" + task1.getId() + ": " + manager.getTaskById(task1.getId()).getName());
    }

    private static void printAll(TaskManager manager) {
        System.out.println("\n--- Все обычные задачи ---");
        if (manager.getAllTasks().isEmpty()) {
            System.out.println("(пусто)");
        } else {
            manager.getAllTasks().forEach(task ->
                    System.out.println("Task: " + task.getId() + " | " + task.getName() + " | " + task.getStatus())
            );
        }
    }
}




