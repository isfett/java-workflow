package com.isfett.workflow.exception;

import javax.validation.constraints.NotNull;

public class EventCanNotBeDispatchedException extends RuntimeException {
    public EventCanNotBeDispatchedException(@NotNull String eventName, @NotNull String methodName, @NotNull String className, @NotNull Throwable cause) {
        super(getMessage(eventName, methodName, className), cause);
    }

    public EventCanNotBeDispatchedException(@NotNull String eventName, @NotNull String methodName, @NotNull String className) {
        super(getMessage(eventName, methodName, className));
    }

    private static @NotNull String getMessage(@NotNull String eventName, @NotNull String methodName, @NotNull String className) {
        return "The event " + eventName + " can't be dispatched in " + className + ". The method " + methodName + " does not exist or is not public.";
    }
}
