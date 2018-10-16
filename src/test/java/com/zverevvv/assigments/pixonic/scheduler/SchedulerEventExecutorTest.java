package com.zverevvv.assigments.pixonic.scheduler;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

import static org.junit.jupiter.api.Assertions.*;

class SchedulerEventExecutorTest {

    private final long eventLiveTimeMillis = 9*1000*1000; // 9 sec
    private SchedulerEventExecutor executor;

    @BeforeEach
    void init(){
        executor = new SchedulerEventExecutor(eventLiveTimeMillis);
        System.out.println(LocalDateTime.now());
    }

    @Test
    void orderedTest() throws ExecutionException, InterruptedException {
        Future<LocalDateTime> firstFuture = TestDataGenerator.addSimpleWithDelay(5, executor);
        Future<LocalDateTime> secondFuture = TestDataGenerator.addSimpleWithDelay(10, executor);
        Future<LocalDateTime> thirdFuture = TestDataGenerator.addSimpleWithDelay(15, executor);

        LocalDateTime firstTaskTime = firstFuture.get();
        LocalDateTime secondTaskTime = secondFuture.get();
        LocalDateTime thirdTaskTime = thirdFuture.get();

        assertTrue(firstTaskTime.isBefore(secondTaskTime));
        assertTrue(secondTaskTime.isBefore(thirdTaskTime));
    }

    @Test
    void unorderedTest() throws ExecutionException, InterruptedException {
        Future<LocalDateTime> secondFuture = TestDataGenerator.addSimpleWithDelay(10, executor);
        Future<LocalDateTime> thirdFuture = TestDataGenerator.addSimpleWithDelay(15, executor);
        Future<LocalDateTime> firstFuture = TestDataGenerator.addSimpleWithDelay(5, executor);

        LocalDateTime firstTaskTime = firstFuture.get();
        LocalDateTime secondTaskTime = secondFuture.get();
        LocalDateTime thirdTaskTime = thirdFuture.get();

        assertTrue(firstTaskTime.isBefore(secondTaskTime));
        assertTrue(secondTaskTime.isBefore(thirdTaskTime));
    }

    @Test
    void duplicatesTest() throws ExecutionException, InterruptedException {
        Future<LocalDateTime> firstFuture = TestDataGenerator.addSleepWithDelay(2000,5, executor);
        Future<LocalDateTime> secondFuture = TestDataGenerator.addSleepWithDelay(1000,5, executor);
        Future<LocalDateTime> thirdFuture = TestDataGenerator.addSimpleWithDelay(10, executor);

        LocalDateTime firstTaskTime = firstFuture.get();
        LocalDateTime secondTaskTime = secondFuture.get();
        LocalDateTime thirdTaskTime = thirdFuture.get();

        assertTrue(firstTaskTime.isBefore(secondTaskTime));
        assertTrue(secondTaskTime.isBefore(thirdTaskTime));
    }

    @Test
    void backlogTest() throws ExecutionException, InterruptedException {
        Future<LocalDateTime> firstFuture = TestDataGenerator.addSleepWithDelay(10000, 5, executor);
        Future<LocalDateTime> secondFuture = TestDataGenerator.addSleepWithDelay(5000,10, executor);
        Future<LocalDateTime> thirdFuture = TestDataGenerator.addSleepWithDelay(1000,10, executor);

        LocalDateTime firstTaskTime = firstFuture.get();
        LocalDateTime secondTaskTime = secondFuture.get();
        LocalDateTime thirdTaskTime = thirdFuture.get();

        assertTrue(firstTaskTime.isBefore(secondTaskTime));
        assertTrue(secondTaskTime.isBefore(thirdTaskTime));
    }

    @Test
    void nearlyExpiredASAPFireTest() throws ExecutionException, InterruptedException {
        Future<LocalDateTime> firstFuture = TestDataGenerator.addSleepWithDelay(10000,5, executor);
        Thread.currentThread().sleep(6000);
        Future<LocalDateTime> secondFuture = TestDataGenerator.addSleepWithDelay(5000,-7, executor);
        Future<LocalDateTime> thirdFuture = TestDataGenerator.addSleepWithDelay(1000,10, executor);

        LocalDateTime firstTaskTime = firstFuture.get();
        LocalDateTime secondTaskTime = secondFuture.get();
        LocalDateTime thirdTaskTime = thirdFuture.get();

        assertTrue(firstTaskTime.isBefore(secondTaskTime));
        assertTrue(secondTaskTime.isBefore(thirdTaskTime));
    }

    @Test
    void nearlyExpiredFirstTest() throws ExecutionException, InterruptedException {
        Future<LocalDateTime> firstFuture = TestDataGenerator.addSimpleWithDelay(-5, executor);
        Future<LocalDateTime> secondFuture = TestDataGenerator.addSimpleWithDelay(5, executor);
        Future<LocalDateTime> thirdFuture = TestDataGenerator.addSimpleWithDelay(10, executor);

        LocalDateTime firstTaskTime = firstFuture.get();
        LocalDateTime secondTaskTime = secondFuture.get();
        LocalDateTime thirdTaskTime = thirdFuture.get();

        assertTrue(firstTaskTime.isBefore(secondTaskTime));
        assertTrue(secondTaskTime.isBefore(thirdTaskTime));
    }

    @Test()
    void expiredAddEvent(){
        Assertions.assertThrows(EventTooLateException.class, () -> {
            TestDataGenerator.addSimpleWithDelay(-(eventLiveTimeMillis +1), executor);
        });
    }

    @AfterEach
    void tearDown(){
        executor.shutdown();
    }
}