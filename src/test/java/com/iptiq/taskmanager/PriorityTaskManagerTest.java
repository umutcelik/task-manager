package com.iptiq.taskmanager;

import static com.iptiq.taskmanager.enums.Priority.HIGH;
import static com.iptiq.taskmanager.enums.Priority.LOW;
import static com.iptiq.taskmanager.enums.Priority.MEDIUM;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.jupiter.api.Assertions.*;
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

class PriorityTaskManagerTest {

    private PriorityTaskManager taskManager;

    @Test
    public void add_extendCapacity_removeLast() {

        taskManager = new PriorityTaskManager(5);

        taskManager.add(new Process(1, LOW));
        taskManager.add(new Process(2, MEDIUM));
        taskManager.add(new Process(3, HIGH));
        taskManager.add(new Process(4, MEDIUM));
        taskManager.add(new Process(5, HIGH));
        taskManager.add(new Process(6, HIGH));
        taskManager.add(new Process(7, HIGH));
        taskManager.add(new Process(8, MEDIUM));
        final SortedSet<TaskManagerProcess> list = taskManager.list(Sort.PRIORITY);

        assertThat(list.stream().map(TaskManagerProcess::getId).collect(Collectors.toList()),
            IsIterableContainingInOrder.contains(7L, 6L, 5L, 3L, 4L));
    }

    @Test
    public void add_extendCapacityConcurrent_removeLast() throws InterruptedException {

        taskManager = new PriorityTaskManager(4);

        List<Runnable> runnables = new ArrayList<>();

        ExecutorService executor = Executors.newFixedThreadPool(4);

        IntStream.rangeClosed(1, 8).forEach(i -> {
            final Runnable runnable = () -> taskManager.add(new Process(i, LOW));
            runnables.add(runnable);
        });


        IntStream.rangeClosed(10, 30).forEach(i -> {
            final Runnable runnable = () -> taskManager.add(new Process(i, MEDIUM));
            runnables.add(runnable);
        });

        IntStream.rangeClosed(1, 4).forEach(i -> {
            final Runnable runnable = () -> {
                final SortedSet<TaskManagerProcess> list = taskManager.list(Sort.ID);
                System.out.println("List result:"+list);
            };
            runnables.add(runnable);
        });



        runnables.forEach(executor::execute);

        Thread.sleep(1000);

        final SortedSet<TaskManagerProcess> list = taskManager.list(Sort.PRIORITY);
        System.out.println("list:"+list);
        assertEquals(4, list.size());
    }

}