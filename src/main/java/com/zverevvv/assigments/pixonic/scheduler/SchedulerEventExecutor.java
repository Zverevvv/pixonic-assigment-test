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

    /**
     * Returns {@code SchedulerEventExecutor} which will throw {@link EventTooLateException}
     * for any expired event (it's desired start time is in past)
     */
    public static SchedulerEventExecutor getOnlyActualEventsExecutor(){
        return new SchedulerEventExecutor(DEFAULT_POOL_SIZE, 0);
    }

    /**
     * Returns {@code SchedulerEventExecutor} which will execute any Event
     */
    public static SchedulerEventExecutor getAllEventsExecutor(){
        return new SchedulerEventExecutor(DEFAULT_POOL_SIZE, EXECUTE_EXPIRED_LIVE_TIME_VALUE);
    }

    /**
     * Returns {@code SchedulerEventExecutor} which will throw {@link EventTooLateException}
     * if event desired star time is greater than {@code eventLiveTimeMillis}
     * @param eventLiveTimeMillis time in milliseconds to consider Event expired
     */
    public SchedulerEventExecutor(long eventLiveTimeMillis) {
        this(DEFAULT_POOL_SIZE, eventLiveTimeMillis);
    }

    /**
     * Returns {@code SchedulerEventExecutor} with given {@code poolSize} which will throw {@link EventTooLateException}
     * if event desired star time is greater than {@code eventLiveTimeMillis}
     * @param poolSize how many threads will be used by executor
     * @param eventLiveTimeMillis time in milliseconds to consider Event expired
     */
    public SchedulerEventExecutor(int poolSize, long eventLiveTimeMillis) {
        this.executor = new ScheduledThreadPoolExecutor(poolSize);
        this.eventLiveTimeMillis = eventLiveTimeMillis;
    }

    /**
     * Schedules event for execution. Will execute {@code callable} at {@code date}
     * @throws EventTooLateException if {@code eventLiveTimeMillis} is set and diff between {@code date} and
     * current time is greater than it
     * @param date to execute event
     * @param callable Callable to execute
     * @return Future for this event
     */
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

    /**
     * Shutdowns executor
     */
    void shutdown(){
        executor.shutdown();
    }
}
