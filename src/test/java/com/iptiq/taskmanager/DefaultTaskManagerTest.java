package com.iptiq.taskmanager;

import static com.iptiq.taskmanager.enums.Priority.HIGH;
import static com.iptiq.taskmanager.enums.Priority.LOW;
import static com.iptiq.taskmanager.enums.Priority.MEDIUM;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import com.iptiq.taskmanager.enums.Sort;
import com.iptiq.taskmanager.process.Process;
import com.iptiq.taskmanager.process.TaskManagerProcess;
import java.util.ArrayList;
import java.util.List;
import java.util.SortedSet;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import org.hamcrest.collection.IsIterableContainingInOrder;
import org.junit.jupiter.api.Test;

class DefaultTaskManagerTest {

    private TaskManager taskManager;


    @Test
    public void add_hasCapacity_successful() {
        taskManager = new DefaultTaskManager(3);
        taskManager.add(new Process(1, HIGH));
        taskManager.add(new Process(2, HIGH));
        taskManager.add(new Process(3, HIGH));
    }

    @Test
    public void add_overflowCapacity() {
        taskManager = new DefaultTaskManager(3);

        taskManager.add(new Process(1, HIGH));
        taskManager.add(new Process(2, LOW));
        taskManager.add(new Process(3, MEDIUM));


        final IllegalStateException illegalStateException =
            assertThrows(IllegalStateException.class, () -> taskManager.add(new Process(4, MEDIUM)));
        assertEquals("Queue is out of capacity:3", illegalStateException.getMessage());
    }

    @Test
    public void list_sortById_successful() {
        taskManager = new DefaultTaskManager(5);

        taskManager.add(new Process(1, LOW));
        taskManager.add(new Process(3, HIGH));
        taskManager.add(new Process(5, MEDIUM));
        taskManager.add(new Process(2, HIGH));
        taskManager.add(new Process(4, MEDIUM));

        final SortedSet<TaskManagerProcess> list = taskManager.list(Sort.ID);
        assertThat(list.stream().map(TaskManagerProcess::getId).collect(Collectors.toList()),
            IsIterableContainingInOrder.contains(1L, 2L, 3L, 4L, 5L));
    }

    @Test
    public void list_sortByPriority_successful() {
        taskManager = new DefaultTaskManager(5);

        taskManager.add(new Process(1, LOW));
        taskManager.add(new Process(3, HIGH));
        taskManager.add(new Process(5, MEDIUM));
        taskManager.add(new Process(2, HIGH));
        taskManager.add(new Process(4, MEDIUM));

        final SortedSet<TaskManagerProcess> list = taskManager.list(Sort.PRIORITY);
        assertThat(list.stream().map(TaskManagerProcess::getPriority).collect(Collectors.toList()),
            IsIterableContainingInOrder.contains(HIGH, HIGH, MEDIUM, MEDIUM, LOW));
    }

    @Test
    public void list_sortByTime_successful() {
        taskManager = new DefaultTaskManager(5);

        taskManager.add(new Process(1, LOW));
        taskManager.add(new Process(3, HIGH));
        taskManager.add(new Process(5, MEDIUM));
        taskManager.add(new Process(2, HIGH));
        taskManager.add(new Process(4, MEDIUM));

        final SortedSet<TaskManagerProcess> list = taskManager.list(Sort.TIME);
        assertThat(list.stream().map(TaskManagerProcess::getId).collect(Collectors.toList()),
            IsIterableContainingInOrder.contains(1L, 3L, 5L, 2L, 4L));
    }

    @Test
    public void kill_processId_successful() {
        taskManager = new DefaultTaskManager(5);

        taskManager.add(new Process(1, LOW));
        taskManager.add(new Process(2, HIGH));
        taskManager.add(new Process(3, HIGH));
        taskManager.add(new Process(4, MEDIUM));
        taskManager.add(new Process(5, MEDIUM));

        taskManager.kill(3);

        final SortedSet<TaskManagerProcess> list = taskManager.list(Sort.ID);

        assertEquals(4, list.size());
        assertThat(list.stream().map(TaskManagerProcess::getId).collect(Collectors.toList()),
            IsIterableContainingInOrder.contains(1L, 2L, 4L, 5L));
    }

    @Test
    public void kill_group_successful() {
        taskManager = new DefaultTaskManager(5);

        taskManager.add(new Process(1, LOW));
        taskManager.add(new Process(2, HIGH));
        taskManager.add(new Process(3, HIGH));
        taskManager.add(new Process(4, MEDIUM));
        taskManager.add(new Process(5, MEDIUM));

        taskManager.killGroup(HIGH);

        final SortedSet<TaskManagerProcess> list = taskManager.list(Sort.ID);

        assertEquals(3, list.size());
        assertThat(list.stream().map(TaskManagerProcess::getId).collect(Collectors.toList()),
            IsIterableContainingInOrder.contains(1L, 4L, 5L));
    }

    @Test
    public void kill_all_successful() {
        taskManager = new DefaultTaskManager(5);

        taskManager.add(new Process(1, LOW));
        taskManager.add(new Process(2, HIGH));
        taskManager.add(new Process(3, HIGH));
        taskManager.add(new Process(4, MEDIUM));
        taskManager.add(new Process(5, MEDIUM));

        taskManager.killAll();

        final SortedSet<TaskManagerProcess> list = taskManager.list(Sort.ID);

        assertEquals(0, list.size());

    }

    @Test
    public void add_extendCapacityConcurrent_removeLast() throws InterruptedException {

        taskManager = new DefaultTaskManager(3);

        List<Runnable> runnables = new ArrayList<>();

        ExecutorService executor = Executors.newFixedThreadPool(4);

        IntStream.rangeClosed(1, 8).forEach(i -> {
            final Runnable runnable = () -> taskManager.add(new Process(i, LOW));
            runnables.add(runnable);
        });

        IntStream.rangeClosed(1, 8).forEach(i -> {
            final Runnable runnable = () -> taskManager.kill(i);
            runnables.add(runnable);
        });

        IntStream.rangeClosed(1, 4).forEach(i -> {
            final Runnable runnable = () -> taskManager.list(Sort.PRIORITY);
            runnables.add(runnable);
        });

        IntStream.rangeClosed(10, 20).forEach(i -> {
            final Runnable runnable = () -> taskManager.add(new Process(i, LOW));
            runnables.add(runnable);
        });

        runnables.forEach(executor::execute);


        Thread.sleep(1000);

        final SortedSet<TaskManagerProcess> list = taskManager.list(Sort.TIME);

        assertEquals(3, list.size());
    }

}