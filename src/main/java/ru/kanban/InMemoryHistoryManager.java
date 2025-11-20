package ru.kanban;

import java.util.*;

public class InMemoryHistoryManager implements HistoryManager {
    private static final int MAX_HISTORY_SIZE = 10;
    private static final int FIRST_ELEMENT_INDEX = 0;
    private final List<BaseTask> history = new ArrayList<>();

    @Override
    public void addToHistory(BaseTask task) {
        if (history.size() == MAX_HISTORY_SIZE) {
            history.remove(FIRST_ELEMENT_INDEX);
        }
        history.add(task);
    }

    @Override
    public List<BaseTask> getHistory() {
        return new ArrayList<>(history);
    }
}

