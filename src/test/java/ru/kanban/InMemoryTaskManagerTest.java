package ru.kanban;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.NoSuchElementException;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;

class InMemoryTaskManagerTest {

    private InMemoryTaskManager manager;
    private Task task;
    private Epic epic;

    @BeforeEach
    void setUp() {
        HistoryManager historyManager = new InMemoryHistoryManager();
        manager = new InMemoryTaskManager(historyManager);
        task = new Task("Задача", "Описание", TaskStatus.NEW);
        epic = new Epic("Эпик", "Описание");
    }

    @Test
    void addTaskShouldAssignId() {
        manager.addTask(task);
        assertThat(task.getId()).isEqualTo(1);
    }

    @Test
    void addTaskShouldStoreInList() {
        manager.addTask(task);
        assertThat(manager.getAllTasks()).hasSize(1);
    }

    @Test
    void addEpicShouldAssignId() {
        manager.addEpic(epic);
        assertThat(epic.getId()).isEqualTo(1);
    }

    @Test
    void addEpicShouldStoreInList() {
        manager.addEpic(epic);
        assertThat(manager.getAllEpics()).hasSize(1);
    }

    @Test
    void addEpicShouldHaveStatusNew() {
        manager.addEpic(epic);
        assertThat(epic.getStatus()).isEqualTo(TaskStatus.NEW);
    }

    @Test
    void addSubtaskShouldAssignId() {
        manager.addEpic(epic);
        Subtask subtask = new Subtask("Подзадача", "Описание", TaskStatus.NEW, epic.getId());
        manager.addSubtask(subtask);
        assertThat(subtask.getId()).isEqualTo(2);
    }

    @Test
    void addSubtaskShouldStoreInList() {
        manager.addEpic(epic);
        Subtask subtask = new Subtask("Подзадача", "Описание", TaskStatus.NEW, epic.getId());
        manager.addSubtask(subtask);
        assertThat(manager.getAllSubtasks()).hasSize(1);
    }

    @Test
    void addSubtaskShouldThrowExceptionIfEpicNotFound() {
        Subtask subtask = new Subtask("Подзадача", "Описание", TaskStatus.NEW, 999);
        assertThatThrownBy(() -> manager.addSubtask(subtask))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Эпик с id=999 не найден");
    }

    @Test
    void addSubtaskShouldAddIdToEpic() {
        manager.addEpic(epic);
        Subtask subtask = new Subtask("Подзадача", "Описание", TaskStatus.NEW, epic.getId());
        manager.addSubtask(subtask);
        assertThat(epic.getSubtaskIds()).contains(subtask.getId());
    }

    @Test
    void getAllTasksShouldReturnEmptyListWhenNoTasks() {
        assertThat(manager.getAllTasks()).isEmpty();
    }

    @Test
    void getAllTasksShouldReturnAllTasks() {
        Task task2 = new Task("Задача 2", "Описание 2", TaskStatus.NEW);
        manager.addTask(task);
        manager.addTask(task2);
        assertThat(manager.getAllTasks()).hasSize(2);
    }

    @Test
    void getAllEpicsShouldReturnEmptyListWhenNoEpics() {
        assertThat(manager.getAllEpics()).isEmpty();
    }

    @Test
    void getAllEpicsShouldReturnAllEpics() {
        Epic epic2 = new Epic("Эпик 2", "Описание 2");
        manager.addEpic(epic);
        manager.addEpic(epic2);
        assertThat(manager.getAllEpics()).hasSize(2);
    }

    @Test
    void getAllSubtasksShouldReturnEmptyListWhenNoSubtasks() {
        assertThat(manager.getAllSubtasks()).isEmpty();
    }

    @Test
    void getAllSubtasksShouldReturnAllSubtasks() {
        manager.addEpic(epic);
        Subtask subtask1 = new Subtask("Подзадача 1", "Описание 1", TaskStatus.NEW, epic.getId());
        Subtask subtask2 = new Subtask("Подзадача 2", "Описание 2", TaskStatus.NEW, epic.getId());
        manager.addSubtask(subtask1);
        manager.addSubtask(subtask2);
        assertThat(manager.getAllSubtasks()).hasSize(2);
    }

