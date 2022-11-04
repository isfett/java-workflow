package com.isfett.workflow.event;

import com.isfett.workflow.Marking;
import com.isfett.workflow.Transition;
import com.isfett.workflow.WorkflowInterface;

import javax.validation.constraints.NotNull;
import java.util.EventObject;

public abstract class AbstractWorkflowEvent extends EventObject {
    private final Marking marking;
    private final Transition transition;
    private final WorkflowInterface workflow;

    public AbstractWorkflowEvent(@NotNull Object source, @NotNull Marking marking, Transition transition, WorkflowInterface workflow) {
        super(source);
        this.marking = marking;
        this.transition = transition;
        this.workflow = workflow;
    }

    public @NotNull Marking getMarking() {
        return this.marking;
    }

    public Transition getTransition() {
        return this.transition;
    }

    public WorkflowInterface getWorkflow() {
        return this.workflow;
    }
}
