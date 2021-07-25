package com.iptiq.taskmanager;

import com.iptiq.taskmanager.process.Process;
import com.iptiq.taskmanager.process.TaskManagerProcess;
import java.time.Instant;
import java.util.Collections;
import java.util.TreeSet;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Priority based task manager.
 * Stores the {@link TaskManagerProcess} with descending priority and time.
 */
public class PriorityTaskManager extends DefaultTaskManager {

    private static final Logger LOGGER
        = LoggerFactory.getLogger(PriorityTaskManager.class);

    public PriorityTaskManager(final int capacity) {
        processes = Collections
            .synchronizedSortedSet(new TreeSet<>(PRIORITY_COMPARATOR));
        this.capacity = capacity;
        LOGGER.debug("PriorityTaskManager initialized for capacity:{}", capacity);
    }

    /**
     * Adds {@link TaskManagerProcess}. When capacity is exceeded checks the task,
     * if there is a task with lower priority removes the first added one. If there is not just ignores.
     *
     * @param process to add.
     */
    @Override
    public synchronized void add(final Process process) {
        LOGGER.debug("Adding process:{}", process);
        if (processes.size() >= capacity) {
            LOGGER.debug("Process capacity exceed");
            if (processes.last().getPriority().getLevel() < process.getPriority().getLevel()) {
                LOGGER.debug("Process {} will be removed", processes.last());
                processes.remove(processes.last());
                processes.add(new TaskManagerProcess(process, Instant.now()));
            } else {
                LOGGER.info("There is no process with lower priority. Process will be skipped");
            }
        } else {
            processes.add(new TaskManagerProcess(process, Instant.now()));
        }
    }
}