    @Test
    void getTaskByIdShouldReturnTask() {
        manager.addTask(task);
        Optional<Task> result = manager.getTaskById(task.getId());
        assertThat(result).hasValue(task);
    }

    @Test
    void getTaskByIdShouldReturnEmptyOptionalWhenTaskNotFound() {
        Optional<Task> result = manager.getTaskById(999);
        assertThat(result).isEmpty();
    }

    @Test
    void getEpicByIdShouldReturnEpic() {
        manager.addEpic(epic);
        Optional<Epic> result = manager.getEpicById(epic.getId());
        assertThat(result).hasValue(epic);
    }

    @Test
    void getEpicByIdShouldReturnEmptyOptionalWhenEpicNotFound() {
        Optional<Epic> result = manager.getEpicById(999);
        assertThat(result).isEmpty();
    }

    @Test
    void getSubtaskByIdShouldReturnSubtask() {
        manager.addEpic(epic);
        Subtask subtask = new Subtask("Подзадача", "Описание", TaskStatus.NEW, epic.getId());
        manager.addSubtask(subtask);
        Optional<Subtask> result = manager.getSubtaskById(subtask.getId());
        assertThat(result).hasValue(subtask);
    }

    @Test
    void getSubtaskByIdShouldReturnEmptyOptionalWhenSubtaskNotFound() {
        Optional<Subtask> result = manager.getSubtaskById(999);
        assertThat(result).isEmpty();
    }

    @Test
    void updateTaskShouldUpdateTaskFields() {
        manager.addTask(task);
        Task updatedTask = new Task("Новое название", "Новое описание", TaskStatus.IN_PROGRESS);
        updatedTask.setId(task.getId());
        manager.updateTask(updatedTask);
        Optional<Task> result = manager.getTaskById(task.getId());
        assertThat(result.get().getName()).isEqualTo("Новое название");
        assertThat(result.get().getStatus()).isEqualTo(TaskStatus.IN_PROGRESS);
    }

    @Test
    void updateTaskShouldThrowExceptionWhenTaskIsNull() {
        assertThatThrownBy(() -> manager.updateTask(null))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Невозможно обновить: задача == null");
    }

    @Test
    void updateTaskShouldThrowExceptionWhenTaskNotFound() {
        task.setId(999);
        assertThatThrownBy(() -> manager.updateTask(task))
                .isInstanceOf(NoSuchElementException.class)
                .hasMessage("Задача с id=999 не найдена");
    }

    @Test
    void updateEpicShouldUpdateEpicFields() {
        manager.addEpic(epic);
        Epic updatedEpic = new Epic("Новое название", "Новое описание");
        updatedEpic.setId(epic.getId());
        manager.updateEpic(updatedEpic);
        Optional<Epic> result = manager.getEpicById(epic.getId());
        assertThat(result.get().getName()).isEqualTo("Новое название");
        assertThat(result.get().getDescription()).isEqualTo("Новое описание");
    }

    @Test
    void updateEpicShouldThrowExceptionWhenEpicIsNull() {
        assertThatThrownBy(() -> manager.updateEpic(null))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Невозможно обновить: эпик == null");
    }

    @Test
    void updateEpicShouldThrowExceptionWhenEpicNotFound() {
        epic.setId(999);
        assertThatThrownBy(() -> manager.updateEpic(epic))
                .isInstanceOf(NoSuchElementException.class)
                .hasMessage("Эпик с id=999 не найден");
    }

    @Test
    void updateSubtaskShouldUpdateSubtaskFields() {
        manager.addEpic(epic);
        Subtask subtask = new Subtask("Старое название", "Старое описание", TaskStatus.NEW, epic.getId());
        manager.addSubtask(subtask);
        Subtask updatedSubtask = new Subtask("Новое название", "Новое описание", TaskStatus.IN_PROGRESS, epic.getId());
        updatedSubtask.setId(subtask.getId());
        manager.updateSubtask(updatedSubtask);
        Optional<Subtask> result = manager.getSubtaskById(subtask.getId());
        assertThat(result.get().getName()).isEqualTo("Новое название");
        assertThat(result.get().getStatus()).isEqualTo(TaskStatus.IN_PROGRESS);
    }

