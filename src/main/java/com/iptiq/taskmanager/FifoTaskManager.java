package com.iptiq.taskmanager;

import com.iptiq.taskmanager.process.Process;
import com.iptiq.taskmanager.process.TaskManagerProcess;
import java.time.Instant;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Stores {@link TaskManagerProcess} with descending time order and works like fifo queue.
 * Removes first process from the queue when capacity is exceeded.
 */
public class FifoTaskManager extends DefaultTaskManager {

    private static final Logger LOGGER
        = LoggerFactory.getLogger(FifoTaskManager.class);

    public FifoTaskManager(final int capacity) {
        super(capacity);
    }

    /**
     * Adds {@link TaskManagerProcess}  to task manager.
     * Removes first process from the queue when capacity is exceeded.
     *
     * @param process to add. Removes the first one if capacity is exceeded.
     */
    @Override
    public synchronized void add(final Process process) {
        LOGGER.debug("Adding process:{}", process);
        if (processes.size() >= capacity) {
            LOGGER.debug("Process capacity exceed, removing last one");
            processes.remove(processes.last());
        }
        processes.add(new TaskManagerProcess(process, Instant.now()));
    }
}
