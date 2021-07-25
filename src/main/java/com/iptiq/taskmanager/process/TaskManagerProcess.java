package com.iptiq.taskmanager.process;

import com.iptiq.taskmanager.enums.Priority;
import java.time.Instant;

/**
 * Wrapper for {@link Process} with time.
 */
public class TaskManagerProcess {
    private final Process process;
    private final Instant time;

    public TaskManagerProcess(Process process, Instant time) {
        this.process = process;
        this.time = time;
    }

    public Instant getTime() {
        return time;
    }

    public long getId() {
        return process.getId();
    }

    public Priority getPriority() {
        return process.getPriority();
    }

    @Override
    public String toString() {
        return "[id:" + getId() + " priority:" + getPriority() + " time:" + time + "]";
    }

}