    @Test
    void updateSubtaskShouldThrowExceptionWhenSubtaskIsNull() {
        assertThatThrownBy(() -> manager.updateSubtask(null))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Невозможно обновить: подзадача == null");
    }

    @Test
    void updateSubtaskShouldThrowExceptionWhenSubtaskNotFound() {
        manager.addEpic(epic);
        Subtask subtask = new Subtask("Подзадача", "Описание", TaskStatus.NEW, epic.getId());
        subtask.setId(999);
        assertThatThrownBy(() -> manager.updateSubtask(subtask))
                .isInstanceOf(NoSuchElementException.class)
                .hasMessage("Подзадача с id=999 не найдена");
    }

    @Test
    void updateSubtaskShouldThrowExceptionWhenEpicNotFound() {
        manager.addEpic(epic);
        Subtask subtask = new Subtask("Подзадача", "Описание", TaskStatus.NEW, epic.getId());
        manager.addSubtask(subtask);
        Subtask updatedSubtask = new Subtask("Новое название", "Новое описание", TaskStatus.IN_PROGRESS, 999);
        updatedSubtask.setId(subtask.getId());
        assertThatThrownBy(() -> manager.updateSubtask(updatedSubtask))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Эпик с id=999 не найден");
    }

    @Test
    void deleteTaskByIdShouldRemoveTask() {
        manager.addTask(task);
        manager.deleteTaskById(task.getId());
        assertThat(manager.getTaskById(task.getId())).isEmpty();
    }

    @Test
    void deleteTaskByIdShouldDoNothingWhenTaskNotFound() {
        manager.deleteTaskById(999);
        assertThat(manager.getAllTasks()).isEmpty();
    }

    @Test
    void deleteSubtaskByIdShouldRemoveSubtask() {
        manager.addEpic(epic);
        Subtask subtask = new Subtask("Подзадача", "Описание", TaskStatus.NEW, epic.getId());
        manager.addSubtask(subtask);
        manager.deleteSubtaskById(subtask.getId());
        assertThat(manager.getAllSubtasks()).isEmpty();
    }

    @Test
    void deleteSubtaskByIdShouldDoNothingWhenSubtaskNotFound() {
        manager.deleteSubtaskById(999);
        assertThat(manager.getAllSubtasks()).isEmpty();
    }

    @Test
    void deleteSubtaskByIdShouldRemoveIdFromEpic() {
        manager.addEpic(epic);
        Subtask subtask = new Subtask("Подзадача", "Описание", TaskStatus.NEW, epic.getId());
        manager.addSubtask(subtask);
        manager.deleteSubtaskById(subtask.getId());
        assertThat(epic.getSubtaskIds()).isEmpty();
    }

    @Test
    void deleteEpicByIdShouldRemoveEpic() {
        manager.addEpic(epic);
        manager.deleteEpicById(epic.getId());
        assertThat(manager.getAllEpics()).isEmpty();
    }

    @Test
    void deleteEpicByIdShouldDoNothingWhenEpicNotFound() {
        manager.deleteEpicById(999);
        assertThat(manager.getAllEpics()).isEmpty();
    }

    @Test
    void deleteEpicByIdShouldRemoveItsSubtasks() {
        manager.addEpic(epic);
        Subtask subtask1 = new Subtask("Подзадача 1", "Описание", TaskStatus.NEW, epic.getId());
        Subtask subtask2 = new Subtask("Подзадача 2", "Описание", TaskStatus.NEW, epic.getId());
        manager.addSubtask(subtask1);
        manager.addSubtask(subtask2);
        manager.deleteEpicById(epic.getId());
        assertThat(manager.getAllSubtasks()).isEmpty();
    }

    @Test
    void deleteAllTasksShouldRemoveAllTasks() {
        Task task2 = new Task("Задача 2", "Описание 2", TaskStatus.NEW);
        manager.addTask(task);
        manager.addTask(task2);
        manager.deleteAllTasks();
        assertThat(manager.getAllTasks()).isEmpty();
    }

    @Test
    void deleteAllTasksShouldDoNothingWhenNoTasks() {
        manager.deleteAllTasks();
        assertThat(manager.getAllTasks()).isEmpty();
    }

