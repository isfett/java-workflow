package com.isfett.workflow.exception;

import javax.validation.constraints.NotNull;

public class MarkingIsEmptyAndNoInitialPlaceFoundException extends RuntimeException {
    public MarkingIsEmptyAndNoInitialPlaceFoundException(@NotNull String workflowName) {
        super(getMessage(workflowName));
    }

    private static @NotNull String getMessage(@NotNull String workflowName) {
        return "The Marking is empty and there is no initial place for workflow " + workflowName + ".";
    }
}
