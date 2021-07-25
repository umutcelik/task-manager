package com.iptiq.taskmanager;

import com.iptiq.taskmanager.enums.Priority;
import com.iptiq.taskmanager.enums.Sort;
import com.iptiq.taskmanager.process.Process;
import com.iptiq.taskmanager.process.TaskManagerProcess;
import java.time.Instant;
import java.util.Collections;
import java.util.Comparator;
import java.util.SortedSet;
import java.util.TreeSet;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Default implementation for {@link TaskManager} with limited capacity.
 * Task manager refuses and throws {@link IllegalStateException} when it's exceed capacity.
 * Stores {@link TaskManagerProcess} with descending time.
 */
public class DefaultTaskManager implements TaskManager {

    private static final Logger LOGGER
        = LoggerFactory.getLogger(DefaultTaskManager.class);

    protected int capacity;
    protected SortedSet<TaskManagerProcess> processes;

    protected static final Comparator<TaskManagerProcess> ID_COMPARATOR = Comparator.comparing(TaskManagerProcess::getId);
    protected static final Comparator<TaskManagerProcess> TIME_COMPARATOR = Comparator.comparing(TaskManagerProcess::getTime);
    protected static final Comparator<TaskManagerProcess> PRIORITY_COMPARATOR =
        Comparator.comparing(TaskManagerProcess::getPriority,
            (p1, p2) -> Integer.compare(p2.getLevel(), p1.getLevel()))
            .thenComparing(TIME_COMPARATOR.reversed());


    public DefaultTaskManager(int capacity) {
        processes = Collections.synchronizedSortedSet(new TreeSet<>(TIME_COMPARATOR.reversed()));
        this.capacity = capacity;
        LOGGER.debug("DefaultTaskManager initialized for capacity:{}", capacity);
    }

    public DefaultTaskManager() {
    }


    /**
     * Adds process to task manager. Refuses and throws {@link IllegalStateException} when it's exceed capacity.
     *
     * @param process to add.
     * @throws IllegalStateException if capacity is exceeded.
     */
    @Override
    public synchronized void add(final Process process) {
        if (processes.size() < capacity) {
            final TaskManagerProcess taskManagerProcess = new TaskManagerProcess(process, Instant.now());
            LOGGER.debug("Adding task manager process:{}", taskManagerProcess);
            processes.add(taskManagerProcess);
        } else {
            throw new IllegalStateException("Queue is out of capacity:" + capacity);
        }
    }

    @Override
    public void kill(final long id) {
        LOGGER.debug("Killing process with id:{}", id);
        processes.removeIf(process -> process.getId() == id);
    }

    @Override
    public void killGroup(final Priority priority) {
        LOGGER.debug("Killing processes with priority:{}", priority);
        processes.removeIf(process -> process.getPriority() == priority);
    }

    @Override
    public void killAll() {
        LOGGER.debug("Killing all processes");
        processes.clear();
    }

    @Override
    public SortedSet<TaskManagerProcess> list(final Sort sort) {
        LOGGER.debug("Listing processes sorted for:{}", sort);
        final SortedSet<TaskManagerProcess> list = Collections.synchronizedSortedSet(new TreeSet<>(getComparator(sort)));
        list.addAll(processes);

        return list;
    }

    private Comparator<TaskManagerProcess> getComparator(final Sort sort) {
        if (Sort.ID == sort) {
            return ID_COMPARATOR;
        } else if (Sort.PRIORITY == sort) {
            return PRIORITY_COMPARATOR;
        } else {
            return TIME_COMPARATOR;
        }
    }
}
