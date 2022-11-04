package com.isfett.workflow;

import com.isfett.workflow.event.EventDispatcherInterface;
import com.isfett.workflow.exception.MarkingStoreNotSingleStateForStateMachineException;
import com.isfett.workflow.markingstore.MarkingStoreInterface;

import javax.validation.constraints.NotNull;
import java.util.List;

public class StateMachine extends Workflow {
    public StateMachine(@NotNull Definition definition, @NotNull MarkingStoreInterface markingStore, @NotNull String name, @NotNull EventDispatcherInterface eventDispatcher, List<events> eventsToDispatch) {
        super(definition, markingStore, name, eventDispatcher, eventsToDispatch);

        this.checkMarkingStoreSingleState(markingStore);
    }

    public StateMachine(@NotNull Definition definition, @NotNull MarkingStoreInterface markingStore, @NotNull String name, @NotNull EventDispatcherInterface eventDispatcher) {
        super(definition, markingStore, name, eventDispatcher);

        this.checkMarkingStoreSingleState(markingStore);
    }

    public StateMachine(@NotNull Definition definition, @NotNull MarkingStoreInterface markingStore, @NotNull String name) {
        super(definition, markingStore, name);

        this.checkMarkingStoreSingleState(markingStore);
    }

    public StateMachine(@NotNull Definition definition, @NotNull MarkingStoreInterface markingStore) {
        super(definition, markingStore);

        this.checkMarkingStoreSingleState(markingStore);
    }

    private void checkMarkingStoreSingleState(MarkingStoreInterface markingStore) {
        if (!markingStore.isSingleState()) {
            throw new MarkingStoreNotSingleStateForStateMachineException(this.name);
        }
    }
}
