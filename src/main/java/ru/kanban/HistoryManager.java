package ru.kanban;

import java.util.*;

public interface HistoryManager {

    void addToHistory(BaseTask task);

    List<BaseTask> getHistory();
}
