package com.isfett.workflow.exception;

import javax.validation.constraints.NotNull;

public class MarkingStoreNotSingleStateForStateMachineException extends IllegalArgumentException {
    public MarkingStoreNotSingleStateForStateMachineException(@NotNull String stateMachineName) {
        super(getMessage(stateMachineName));
    }

    private static @NotNull String getMessage(@NotNull String stateMachineName) {
        return "The StateMachine " + stateMachineName + " has a marking store that is not configured as a single state marking store.";
    }
}
