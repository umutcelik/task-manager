package com.iptiq.taskmanager.process;

import com.iptiq.taskmanager.enums.Priority;
import java.util.Objects;

/**
 * Immutable object to handle process information.
 * <code>id</code> is unique identifier
 */
public final class Process {

    private final long id;

    private final Priority priority;


    public Process(long id, Priority priority) {
        this.id = id;
        this.priority = priority;
    }

    public long getId() {
        return id;
    }

    public Priority getPriority() {
        return priority;
    }

    @Override
    public String toString() {
        return "id:" + id + " priority:" + priority;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        Process process = (Process) o;
        return id == process.id;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
