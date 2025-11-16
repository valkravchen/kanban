package ru.kanban;

public class Main {
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
        manager.addEpic(epic1);
        manager.addEpic(epic2);
        Subtask subtask1 = new Subtask("Сделать модель задач",
                "Task/Epic/Subtask/Status", TaskStatus.NEW, epic1.getId());
        Subtask subtask2 = new Subtask("Реализовать менеджер задач",
                "CRUD + правила эпика", TaskStatus.NEW, epic1.getId());
        Subtask subtask3 = new Subtask("Собрать вещи",
                "Сумка, документы", TaskStatus.NEW, epic2.getId());
        manager.addSubtask(subtask1);
        manager.addSubtask(subtask2);
        manager.addSubtask(subtask3);
        printAll(manager);
        System.out.println("\nПроверка get*ById:");
        System.out.println("Task по id=" + task1.getId() + ": " + manager.getTaskById(task1.getId()).getName());
        System.out.println("Epic по id=" + epic1.getId() + ": " + manager.getEpicById(epic1.getId()).getName());
        System.out.println("Subtask по id=" + subtask1.getId() + ": " + manager.getSubtaskById(subtask1.getId()).getName());

        System.out.println("\nПроверка getSubtasksByEpicId:");
        System.out.println("Подзадачи эпика '" + epic1.getName() + "':");
        manager.getSubtasksByEpicId(epic1.getId())
                .forEach(st -> System.out.println("  - " + st.getId() + " " + st.getName()));
    }

    private static void printAll(TaskManager manager) {
        System.out.println("\n--- Все обычные задачи ---");
        if (manager.getAllTasks().isEmpty()) {
            System.out.println("(пусто)");
        } else {
            manager.getAllTasks().forEach(task ->
                    System.out.println("Task: " + task.getId() + " | " + task.getName()
                            + " | " + task.getStatus())
            );
        }
        System.out.println("\n--- Все эпики ---");
        if (manager.getAllEpics().isEmpty()) {
            System.out.println("(пусто)");
        } else {
            manager.getAllEpics().forEach(Main::printEpicDetailsInline);
        }

        System.out.println("\n--- Все подзадачи ---");
        if (manager.getAllSubtasks().isEmpty()) {
            System.out.println("(пусто)");
        } else {
            manager.getAllSubtasks().forEach(subtask ->
                    System.out.println("Subtask: " + subtask.getId()
                            + " | " + subtask.getName()
                            + " | " + subtask.getStatus()
                            + " | epicId=" + subtask.getEpicId() + " | " + subtask.getName()
                            + " | " + subtask.getStatus()
                            + " | epicId=" + subtask.getEpicId()
                    )
            );
        }
    }

    private static void printEpicCompact(Epic epic) {
        System.out.println("Epic: " + epic.getId()
                + " | " + epic.getName()
                + " | status=" + epic.getStatus()
                + " | subtasks=" + epic.getSubtaskIds().size());
    }

    private static void printEpicDetailsInline(Epic epic) {
        System.out.println("Epic: " + epic.getId()
                + " | " + epic.getName()
                + " | " + epic.getStatus()
                + " | subtasks=" + epic.getSubtaskIds()
        );
    }

    private static void printEpicDetails(TaskManager manager, int epicId) {
        Epic epic = manager.getEpicById(epicId);
        if (epic == null) {
            System.out.println("Эпик id=" + epicId + " не найден");
            return;
        }
        System.out.println("Epic: " + epic.getId() +
                " | " + epic.getName() +
                " | status=" + epic.getStatus());
        var subs = manager.getSubtasksByEpicId(epicId);
        if (subs.isEmpty()) {
            System.out.println("  Подзадач нет");
        } else {
            System.out.println("  Подзадачи:");
            subs.forEach(subtask ->
                    System.out.println("    - " + subtask.getId()
                            + " | " + subtask.getName()
                            + " | " + subtask.getStatus())
            );
        }
    }
}