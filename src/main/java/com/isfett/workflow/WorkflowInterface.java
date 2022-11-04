package com.isfett.workflow;

import com.isfett.workflow.event.EventListenerInterface;
import com.isfett.workflow.markingstore.MarkingStoreInterface;

import javax.validation.constraints.NotNull;
import java.util.List;
import org.hibernate.validator.constraints.NotEmpty;

public interface WorkflowInterface {
    void addEventListener(@NotNull EventListenerInterface eventListener, @NotEmpty List<String> subscribedEventNames);

    @NotNull Marking getMarking(@NotNull Object subject);

    @NotNull Boolean can(@NotNull Object subject, @NotNull String transitionName);

    @NotNull List<TransitionBlocker> buildTransitionBlockerList(@NotNull Object subject, @NotNull String transitionName);

    @NotNull Marking apply(@NotNull Object subject, @NotNull String transitionName);

    @NotNull List<Transition> getEnabledTransitions(@NotNull Object subject);

    Transition getEnabledTransition(@NotNull Object subject, @NotNull String transitionName);

    @NotNull String getName();

    @NotNull Definition getDefinition();

    @NotNull MarkingStoreInterface getMarkingStore();
}
