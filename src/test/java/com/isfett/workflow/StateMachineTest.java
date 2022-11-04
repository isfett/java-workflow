package com.isfett.workflow;

import com.isfett.workflow.WorkflowTestHelper.CheckPropertyEventListener;
import com.isfett.workflow.WorkflowTestHelper.SingleStateMethodSubject;
import com.isfett.workflow.WorkflowTestHelper.TestEventListener;
import com.isfett.workflow.event.EventDispatcher;
import com.isfett.workflow.exception.MarkingStoreNotSingleStateForStateMachineException;
import com.isfett.workflow.markingstore.MarkingStoreInterface;
import com.isfett.workflow.markingstore.MethodMarkingStore;
import com.isfett.workflow.markingstore.PropertyMarkingStore;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class StateMachineTest {
    @Test
    void testCan() {
        Definition definition = WorkflowTestHelper.createComplexStateMachineDefinition();
        MarkingStoreInterface markingStore = new MethodMarkingStore(true, "status");

        StateMachine stateMachine = new StateMachine(definition, markingStore);

        SingleStateMethodSubject subject = new SingleStateMethodSubject();

        // If you are in place "a" you should be able to apply "t1"
        subject.setStatus("a");
        assertTrue(stateMachine.can(subject, "t1"));
        subject.setStatus("d");
        assertTrue(stateMachine.can(subject, "t1"));

        subject.setStatus("b");
        assertFalse(stateMachine.can(subject, "t1"));
    }

    @Test
    void testCanWithMultipleTransition() {
        Definition definition = WorkflowTestHelper.createComplexStateMachineDefinition();
        MarkingStoreInterface markingStore = new MethodMarkingStore(true, "status");

        StateMachine stateMachine = new StateMachine(definition, markingStore);

        SingleStateMethodSubject subject = new SingleStateMethodSubject();

        // If you are in place "b" you should be able to apply "t1" and "t2"
        subject.setStatus("b");
        assertTrue(stateMachine.can(subject, "t2"));
        assertTrue(stateMachine.can(subject, "t3"));

        assertFalse(stateMachine.can(subject, "t1"));
    }

    @Test
    void testBuildTransitionBlockerList() {
        Definition definition = WorkflowTestHelper.createComplexStateMachineDefinition();
        MarkingStoreInterface markingStore = new MethodMarkingStore(true, "status");

        StateMachine stateMachine = new StateMachine(definition, markingStore);

        SingleStateMethodSubject subject = new SingleStateMethodSubject();

        subject.setStatus("a");
        assertTrue(stateMachine.buildTransitionBlockerList(subject, "t1").isEmpty());
        subject.setStatus("d");
        assertTrue(stateMachine.buildTransitionBlockerList(subject, "t1").isEmpty());

        subject.setStatus("b");
        assertFalse(stateMachine.buildTransitionBlockerList(subject, "t1").isEmpty());
    }

    @Test
    void testBuildTransitionBlockerListWithMultipleTransitions() {
        Definition definition = WorkflowTestHelper.createComplexStateMachineDefinition();
        MarkingStoreInterface markingStore = new MethodMarkingStore(true, "status");

        StateMachine stateMachine = new StateMachine(definition, markingStore, "statemachine_name");

        SingleStateMethodSubject subject = new SingleStateMethodSubject();

        subject.setStatus("b");
        assertTrue(stateMachine.buildTransitionBlockerList(subject, "t2").isEmpty());
        assertTrue(stateMachine.buildTransitionBlockerList(subject, "t3").isEmpty());

        subject.setStatus("b");
        assertFalse(stateMachine.buildTransitionBlockerList(subject, "t1").isEmpty());
    }

    @Test
    void testConstructorWithMultiStateMarkingStoreShouldThrowException() {
        Throwable exception = assertThrows(MarkingStoreNotSingleStateForStateMachineException.class, () -> {
            new StateMachine(WorkflowTestHelper.createComplexStateMachineDefinition(), new PropertyMarkingStore(false, "status"));
        });
        assertEquals("The StateMachine unnamed has a marking store that is not configured as a single state marking store.", exception.getMessage());
    }

    @Test
    void testApplyWithEventDispatcher() {
        SingleStateMethodSubject subject = new SingleStateMethodSubject();
        subject.setStatus("a");
        EventDispatcher eventDispatcher = new EventDispatcher();
        StateMachine stateMachine = new StateMachine(WorkflowTestHelper.createComplexStateMachineDefinition(), new MethodMarkingStore(true, "status"), "statemachine_name", eventDispatcher);
        TestEventListener eventListener = new TestEventListener();

        stateMachine.addEventListener(eventListener, List.of());

        List<String> expectedEventNames = List.of(
            "workflow.guard",
            "workflow.statemachine_name.guard",
            "workflow.statemachine_name.guard.t1",
            "workflow.leave",
            "workflow.statemachine_name.leave",
            "workflow.statemachine_name.leave.a",
            "workflow.transition",
            "workflow.statemachine_name.transition",
            "workflow.statemachine_name.transition.t1",
            "workflow.enter",
            "workflow.statemachine_name.enter",
            "workflow.statemachine_name.enter.b",
            "workflow.entered",
            "workflow.statemachine_name.entered",
            "workflow.statemachine_name.entered.b",
            "workflow.completed",
            "workflow.statemachine_name.completed",
            "workflow.statemachine_name.completed.t1",
            "workflow.announce",
            "workflow.statemachine_name.announce",
            "workflow.guard",
            "workflow.statemachine_name.guard",
            "workflow.statemachine_name.guard.t2",
            "workflow.guard",
            "workflow.statemachine_name.guard",
            "workflow.statemachine_name.guard.t3",
            "workflow.statemachine_name.announce.t2",
            "workflow.statemachine_name.announce.t3"
        );

        stateMachine.apply(subject, "t1");

        assertEquals(expectedEventNames.size(), eventListener.getFiredEvents().size());
        assertEquals(expectedEventNames, eventListener.getFiredEvents());
    }

    @Test
    void testApplyWithEventDispatcherCheckEvents() {
        SingleStateMethodSubject subject = new SingleStateMethodSubject();
        subject.setStatus("a");
        EventDispatcher eventDispatcher = new EventDispatcher();
        StateMachine stateMachine = new StateMachine(WorkflowTestHelper.createComplexStateMachineDefinition(), new MethodMarkingStore(true, "status"), "statemachine_name", eventDispatcher);
        CheckPropertyEventListener eventListener = new CheckPropertyEventListener();

        stateMachine.addEventListener(eventListener, List.of("workflow.statemachine_name.leave.a"));

        stateMachine.apply(subject, "t1");

        List<String> expectedProperties = List.of(
            "statemachine_name",
            "[a]",
            "t1"
        );
        assertEquals(expectedProperties.size(), eventListener.getProperties().size());
        assertEquals(expectedProperties, eventListener.getProperties());
    }

    @Test
    void testApplyWithEventDispatcherAndCustomEventsToBeDispatched() {
        SingleStateMethodSubject subject = new SingleStateMethodSubject();
        subject.setStatus("a");
        EventDispatcher eventDispatcher = new EventDispatcher();
        StateMachine stateMachine = new StateMachine(WorkflowTestHelper.createComplexStateMachineDefinition(), new MethodMarkingStore(true, "status"), "statemachine_name", eventDispatcher, List.of(Workflow.events.ENTER, Workflow.events.LEAVE));
        TestEventListener eventListener = new TestEventListener();

        stateMachine.addEventListener(eventListener, List.of());

        List<String> expectedEventNames = List.of(
            "workflow.guard",
            "workflow.statemachine_name.guard",
            "workflow.statemachine_name.guard.t1",
            "workflow.leave",
            "workflow.statemachine_name.leave",
            "workflow.statemachine_name.leave.a",
            "workflow.enter",
            "workflow.statemachine_name.enter",
            "workflow.statemachine_name.enter.b"
        );

        stateMachine.apply(subject, "t1");
        assertEquals(expectedEventNames.size(), eventListener.getFiredEvents().size());
        assertEquals(expectedEventNames, eventListener.getFiredEvents());
    }
}