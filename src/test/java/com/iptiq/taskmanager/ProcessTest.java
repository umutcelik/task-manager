package com.iptiq.taskmanager;

import static org.junit.jupiter.api.Assertions.assertEquals;
import com.iptiq.taskmanager.enums.Priority;
import com.iptiq.taskmanager.process.Process;
import org.junit.jupiter.api.Test;

class ProcessTest {

    public static final String UUID_STRING = "38400000-8cf0-11bd-b23e-10b96e4ef00d";

    @Test
    public void testImmutable() {
        long id = 99;
        Priority priority = Priority.HIGH;
        Process process = new Process(id, priority);

        id = 42;
        priority = Priority.LOW;

        assertEquals(99, process.getId());
        assertEquals(Priority.HIGH, process.getPriority());
    }
}