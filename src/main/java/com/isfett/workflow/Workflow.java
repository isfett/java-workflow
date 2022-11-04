package com.isfett.workflow;

import com.isfett.workflow.event.*;
import com.isfett.workflow.exception.*;
import com.isfett.workflow.markingstore.MarkingStoreInterface;

import javax.validation.constraints.NotNull;
import java.util.*;
import org.hibernate.validator.constraints.NotEmpty;

public class Workflow implements WorkflowInterface {
    public enum events {
        LEAVE,
        TRANSITION,
        ENTER,
        ENTERED,
        COMPLETED,
        ANNOUNCE
    }
    protected final Definition definition;
    protected final MarkingStoreInterface markingStore;
    protected String name = "unnamed";
    protected EventDispatcherInterface eventDispatcher;
    protected List<events> eventsToDispatch = List.of(events.values());

    public Workflow(@NotNull Definition definition, @NotNull MarkingStoreInterface markingStore, @NotNull String name, @NotNull EventDispatcherInterface eventDispatcher, List<events> eventsToDispatch) {
        this.definition = definition;
        this.markingStore = markingStore;
        this.name = name;
        this.eventDispatcher = eventDispatcher;
        this.eventsToDispatch = eventsToDispatch;
    }

    public Workflow(@NotNull Definition definition, @NotNull MarkingStoreInterface markingStore, @NotNull String name, @NotNull EventDispatcherInterface eventDispatcher) {
        this.definition = definition;
        this.markingStore = markingStore;
        this.name = name;
        this.eventDispatcher = eventDispatcher;
    }

    public Workflow(@NotNull Definition definition, @NotNull MarkingStoreInterface markingStore, @NotNull String name) {
        this.definition = definition;
        this.markingStore = markingStore;
        this.name = name;
    }

    public Workflow(@NotNull Definition definition, @NotNull MarkingStoreInterface markingStore) {
        this.definition = definition;
        this.markingStore = markingStore;
    }

    public void addEventListener(@NotNull EventListenerInterface eventListener, @NotEmpty List<String> subscribedEventNames) {
        this.eventDispatcher.addListener(eventListener, subscribedEventNames);
    }

    public @NotNull Marking getMarking(@NotNull Object subject) {
        Marking marking = this.markingStore.getMarking(subject);

        // check if the subject is already in the workflow
        if (marking.getPlaces().isEmpty()) {
            if (this.definition.getInitialPlaces().isEmpty()) {
                throw new MarkingIsEmptyAndNoInitialPlaceFoundException(this.name);
            }

            this.definition.getInitialPlaces().values().forEach(marking::mark);

            this.markingStore.setMarking(subject, marking);

            this.entered(subject, null, marking);
        }

        // check that the subject has a known place
        Map<String, String> definitionPlaces = this.definition.getPlaces();
        if (definitionPlaces.isEmpty()) {
            throw new NoPlacesDefinedException();
        }
        marking.getPlaces().forEach((String markingPlace) -> {
            if (!definitionPlaces.containsKey(markingPlace)) {
                throw new PlaceNotValidForWorkflowException(markingPlace, this.name);
            }
        });

        return marking;
    }

    @Override
    public @NotNull Boolean can(@NotNull Object subject, @NotNull String transitionName) {
        List<Transition> transitions = this.definition.getTransitions();
        Marking marking = this.getMarking(subject);

        for (Transition transition : transitions) {
            if (!transitionName.equals(transition.getName())) {
                continue;
            }

            List<TransitionBlocker> transitionBlockers = this.buildTransitionBlockerListForTransition(subject, marking, transition);

            if (transitionBlockers.isEmpty()) {
                return true;
            }
        }

        return false;
    }

    @Override
    public @NotNull List<TransitionBlocker> buildTransitionBlockerList(@NotNull Object subject, @NotNull String transitionName) {
        List<Transition> transitions = this.definition.getTransitions();
        Marking marking = this.getMarking(subject);
        List<TransitionBlocker> transitionBlockers = new ArrayList<>();

        for (Transition transition : transitions) {
            if (!transition.getName().equals(transitionName)) {
                continue;
            }

            transitionBlockers = this.buildTransitionBlockerListForTransition(subject, marking, transition);

            if (transitionBlockers.isEmpty()) {
                return transitionBlockers;
            }
        }

        if (transitionBlockers.isEmpty()) {
            throw new TransitionUndefinedException(transitionName, this.name);
        }

        return transitionBlockers;
    }

