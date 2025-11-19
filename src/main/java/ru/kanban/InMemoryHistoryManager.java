package ru.kanban;

import java.util.*;

public class InMemoryHistoryManager implements HistoryManager {
    private final List<BaseTask> history = new ArrayList<>();

    @Override
    public void addToHistory(BaseTask task) {
        if (history.size() == 10) {
            history.remove(0);
        }
        history.add(task);
    }

    @Override
    public List<BaseTask> getHistory() {
        return new ArrayList<>(history);
    }
}

