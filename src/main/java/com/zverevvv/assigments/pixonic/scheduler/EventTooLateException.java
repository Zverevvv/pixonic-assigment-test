package com.zverevvv.assigments.pixonic.scheduler;

public class EventTooLateException extends RuntimeException {
    public EventTooLateException(String message) {
        super(message);
    }
}