    @Override
    public @NotNull Marking apply(@NotNull Object subject, @NotNull String transitionName) {
        Marking marking = this.getMarking(subject);
        List<Transition> transitions = this.definition.getTransitions();

        boolean transitionExist = false;
        List<Transition> approvedTransitions = new ArrayList<>();

        for (Transition transition : transitions) {
            if (!transitionName.equals(transition.getName())) {
                continue;
            }

            transitionExist = true;

            List<TransitionBlocker> transitionBlockers = this.buildTransitionBlockerListForTransition(subject, marking, transition);

            if (transitionBlockers.isEmpty()) {
                approvedTransitions.add(transition);
            }
        }

        if (!transitionExist) {
            throw new TransitionUndefinedException(transitionName, this.name);
        }

        if (0 == approvedTransitions.size()) {
            throw new TransitionNotEnabledException(transitionName, this.name);
        }

        for (Transition transition : approvedTransitions) {
            this.leave(subject, transition, marking);

            this.transition(subject, transition, marking);

            this.enter(subject, transition, marking);

            this.markingStore.setMarking(subject, marking);

            this.entered(subject, transition, marking);

            this.completed(subject, transition, marking);

            this.announce(subject, transition, marking);
        }

        return marking;
    }

    @Override
    public @NotNull List<Transition> getEnabledTransitions(@NotNull Object subject) {
        List<Transition> enabledTransitions = new ArrayList<>();
        Marking marking = this.getMarking(subject);

        for (Transition transition : this.definition.getTransitions()) {
            List<TransitionBlocker> transitionBlockers = this.buildTransitionBlockerListForTransition(subject, marking, transition);

            if (transitionBlockers.isEmpty()) {
                enabledTransitions.add(transition);
            }
        }

        return enabledTransitions;
    }

    @Override
    public Transition getEnabledTransition(@NotNull Object subject, @NotNull String transitionName) {
        Marking marking = this.getMarking(subject);

        for (Transition transition : this.definition.getTransitions()) {
            if (!transition.getName().equals(transitionName)) {
                continue;
            }

            List<TransitionBlocker> transitionBlockers = this.buildTransitionBlockerListForTransition(subject, marking, transition);

            if (!transitionBlockers.isEmpty()) {
                continue;
            }

            return transition;
        }

        return null;
    }

    @Override
    public @NotNull Definition getDefinition() {
        return definition;
    }

    @Override
    public @NotNull MarkingStoreInterface getMarkingStore() {
        return markingStore;
    }

    @Override
    public @NotNull String getName() {
        return name;
    }

    private @NotNull List<TransitionBlocker> buildTransitionBlockerListForTransition(@NotNull Object subject, @NotNull Marking marking, @NotNull Transition transition) {
        for (String place : transition.getFroms()) {
            if (!marking.has(place)) {
                return List.of(TransitionBlocker.createBlockedByMarking(marking));
            }
        }

        if (null == this.eventDispatcher) {
            return new ArrayList<>();
        }

        GuardEvent guardEvent = this.guardTransition(subject, marking, transition);

        if (guardEvent.isBlocked()) {
            return guardEvent.getTransitionBlockerList();
        }

        return new ArrayList<>();
    }

    private @NotNull GuardEvent guardTransition(@NotNull Object subject, @NotNull Marking marking, @NotNull Transition transition) {
        GuardEvent guardEvent = new GuardEvent(subject, marking, transition, this);

        this.eventDispatcher.dispatch(guardEvent, "workflow.guard");
        this.eventDispatcher.dispatch(guardEvent, "workflow." + this.name + ".guard");
        this.eventDispatcher.dispatch(guardEvent, "workflow." + this.name + ".guard." + transition.getName());

        return guardEvent;
    }

