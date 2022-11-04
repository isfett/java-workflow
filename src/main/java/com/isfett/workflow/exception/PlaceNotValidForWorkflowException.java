package com.isfett.workflow.exception;

import javax.validation.constraints.NotNull;

public class PlaceNotValidForWorkflowException extends RuntimeException {
    public PlaceNotValidForWorkflowException(@NotNull String place, @NotNull String workflowName) {
        super(getMessage(place, workflowName));
    }

    private static @NotNull String getMessage(@NotNull String place, @NotNull String workflowName) {
        return "Place " + place + " is not valid for workflow " + workflowName + ".";
    }
}
