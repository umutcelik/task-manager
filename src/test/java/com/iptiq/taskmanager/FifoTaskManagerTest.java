package com.iptiq.taskmanager;

import static com.iptiq.taskmanager.enums.Priority.HIGH;
import static com.iptiq.taskmanager.enums.Priority.LOW;
import static com.iptiq.taskmanager.enums.Priority.MEDIUM;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
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

class FifoTaskManagerTest {

    private FifoTaskManager taskManager;


    @Test
    public void add_extendCapacity_removeLast() {

        taskManager = new FifoTaskManager(3);

        taskManager.add(new Process(1, LOW));
        taskManager.add(new Process(2, HIGH));
        taskManager.add(new Process(3, HIGH));
        taskManager.add(new Process(4, MEDIUM));
        taskManager.add(new Process(5, MEDIUM));

        final SortedSet<TaskManagerProcess> list = taskManager.list(Sort.TIME);

        assertThat(list.stream().map(TaskManagerProcess::getId).collect(Collectors.toList()),
            IsIterableContainingInOrder.contains(3l, 4l, 5l));
    }


    @Test
    public void add_extendCapacityConcurrent_removeLast() throws InterruptedException {

        taskManager = new FifoTaskManager(4);

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
            final Runnable runnable = () -> taskManager.add(new Process(i, MEDIUM));
            runnables.add(runnable);
        });
        runnables.forEach(executor::execute);

        Thread.sleep(1000);

        final SortedSet<TaskManagerProcess> list = taskManager.list(Sort.TIME);
        assertEquals(4, list.size());
    }

}