    private void leave(@NotNull Object subject, @NotNull Transition transition, @NotNull Marking marking) {
        List<String> places = transition.getFroms();

        if (this.shouldDispatchEvent(events.LEAVE)) {
            LeaveEvent leaveEvent = new LeaveEvent(subject, marking, transition, this);
            this.eventDispatcher.dispatch(leaveEvent, "workflow.leave");
            this.eventDispatcher.dispatch(leaveEvent, "workflow." + this.name + ".leave");

            for (String place : places) {
                this.eventDispatcher.dispatch(leaveEvent, "workflow." + this.name + ".leave." + place);
            }
        }

        for (String place : places) {
            marking.unmark(place);
        }
    }

    private void transition(@NotNull Object subject, @NotNull Transition transition, @NotNull Marking marking) {
        if (!this.shouldDispatchEvent(events.TRANSITION)) {
            return;
        }

        TransitionEvent transitionEvent = new TransitionEvent(subject, marking, transition, this);
        this.eventDispatcher.dispatch(transitionEvent, "workflow.transition");
        this.eventDispatcher.dispatch(transitionEvent, "workflow." + this.name + ".transition");
        this.eventDispatcher.dispatch(transitionEvent, "workflow." + this.name + ".transition." + transition.getName());
    }

    private void enter(@NotNull Object subject, @NotNull Transition transition, @NotNull Marking marking) {
        List<String> places = transition.getTos();

        if (this.shouldDispatchEvent(events.ENTER)) {
            EnterEvent enterEvent = new EnterEvent(subject, marking, transition, this);
            this.eventDispatcher.dispatch(enterEvent, "workflow.enter");
            this.eventDispatcher.dispatch(enterEvent, "workflow." + this.name + ".enter");

            for (String place : places) {
                this.eventDispatcher.dispatch(enterEvent, "workflow." + this.name + ".enter." + place);
            }
        }

        for (String place : places) {
            marking.mark(place);
        }
    }

    private void entered(@NotNull Object subject, Transition transition, @NotNull Marking marking) {
        if (!this.shouldDispatchEvent(events.ENTERED)) {
            return;
        }

        EnteredEvent enterEvent = new EnteredEvent(subject, marking, transition, this);

        this.eventDispatcher.dispatch(enterEvent, "workflow.entered");
        this.eventDispatcher.dispatch(enterEvent, "workflow." + this.name + ".entered");

        for (String place : marking.getPlaces()) {
            this.eventDispatcher.dispatch(enterEvent, "workflow." + this.name + ".entered." + place);
        }
    }

    private void completed(@NotNull Object subject, @NotNull Transition transition, @NotNull Marking marking) {
        if (!this.shouldDispatchEvent(events.COMPLETED)) {
            return;
        }

        CompletedEvent completedEvent = new CompletedEvent(subject, marking, transition, this);

        this.eventDispatcher.dispatch(completedEvent, "workflow.completed");
        this.eventDispatcher.dispatch(completedEvent, "workflow." + this.name + ".completed");
        this.eventDispatcher.dispatch(completedEvent, "workflow." + this.name + ".completed." + transition.getName());
    }

    private void announce(@NotNull Object subject, @NotNull Transition initialTransition, @NotNull Marking marking) {
        if (!this.shouldDispatchEvent(events.ANNOUNCE)) {
            return;
        }

        AnnounceEvent announceEvent = new AnnounceEvent(subject, marking, initialTransition, this);

        this.eventDispatcher.dispatch(announceEvent, "workflow.announce");
        this.eventDispatcher.dispatch(announceEvent, "workflow." + this.name + ".announce");
        for (Transition transition : this.getEnabledTransitions(subject)) {
            this.eventDispatcher.dispatch(announceEvent, "workflow." + this.name + ".announce." + transition.getName());
        }
    }

    private @NotNull Boolean shouldDispatchEvent(@NotNull events eventName) {
        if (null == this.eventDispatcher) {
            return false;
        }

        if (null == this.eventsToDispatch) {
            return true;
        }

        if (this.eventsToDispatch.isEmpty()) {
            return false;
        }

        return this.eventsToDispatch.contains(eventName);
    }
}
