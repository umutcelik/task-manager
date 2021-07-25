package com.iptiq.taskmanager;

import com.iptiq.taskmanager.enums.Priority;
import com.iptiq.taskmanager.enums.Sort;
import com.iptiq.taskmanager.process.Process;
import com.iptiq.taskmanager.process.TaskManagerProcess;
import java.util.SortedSet;

public interface TaskManager {

    /**
     * adds process to task manager
     *
     * @param process to add.
     */
    void add(Process process);

    /**
     * Kills the process with id.
     *
     * @param id of the process to kill.
     */
    void kill(long id);

    /**
     * Kills all the process with the priority.
     *
     * @param priority of the processes to kill.
     */
    void killGroup(Priority priority);

    /**
     * Kills all the processes.
     */
    void killAll();

    /**
     * Lists all the process running.
     *
     * @param sort sort type.
     * @return list of process.
     * @see Sort
     */
    SortedSet<TaskManagerProcess> list(Sort sort);
}
