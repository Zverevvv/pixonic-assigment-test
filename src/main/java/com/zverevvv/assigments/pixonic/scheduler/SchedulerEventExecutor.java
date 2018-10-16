package com.zverevvv.assigments.pixonic.scheduler;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.concurrent.Callable;
import java.util.concurrent.Future;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class SchedulerEventExecutor {
    private final ScheduledThreadPoolExecutor executor;
    private final long eventLiveTimeMillis;
    private static final int DEFAULT_POOL_SIZE = 1;
    private static final int EXECUTE_EXPIRED_LIVE_TIME_VALUE = -1;

    public static SchedulerEventExecutor getOnlyActualEventsExecutor(){
        return new SchedulerEventExecutor(DEFAULT_POOL_SIZE, 0);
    }

    public static SchedulerEventExecutor getAllEventsExecutor(){
        return new SchedulerEventExecutor(DEFAULT_POOL_SIZE, EXECUTE_EXPIRED_LIVE_TIME_VALUE);
    }

    public SchedulerEventExecutor(long eventLiveTimeMillis) {
        this(DEFAULT_POOL_SIZE, eventLiveTimeMillis);
    }

    public SchedulerEventExecutor(int poolSize, long eventLiveTimeMillis) {
        this.executor = new ScheduledThreadPoolExecutor(poolSize);
        this.eventLiveTimeMillis = eventLiveTimeMillis;
    }

    public Future addEvent(LocalDateTime date, Callable callable){
        long diff = Duration.between(LocalDateTime.now(), date).toMillis();
        if (diff > 0){
            return executor.schedule(callable, diff, TimeUnit.MILLISECONDS);
        } else if (eventLiveTimeMillis == EXECUTE_EXPIRED_LIVE_TIME_VALUE || Math.abs(diff) < eventLiveTimeMillis) { // Fire it ASAP
            return executor.submit(callable);
        } else {
            throw new EventTooLateException("Your event couldn't be executed because it's more then " + eventLiveTimeMillis + " milliseconds in past");
        }
    }

    void shutdown(){
        executor.shutdown();
    }
}
