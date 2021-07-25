package com.iptiq.taskmanager.enums;

/**
 * Enums for priority. Higher level means higher priority
 */
public enum Priority {
    LOW(10), MEDIUM(20), HIGH(30);

    private final int level;

    Priority(final int level) {
        this.level = level;
    }

    public int getLevel() {
        return level;
    }
}