    @Test
    void deleteAllSubtasksShouldRemoveAllSubtasks() {
        manager.addEpic(epic);
        Subtask subtask1 = new Subtask("Подзадача 1", "Описание 1", TaskStatus.NEW, epic.getId());
        Subtask subtask2 = new Subtask("Подзадача 2", "Описание 2", TaskStatus.NEW, epic.getId());
        manager.addSubtask(subtask1);
        manager.addSubtask(subtask2);
        manager.deleteAllSubtasks();
        assertThat(manager.getAllSubtasks()).isEmpty();
    }

    @Test
    void deleteAllSubtasksShouldDoNothingWhenNoSubtasks() {
        manager.deleteAllSubtasks();
        assertThat(manager.getAllSubtasks()).isEmpty();
    }

    @Test
    void deleteAllSubtasksShouldUpdateEpicStatusToNew() {
        manager.addEpic(epic);
        Subtask subtask = new Subtask("Подзадача", "Описание", TaskStatus.DONE, epic.getId());
        manager.addSubtask(subtask);
        assertThat(epic.getStatus()).isEqualTo(TaskStatus.DONE);
        manager.deleteAllSubtasks();
        assertThat(epic.getStatus()).isEqualTo(TaskStatus.NEW);
    }

    @Test
    void deleteAllEpicsShouldRemoveAllEpics() {
        Epic epic2 = new Epic("Эпик 2", "Описание 2");
        manager.addEpic(epic);
        manager.addEpic(epic2);
        manager.deleteAllEpics();
        assertThat(manager.getAllEpics()).isEmpty();
    }

    @Test
    void deleteAllEpicsShouldDoNothingWhenNoEpics() {
        manager.deleteAllEpics();
        assertThat(manager.getAllEpics()).isEmpty();
    }

    @Test
    void deleteAllEpicsShouldRemoveAllSubtasks() {
        manager.addEpic(epic);
        Subtask subtask = new Subtask("Подзадача", "Описание", TaskStatus.NEW, epic.getId());
        manager.addSubtask(subtask);
        manager.deleteAllEpics();
        assertThat(manager.getAllSubtasks()).isEmpty();
    }

    @Test
    void getSubtasksByEpicIdShouldReturnSubtasks() {
        manager.addEpic(epic);
        Subtask subtask1 = new Subtask("Подзадача 1", "Описание 1", TaskStatus.NEW, epic.getId());
        Subtask subtask2 = new Subtask("Подзадача 2", "Описание 2", TaskStatus.NEW, epic.getId());
        manager.addSubtask(subtask1);
        manager.addSubtask(subtask2);
        assertThat(manager.getSubtasksByEpicId(epic.getId())).hasSize(2);
    }

    @Test
    void getSubtasksByEpicIdShouldReturnEmptyListWhenNoSubtasks() {
        manager.addEpic(epic);
        assertThat(manager.getSubtasksByEpicId(epic.getId())).isEmpty();
    }

    @Test
    void getSubtasksByEpicIdShouldReturnEmptyListWhenEpicNotFound() {
        assertThat(manager.getSubtasksByEpicId(999)).isEmpty();
    }

    @Test
    void epicWithoutSubtasksShouldHaveStatusNew() {
        manager.addEpic(epic);
        assertThat(epic.getStatus()).isEqualTo(TaskStatus.NEW);
    }

    @Test
    void epicWithAllNewSubtasksShouldHaveStatusNew() {
        manager.addEpic(epic);
        Subtask subtask1 = new Subtask("Подзадача 1", "Описание", TaskStatus.NEW, epic.getId());
        Subtask subtask2 = new Subtask("Подзадача 2", "Описание", TaskStatus.NEW, epic.getId());
        manager.addSubtask(subtask1);
        manager.addSubtask(subtask2);
        assertThat(epic.getStatus()).isEqualTo(TaskStatus.NEW);
    }

