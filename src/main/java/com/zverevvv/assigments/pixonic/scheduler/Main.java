package com.zverevvv.assigments.pixonic.scheduler;

import java.time.LocalDateTime;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

public class Main {
    public static void main(String[] args) throws ExecutionException, InterruptedException {
        System.out.println(LocalDateTime.now());
        SchedulerEventExecutor eventExecutor = SchedulerEventExecutor.getAllEventsExecutor();
        Future first = eventExecutor.addEvent(LocalDateTime.now().plusSeconds(20), new Callable() {
            @Override
            public Object call() throws Exception {
                System.out.println(LocalDateTime.now() + " +20 seconds");
                return true;
            }
        });

        Future second = eventExecutor.addEvent(LocalDateTime.now().plusSeconds(10), new Callable() {
            @Override
            public Object call() throws Exception {
                System.out.println(LocalDateTime.now() + " +10 seconds");
                return true;
            }
        });

        Future third = eventExecutor.addEvent(LocalDateTime.now().plusSeconds(10), new Callable() {
            @Override
            public Object call() throws Exception {
                System.out.println(LocalDateTime.now() + " +10 seconds2");
                return true;
            }
        });

        Future forth = eventExecutor.addEvent(LocalDateTime.now().plusSeconds(30), new Callable() {
            @Override
            public Object call() throws Exception {
                System.out.println(LocalDateTime.now() + " +30 seconds");
                return true;
            }
        });

        first.get();
        second.get();
        third.get();
        forth.get();

        eventExecutor.shutdown();
    }
}
