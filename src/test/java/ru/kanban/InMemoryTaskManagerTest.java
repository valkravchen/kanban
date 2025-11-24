package ru.kanban;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.NoSuchElementException;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;

class InMemoryTaskManagerTest {

    private InMemoryTaskManager manager;

    @BeforeEach
    void setUp() {
        HistoryManager historyManager = new InMemoryHistoryManager();
        this.manager = new InMemoryTaskManager(historyManager);
    }

    @Test
    void addTaskShouldAssignId() {
        Task task = new Task("Первая задача",
                "Сделать тест для метода addTask: проверка ID = 1", TaskStatus.NEW);
        manager.addTask(task);
        int expected = 1;
        int result = task.getId();
        assertThat(result).isEqualTo(expected);
    }

    @Test
    void addTaskShouldStoreInList() {
        Task task = new Task("Вторая задача",
                "Сделать тест для метода addTask: проверить, что size = 1", TaskStatus.NEW);
        manager.addTask(task);
        int expected = 1;
        int result = manager.getAllTasks().size();
        assertThat(result).isEqualTo(expected);
    }

    @Test
    void addEpicShouldAssignId() {
        Epic epic = new Epic("Первый эпик",
                "Сделать тест для метода addEpic: проверка ID = 1");
        manager.addEpic(epic);
        int expected = 1;
        int result = epic.getId();
        assertThat(result).isEqualTo(expected);
    }

    @Test
    void addEpicShouldStoreInList() {
        Epic epic = new Epic("Второй эпик",
                "Сделать тест для метода addEpic: проверить, что size = 1");
        manager.addEpic(epic);
        int expected = 1;
        int result = manager.getAllEpics().size();
        assertThat(result).isEqualTo(expected);
    }

    @Test
    void addEpicShouldHaveStatusNew() {
        Epic epic = new Epic("Третий эпик",
                "Сделать тест для метода addEpic: проверить, что статус = NEW");
        manager.addEpic(epic);

        TaskStatus expected = TaskStatus.NEW;
        TaskStatus result = epic.getStatus();
        assertThat(result).isEqualTo(expected);
    }

    @Test
    void addSubtaskShouldAssignId() {
        Epic epic = new Epic("Четвёртый эпик", "Проверить подзадачу по ID");
        manager.addEpic(epic);
        Subtask subtask = new Subtask("Первая подзадача",
                "Сделать тест для метода addSubtask: проверка ID = 2", TaskStatus.NEW, epic.getId());
        manager.addSubtask(subtask);
        int expected = 2;
        int result = subtask.getId();
        assertThat(result).isEqualTo(expected);
    }

    @Test
    void addSubtaskShouldStoreInList() {
        Epic epic = new Epic("Пятый эпик", "Проверить количество подзадач");
        manager.addEpic(epic);
        Subtask subtask = new Subtask("Вторая подзадача",
                "Сделать тест для метода addSubtask: проверить, что size = 1",
                TaskStatus.NEW, epic.getId());
        manager.addSubtask(subtask);
        int expected = 1;
        int result = manager.getAllSubtasks().size();
        assertThat(result).isEqualTo(expected);
    }

