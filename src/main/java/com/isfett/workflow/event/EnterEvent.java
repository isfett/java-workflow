package com.isfett.workflow.event;

import com.isfett.workflow.Marking;
import com.isfett.workflow.Transition;
import com.isfett.workflow.WorkflowInterface;

import javax.validation.constraints.NotNull;

public final class EnterEvent extends AbstractWorkflowEvent {
    public EnterEvent(@NotNull Object source, @NotNull Marking marking, Transition transition, WorkflowInterface workflow) {
        super(source, marking, transition, workflow);
    }
}