    @Test
    void epicWithAllDoneSubtasksShouldHaveStatusDone() {
        manager.addEpic(epic);
        Subtask subtask1 = new Subtask("Подзадача 1", "Описание", TaskStatus.DONE, epic.getId());
        Subtask subtask2 = new Subtask("Подзадача 2", "Описание", TaskStatus.DONE, epic.getId());
        manager.addSubtask(subtask1);
        manager.addSubtask(subtask2);
        assertThat(epic.getStatus()).isEqualTo(TaskStatus.DONE);
    }

    @Test
    void epicWithMixedNewAndDoneSubtasksShouldHaveStatusInProgress() {
        manager.addEpic(epic);
        Subtask subtask1 = new Subtask("Подзадача 1", "Описание", TaskStatus.NEW, epic.getId());
        Subtask subtask2 = new Subtask("Подзадача 2", "Описание", TaskStatus.DONE, epic.getId());
        manager.addSubtask(subtask1);
        manager.addSubtask(subtask2);
        assertThat(epic.getStatus()).isEqualTo(TaskStatus.IN_PROGRESS);
    }

    @Test
    void epicWithInProgressSubtaskShouldHaveStatusInProgress() {
        manager.addEpic(epic);
        Subtask subtask1 = new Subtask("Подзадача 1", "Описание", TaskStatus.NEW, epic.getId());
        Subtask subtask2 = new Subtask("Подзадача 2", "Описание", TaskStatus.IN_PROGRESS, epic.getId());
        manager.addSubtask(subtask1);
        manager.addSubtask(subtask2);
        assertThat(epic.getStatus()).isEqualTo(TaskStatus.IN_PROGRESS);
    }

    @Test
    void epicStatusShouldUpdateWhenSubtaskStatusChanges() {
        manager.addEpic(epic);
        Subtask subtask = new Subtask("Подзадача", "Описание", TaskStatus.NEW, epic.getId());
        manager.addSubtask(subtask);
        Subtask updatedSubtask = new Subtask("Подзадача", "Описание", TaskStatus.DONE, epic.getId());
        updatedSubtask.setId(subtask.getId());
        manager.updateSubtask(updatedSubtask);
        assertThat(epic.getStatus()).isEqualTo(TaskStatus.DONE);
    }

    @Test
    void epicStatusShouldUpdateWhenSubtaskDeleted() {
        manager.addEpic(epic);
        Subtask subtask1 = new Subtask("Подзадача 1", "Описание", TaskStatus.DONE, epic.getId());
        Subtask subtask2 = new Subtask("Подзадача 2", "Описание", TaskStatus.NEW, epic.getId());
        manager.addSubtask(subtask1);
        manager.addSubtask(subtask2);
        assertThat(epic.getStatus()).isEqualTo(TaskStatus.IN_PROGRESS);
        manager.deleteSubtaskById(subtask2.getId());
        assertThat(epic.getStatus()).isEqualTo(TaskStatus.DONE);
    }

    @Test
    void getTaskByIdShouldAddToHistory() {
        manager.addTask(task);
        manager.getTaskById(task.getId());
        assertThat(manager.getHistory()).hasSize(1);
        assertThat(manager.getHistory().get(0)).isEqualTo(task);
    }

    @Test
    void getEpicByIdShouldAddToHistory() {
        manager.addEpic(epic);
        manager.getEpicById(epic.getId());
        assertThat(manager.getHistory()).hasSize(1);
        assertThat(manager.getHistory().get(0)).isEqualTo(epic);
    }

    @Test
    void getSubtaskByIdShouldAddToHistory() {
        manager.addEpic(epic);
        Subtask subtask = new Subtask("Подзадача", "Описание", TaskStatus.NEW, epic.getId());
        manager.addSubtask(subtask);
        manager.getSubtaskById(subtask.getId());
        assertThat(manager.getHistory()).hasSize(1);
        assertThat(manager.getHistory().get(0)).isEqualTo(subtask);
    }

    @Test
    void getByIdShouldNotAddToHistoryWhenNotFound() {
        manager.getTaskById(999);
        manager.getEpicById(999);
        manager.getSubtaskById(999);
        assertThat(manager.getHistory()).isEmpty();
    }

    @Test
    void idsShouldBeUniqueAcrossAllTaskTypes() {
        manager.addTask(task);
        manager.addEpic(epic);
        assertThat(task.getId()).isNotEqualTo(epic.getId());
    }
}