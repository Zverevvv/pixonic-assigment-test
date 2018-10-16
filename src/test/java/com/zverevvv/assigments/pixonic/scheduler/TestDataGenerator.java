package com.zverevvv.assigments.pixonic.scheduler;

import java.time.LocalDateTime;
import java.util.concurrent.Callable;
import java.util.concurrent.Future;

public class TestDataGenerator {
    public static Future<LocalDateTime> addSimpleWithDelay(long delay, SchedulerEventExecutor eventExecutor){
        final LocalDateTime executeAt = LocalDateTime.now().plusSeconds(delay);
        return eventExecutor.addEvent(executeAt, (Callable) () -> {
            LocalDateTime dateTime = LocalDateTime.now();
            System.out.println("Executed at " + dateTime + ". Requested " + executeAt);
            return dateTime;
        });
    }

    public static Future<LocalDateTime> addSleepWithDelay(long sleepMillis, long delay, SchedulerEventExecutor eventExecutor){
        final LocalDateTime executeAt = LocalDateTime.now().plusSeconds(delay);
        return eventExecutor.addEvent(executeAt, (Callable) () -> {
            LocalDateTime start = LocalDateTime.now();
            Thread.sleep(sleepMillis);
            LocalDateTime end = LocalDateTime.now();
            System.out.println("Execution started at " + start + " ended at " + end + ". Requested " + executeAt);
            return end;
        });
    }
}
