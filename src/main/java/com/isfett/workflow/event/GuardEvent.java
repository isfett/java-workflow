package com.isfett.workflow.event;

import com.isfett.workflow.Marking;
import com.isfett.workflow.Transition;
import com.isfett.workflow.TransitionBlocker;
import com.isfett.workflow.WorkflowInterface;

import javax.validation.constraints.NotNull;
import java.util.ArrayList;
import java.util.List;

public final class GuardEvent extends AbstractWorkflowEvent {
    private final List<TransitionBlocker> transitionBlockerList = new ArrayList<>();

    public GuardEvent(@NotNull Object source, @NotNull Marking marking, Transition transition, WorkflowInterface workflow) {
        super(source, marking, transition, workflow);
    }

    public Boolean isBlocked() {
        return !this.transitionBlockerList.isEmpty();
    }

    public void setBlocked(@NotNull Boolean blocked, String message) {
        this.checkClearTransitionBlockerList(blocked);

        if (!blocked) {
            return;
        }

        this.transitionBlockerList.add(TransitionBlocker.createUnknown(message));
    }

    public void setBlocked(@NotNull Boolean blocked) {
        this.checkClearTransitionBlockerList(blocked);

        if (!blocked) {
            return;
        }

        this.transitionBlockerList.add(TransitionBlocker.createUnknown());
    }

    public List<TransitionBlocker> getTransitionBlockerList() {
        return transitionBlockerList;
    }

    private void checkClearTransitionBlockerList(@NotNull Boolean blocked) {
        if (!blocked) {
            this.transitionBlockerList.clear();
        }
    }
}