    @Test
    void addSubtaskShouldThrowExceptionIfEpicNotFound() {
        Subtask subtask = new Subtask("Третья подзадача",
                "Сделать тест для метода addSubtask: проверка исключения",
                TaskStatus.NEW, 999);

        assertThatThrownBy(() -> manager.addSubtask(subtask))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void addSubtaskShouldAddIdToEpic() {
        Epic epic = new Epic("Эпик", "Описание");
        manager.addEpic(epic);
        Subtask subtask = new Subtask("Подзадача", "Описание", TaskStatus.NEW, epic.getId());
        manager.addSubtask(subtask);

        assertThat(epic.getSubtaskIds()).contains(subtask.getId());
    }

    @Test
    void getAllTasksShouldReturnEmptyListWhenNoTasks() {
        int expected = 0;
        int result = manager.getAllTasks().size();
        assertThat(result).isEqualTo(expected);
    }

    @Test
    void getAllTasksShouldReturnAllTasks() {
        Task task1 = new Task("Задача 1", "Описание 1", TaskStatus.NEW);
        Task task2 = new Task("Задача 2", "Описание 2", TaskStatus.NEW);
        manager.addTask(task1);
        manager.addTask(task2);
        int expected = 2;
        int result = manager.getAllTasks().size();
        assertThat(result).isEqualTo(expected);
    }

    @Test
    void getAllEpicsShouldReturnEmptyListWhenNoEpics() {
        int expected = 0;
        int result = manager.getAllEpics().size();
        assertThat(result).isEqualTo(expected);
    }

    @Test
    void getAllEpicsShouldReturnAllEpics() {
        Epic epic1 = new Epic("Эпик 1", "Описание 1");
        Epic epic2 = new Epic("Эпик 2", "Описание 2");
        manager.addEpic(epic1);
        manager.addEpic(epic2);
        int expected = 2;
        int result = manager.getAllEpics().size();
        assertThat(result).isEqualTo(expected);
    }

    @Test
    void getAllSubtasksShouldReturnEmptyListWhenNoSubtasks() {
        int expected = 0;
        int result = manager.getAllSubtasks().size();
        assertThat(result).isEqualTo(expected);
    }

    @Test
    void getAllSubtasksShouldReturnAllSubtasks() {
        Epic epic = new Epic("Эпик", "Описание");
        manager.addEpic(epic);
        Subtask subtask1 = new Subtask("Подзадача 1", "Описание 1", TaskStatus.NEW, epic.getId());
        Subtask subtask2 = new Subtask("Подзадача 2", "Описание 2", TaskStatus.NEW, epic.getId());
        manager.addSubtask(subtask1);
        manager.addSubtask(subtask2);
        int expected = 2;
        int result = manager.getAllSubtasks().size();
        assertThat(result).isEqualTo(expected);
    }

    @Test
    void getTaskByIdShouldReturnTask() {
        Task task = new Task("Задача", "Описание", TaskStatus.NEW);
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
        Epic epic = new Epic("Эпик", "Описание");
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
        Epic epic = new Epic("Эпик", "Описание");
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
        Task task = new Task("Старое название", "Старое описание", TaskStatus.NEW);
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
                .hasMessageContaining("null");
    }

    @Test
    void updateTaskShouldThrowExceptionWhenTaskNotFound() {
        Task task = new Task("Задача", "Описание", TaskStatus.NEW);
        task.setId(999);
        assertThatThrownBy(() -> manager.updateTask(task))
                .isInstanceOf(NoSuchElementException.class);
    }

    @Test
    void updateEpicShouldUpdateEpicFields() {
        Epic epic = new Epic("Старое название", "Старое описание");
        manager.addEpic(epic);
        Epic updatedEpic = new Epic("Новое название", "Новое описание");
        updatedEpic.setId(epic.getId()); // важно! сохраняем ID
        manager.updateEpic(updatedEpic);
        Optional<Epic> result = manager.getEpicById(epic.getId());
        assertThat(result.get().getName()).isEqualTo("Новое название");
        assertThat(result.get().getDescription()).isEqualTo("Новое описание");
    }

    @Test
    void updateEpicShouldThrowExceptionWhenEpicIsNull() {
        assertThatThrownBy(() -> manager.updateEpic(null))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("null");
    }

    @Test
    void updateEpicShouldThrowExceptionWhenEpicNotFound() {
        Epic epic = new Epic("Эпик", "Описание");
        epic.setId(999);
        assertThatThrownBy(() -> manager.updateEpic(epic))
                .isInstanceOf(NoSuchElementException.class);
    }

    @Test
    void updateSubtaskShouldUpdateSubtaskFields() {
        Epic epic = new Epic("Эпик", "Описание");
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
                .hasMessageContaining("null");
    }

    @Test
    void updateSubtaskShouldThrowExceptionWhenSubtaskNotFound() {
        Epic epic = new Epic("Эпик", "Описание");
        manager.addEpic(epic);
        Subtask subtask = new Subtask("Подзадача", "Описание", TaskStatus.NEW, epic.getId());
        subtask.setId(999);
        assertThatThrownBy(() -> manager.updateSubtask(subtask))
                .isInstanceOf(NoSuchElementException.class);
    }

    @Test
    void updateSubtaskShouldThrowExceptionWhenEpicNotFound() {
        Epic epic = new Epic("Эпик", "Описание");
        manager.addEpic(epic);
        Subtask subtask = new Subtask("Подзадача", "Описание", TaskStatus.NEW, epic.getId());
        manager.addSubtask(subtask);
        Subtask updatedSubtask = new Subtask("Новое название", "Новое описание", TaskStatus.IN_PROGRESS, 999);
        updatedSubtask.setId(subtask.getId());
        assertThatThrownBy(() -> manager.updateSubtask(updatedSubtask))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void deleteTaskByIdShouldRemoveTask() {
        Task task = new Task("Задача", "Описание", TaskStatus.NEW);
        manager.addTask(task);
        manager.deleteTaskById(task.getId());
        Optional<Task> result = manager.getTaskById(task.getId());
        assertThat(result).isEmpty();
    }

    @Test
    void deleteTaskByIdShouldDoNothingWhenTaskNotFound() {
        manager.deleteTaskById(999);
        int expected = 0;
        int result = manager.getAllTasks().size();
        assertThat(result).isEqualTo(expected);
    }

    @Test
    void deleteSubtaskByIdShouldRemoveSubtask() {
        Epic epic = new Epic("Эпик", "Описание");
        manager.addEpic(epic);
        Subtask subtask = new Subtask("Подзадача", "Описание", TaskStatus.NEW, epic.getId());
        manager.addSubtask(subtask);
        manager.deleteSubtaskById(subtask.getId());
        int expected = 0;
        int result = manager.getAllSubtasks().size();
        assertThat(result).isEqualTo(expected);
    }

    @Test
    void deleteSubtaskByIdShouldDoNothingWhenSubtaskNotFound() {
        manager.deleteSubtaskById(999);
        int expected = 0;
        int result = manager.getAllSubtasks().size();
        assertThat(result).isEqualTo(expected);
    }

    @Test
    void deleteEpicByIdShouldRemoveEpic() {
        Epic epic = new Epic("Эпик", "Описание");
        manager.addEpic(epic);
        manager.deleteEpicById(epic.getId());
        int expected = 0;
        int result = manager.getAllEpics().size();
        assertThat(result).isEqualTo(expected);
    }

    @Test
    void deleteEpicByIdShouldDoNothingWhenEpicNotFound() {
        manager.deleteEpicById(999);
        int expected = 0;
        int result = manager.getAllEpics().size();
        assertThat(result).isEqualTo(expected);
    }

    @Test
    void deleteAllTasksShouldRemoveAllTasks() {
        Task task1 = new Task("Задача 1", "Описание 1", TaskStatus.NEW);
        Task task2 = new Task("Задача 2", "Описание 2", TaskStatus.NEW);
        manager.addTask(task1);
        manager.addTask(task2);
        manager.deleteAllTasks();
        int expected = 0;
        int result = manager.getAllTasks().size();
        assertThat(result).isEqualTo(expected);
    }

    @Test
    void deleteAllTasksShouldDoNothingWhenNoTasks() {
        manager.deleteAllTasks();
        int expected = 0;
        int result = manager.getAllTasks().size();
        assertThat(result).isEqualTo(expected);
    }

    @Test
    void deleteAllSubtasksShouldRemoveAllSubtasks() {
        Epic epic = new Epic("Эпик", "Описание");
        manager.addEpic(epic);
        Subtask subtask1 = new Subtask("Подзадача 1", "Описание 1", TaskStatus.NEW, epic.getId());
        Subtask subtask2 = new Subtask("Подзадача 2", "Описание 2", TaskStatus.NEW, epic.getId());
        manager.addSubtask(subtask1);
        manager.addSubtask(subtask2);
        manager.deleteAllSubtasks();
        int expected = 0;
        int result = manager.getAllSubtasks().size();
        assertThat(result).isEqualTo(expected);
    }

    @Test
    void deleteAllSubtasksShouldDoNothingWhenNoSubtasks() {
        manager.deleteAllSubtasks();
        int expected = 0;
        int result = manager.getAllSubtasks().size();
        assertThat(result).isEqualTo(expected);
    }

    @Test
    void deleteAllEpicsShouldRemoveAllEpics() {
        Epic epic1 = new Epic("Эпик 1", "Описание 1");
        Epic epic2 = new Epic("Эпик 2", "Описание 2");
        manager.addEpic(epic1);
        manager.addEpic(epic2);
        manager.deleteAllEpics();
        int expected = 0;
        int result = manager.getAllEpics().size();
        assertThat(result).isEqualTo(expected);
    }

    @Test
    void deleteAllEpicsShouldDoNothingWhenNoEpics() {
        manager.deleteAllEpics();
        int expected = 0;
        int result = manager.getAllEpics().size();
        assertThat(result).isEqualTo(expected);
    }

    @Test
    void getSubtasksByEpicIdShouldReturnSubtasks() {
        Epic epic = new Epic("Эпик", "Описание");
        manager.addEpic(epic);
        Subtask subtask1 = new Subtask("Подзадача 1", "Описание 1", TaskStatus.NEW, epic.getId());
        Subtask subtask2 = new Subtask("Подзадача 2", "Описание 2", TaskStatus.NEW, epic.getId());
        manager.addSubtask(subtask1);
        manager.addSubtask(subtask2);
        int expected = 2;
        int result = manager.getSubtasksByEpicId(epic.getId()).size();
        assertThat(result).isEqualTo(expected);
    }

    @Test
    void getSubtasksByEpicIdShouldReturnEmptyListWhenNoSubtasks() {
        Epic epic = new Epic("Эпик", "Описание");
        manager.addEpic(epic);
        int expected = 0;
        int result = manager.getSubtasksByEpicId(epic.getId()).size();
        assertThat(result).isEqualTo(expected);
    }

    @Test
    void getSubtasksByEpicIdShouldReturnEmptyListWhenEpicNotFound() {
        int expected = 0;
        int result = manager.getSubtasksByEpicId(999).size();
        assertThat(result).isEqualTo(expected);
    }

    @Test
    void epicWithoutSubtasksShouldHaveStatusNew() {
        Epic epic = new Epic("Эпик", "Описание");
        manager.addEpic(epic);
        TaskStatus expected = TaskStatus.NEW;
        TaskStatus result = epic.getStatus();
        assertThat(result).isEqualTo(expected);
    }

    @Test
    void epicWithAllNewSubtasksShouldHaveStatusNew() {
        Epic epic = new Epic("Эпик", "Описание");
        manager.addEpic(epic);
        Subtask subtask1 = new Subtask("Подзадача 1", "Описание", TaskStatus.NEW, epic.getId());
        Subtask subtask2 = new Subtask("Подзадача 2", "Описание", TaskStatus.NEW, epic.getId());
        manager.addSubtask(subtask1);
        manager.addSubtask(subtask2);
        TaskStatus expected = TaskStatus.NEW;
        TaskStatus result = epic.getStatus();
        assertThat(result).isEqualTo(expected);
    }

    @Test
    void epicWithAllDoneSubtasksShouldHaveStatusDone() {
        Epic epic = new Epic("Эпик", "Описание");
        manager.addEpic(epic);
        Subtask subtask1 = new Subtask("Подзадача 1", "Описание", TaskStatus.DONE, epic.getId());
        Subtask subtask2 = new Subtask("Подзадача 2", "Описание", TaskStatus.DONE, epic.getId());
        manager.addSubtask(subtask1);
        manager.addSubtask(subtask2);
        TaskStatus expected = TaskStatus.DONE;
        TaskStatus result = epic.getStatus();
        assertThat(result).isEqualTo(expected);
    }

    @Test
    void epicWithMixedNewAndDoneSubtasksShouldHaveStatusInProgress() {
        Epic epic = new Epic("Эпик", "Описание");
        manager.addEpic(epic);
        Subtask subtask1 = new Subtask("Подзадача 1", "Описание", TaskStatus.NEW, epic.getId());
        Subtask subtask2 = new Subtask("Подзадача 2", "Описание", TaskStatus.DONE, epic.getId());
        manager.addSubtask(subtask1);
        manager.addSubtask(subtask2);
        TaskStatus expected = TaskStatus.IN_PROGRESS;
        TaskStatus result = epic.getStatus();
        assertThat(result).isEqualTo(expected);
    }

    @Test
    void epicWithInProgressSubtaskShouldHaveStatusInProgress() {
        Epic epic = new Epic("Эпик", "Описание");
        manager.addEpic(epic);
        Subtask subtask1 = new Subtask("Подзадача 1", "Описание", TaskStatus.NEW, epic.getId());
        Subtask subtask2 = new Subtask("Подзадача 2", "Описание", TaskStatus.IN_PROGRESS, epic.getId());
        manager.addSubtask(subtask1);
        manager.addSubtask(subtask2);
        TaskStatus expected = TaskStatus.IN_PROGRESS;
        TaskStatus result = epic.getStatus();
        assertThat(result).isEqualTo(expected);
    }

    @Test
    void epicStatusShouldUpdateWhenSubtaskStatusChanges() {
        Epic epic = new Epic("Эпик", "Описание");
        manager.addEpic(epic);
        Subtask subtask = new Subtask("Подзадача", "Описание", TaskStatus.NEW, epic.getId());
        manager.addSubtask(subtask);
        Subtask updatedSubtask = new Subtask("Подзадача", "Описание", TaskStatus.DONE, epic.getId());
        updatedSubtask.setId(subtask.getId());
        manager.updateSubtask(updatedSubtask);
        TaskStatus expected = TaskStatus.DONE;
        TaskStatus result = epic.getStatus();
        assertThat(result).isEqualTo(expected);
    }

    @Test
    void epicStatusShouldUpdateWhenSubtaskDeleted() {
        Epic epic = new Epic("Эпик", "Описание");
        manager.addEpic(epic);
        Subtask subtask1 = new Subtask("Подзадача 1", "Описание", TaskStatus.DONE, epic.getId());
        Subtask subtask2 = new Subtask("Подзадача 2", "Описание", TaskStatus.NEW, epic.getId());
        manager.addSubtask(subtask1);
        manager.addSubtask(subtask2);
        assertThat(epic.getStatus()).isEqualTo(TaskStatus.IN_PROGRESS);
        manager.deleteSubtaskById(subtask2.getId());
        TaskStatus expected = TaskStatus.DONE;
        TaskStatus result = epic.getStatus();
        assertThat(result).isEqualTo(expected);
    }

    @Test
    void deleteEpicByIdShouldRemoveItsSubtasks() {
        Epic epic = new Epic("Эпик", "Описание");
        manager.addEpic(epic);
        Subtask subtask1 = new Subtask("Подзадача 1", "Описание", TaskStatus.NEW, epic.getId());
        Subtask subtask2 = new Subtask("Подзадача 2", "Описание", TaskStatus.NEW, epic.getId());
        manager.addSubtask(subtask1);
        manager.addSubtask(subtask2);
        manager.deleteEpicById(epic.getId());
        assertThat(manager.getAllSubtasks()).isEmpty();
    }

    @Test
    void deleteAllSubtasksShouldUpdateEpicStatusToNew() {
        Epic epic = new Epic("Эпик", "Описание");
        manager.addEpic(epic);
        Subtask subtask = new Subtask("Подзадача", "Описание", TaskStatus.DONE, epic.getId());
        manager.addSubtask(subtask);
        assertThat(epic.getStatus()).isEqualTo(TaskStatus.DONE);
        manager.deleteAllSubtasks();
        assertThat(epic.getStatus()).isEqualTo(TaskStatus.NEW);
    }

    @Test
    void deleteAllEpicsShouldRemoveAllSubtasks() {
        Epic epic = new Epic("Эпик", "Описание");
        manager.addEpic(epic);
        Subtask subtask = new Subtask("Подзадача", "Описание", TaskStatus.NEW, epic.getId());
        manager.addSubtask(subtask);
        manager.deleteAllEpics();
        assertThat(manager.getAllSubtasks()).isEmpty();
    }

    @Test
    void deleteSubtaskByIdShouldRemoveIdFromEpic() {
        Epic epic = new Epic("Эпик", "Описание");
        manager.addEpic(epic);
        Subtask subtask = new Subtask("Подзадача", "Описание", TaskStatus.NEW, epic.getId());
        manager.addSubtask(subtask);
        manager.deleteSubtaskById(subtask.getId());
        assertThat(epic.getSubtaskIds()).isEmpty();
    }

    @Test
    void getTaskByIdShouldAddToHistory() {
        Task task = new Task("Задача", "Описание", TaskStatus.NEW);
        manager.addTask(task);
        manager.getTaskById(task.getId());
        assertThat(manager.getHistory()).hasSize(1);
        assertThat(manager.getHistory().get(0)).isEqualTo(task);
    }

    @Test
    void getEpicByIdShouldAddToHistory() {
        Epic epic = new Epic("Эпик", "Описание");
        manager.addEpic(epic);
        manager.getEpicById(epic.getId());
        assertThat(manager.getHistory()).hasSize(1);
        assertThat(manager.getHistory().get(0)).isEqualTo(epic);
    }

    @Test
    void getSubtaskByIdShouldAddToHistory() {
        Epic epic = new Epic("Эпик", "Описание");
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
}