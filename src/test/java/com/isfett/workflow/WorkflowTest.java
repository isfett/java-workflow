package com.isfett.workflow;

import com.isfett.workflow.WorkflowTestHelper.CheckPropertyEventListener;
import com.isfett.workflow.WorkflowTestHelper.GuardBlockEventListener;
import com.isfett.workflow.WorkflowTestHelper.GuardBlockUnblockEventListener;
import com.isfett.workflow.WorkflowTestHelper.MethodNotPublicEventListener;
import com.isfett.workflow.WorkflowTestHelper.MultiStateMethodSubject;
import com.isfett.workflow.WorkflowTestHelper.TestEventListener;
import com.isfett.workflow.event.EventDispatcher;
import com.isfett.workflow.event.GuardEvent;
import com.isfett.workflow.exception.*;
import com.isfett.workflow.markingstore.MarkingStoreInterface;
import com.isfett.workflow.markingstore.MethodMarkingStore;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class WorkflowTest {
    @Test
    void testGetMarkingWithEmptyDefinition() {
        MultiStateMethodSubject subject = new MultiStateMethodSubject();
        Workflow workflow = new Workflow(new Definition(List.of(), List.of(), List.of()), new MethodMarkingStore("status"), "workflow_name");

        assertEquals("workflow_name", workflow.getName());

        Throwable exception = assertThrows(MarkingIsEmptyAndNoInitialPlaceFoundException.class, () -> workflow.getMarking(subject));
        assertEquals("The Marking is empty and there is no initial place for workflow workflow_name.", exception.getMessage());
    }

    @Test
    void testGetMarkingWithoutPlaces() {
        MultiStateMethodSubject subject = new MultiStateMethodSubject();
        subject.setStatus(List.of("nope"));
        Workflow workflow = new Workflow(new Definition(List.of(), List.of(), List.of()), new MethodMarkingStore("status"));

        Throwable exception = assertThrows(NoPlacesDefinedException.class, () -> workflow.getMarking(subject));
        assertEquals("It seems you forgot to add places to the current workflow.", exception.getMessage());
    }

    @Test
    void testGetMarkingWithImpossiblePlace() {
        MultiStateMethodSubject subject = new MultiStateMethodSubject();
        subject.setStatus(List.of("nope"));
        Workflow workflow = new Workflow(new Definition(List.of("a"), List.of(), List.of()), new MethodMarkingStore("status"));

        Throwable exception = assertThrows(PlaceNotValidForWorkflowException.class, () -> workflow.getMarking(subject));
        assertEquals("Place nope is not valid for workflow unnamed.", exception.getMessage());
    }

    @Test
    void testGetMarkingWithEmptyInitialMarking() {
        MultiStateMethodSubject subject = new MultiStateMethodSubject();
        MarkingStoreInterface markingStore = new MethodMarkingStore("status");
        Definition definition = WorkflowTestHelper.createComplexWorkflowDefinition();
        Workflow workflow = new Workflow(definition, markingStore);

        assertSame(definition, workflow.getDefinition());
        assertSame(markingStore, workflow.getMarkingStore());

        Marking marking = workflow.getMarking(subject);

        assertTrue(marking.has("a"));
        assertEquals(List.of("a"), subject.getStatus());
    }

    @Test
    void testGetMarkingWithExistingMarking() {
        MultiStateMethodSubject subject = new MultiStateMethodSubject();
        subject.setStatus(List.of("b", "c"));
        Workflow workflow = new Workflow(WorkflowTestHelper.createComplexWorkflowDefinition(), new MethodMarkingStore("status"));

        Marking marking = workflow.getMarking(subject);

        assertTrue(marking.has("b"));
        assertTrue(marking.has("c"));
        assertEquals(List.of("b", "c"), subject.getStatus());
    }

    @Test
    void testCanWithNotExistingTransition() {
        MultiStateMethodSubject subject = new MultiStateMethodSubject();
        Workflow workflow = new Workflow(WorkflowTestHelper.createComplexWorkflowDefinition(), new MethodMarkingStore("status"));

        assertFalse(workflow.can(subject, "foobar"));
    }

    @Test
    void testCan() {
        MultiStateMethodSubject subject = new MultiStateMethodSubject();
        Workflow workflow = new Workflow(WorkflowTestHelper.createComplexWorkflowDefinition(), new MethodMarkingStore("status"));

        assertTrue(workflow.can(subject, "t1"));
        assertFalse(workflow.can(subject, "t2"));

        subject.setStatus(List.of("b"));

        assertFalse(workflow.can(subject, "t1"));
        assertFalse(workflow.can(subject, "t2"));

        subject.setStatus(List.of("b", "c"));

        assertFalse(workflow.can(subject, "t1"));
        assertTrue(workflow.can(subject, "t2"));

        subject.setStatus(List.of("f"));

        assertFalse(workflow.can(subject, "t5"));
        assertTrue(workflow.can(subject, "t6"));
    }

    @Test
    void testCanWithGuardEvent() {
        Definition definition = WorkflowTestHelper.createComplexWorkflowDefinition();
        MultiStateMethodSubject subject = new MultiStateMethodSubject();
        EventDispatcher eventDispatcher = new EventDispatcher();
        GuardBlockEventListener eventListener = new GuardBlockEventListener();
        Workflow workflow = new Workflow(definition, new MethodMarkingStore("status"), "workflow_name", eventDispatcher);

        workflow.addEventListener(eventListener, List.of("workflow.workflow_name.guard.t1"));

        assertFalse(workflow.can(subject, "t1"));
        assertEquals(1, eventListener.getFiredEvents().size());
        GuardEvent event = eventListener.getEvents().get(0);
        assertTrue(event.isBlocked());
    }

    @Test
    void testCanWithGuardEventUnblock() {
        Definition definition = WorkflowTestHelper.createComplexWorkflowDefinition();
        MultiStateMethodSubject subject = new MultiStateMethodSubject();
        EventDispatcher eventDispatcher = new EventDispatcher();
        GuardBlockUnblockEventListener eventListener = new GuardBlockUnblockEventListener();
        Workflow workflow = new Workflow(definition, new MethodMarkingStore("status"), "workflow_name", eventDispatcher);

        workflow.addEventListener(eventListener, List.of("workflow.workflow_name.guard.t1"));

        assertFalse(workflow.can(subject, "t1"));
        assertEquals(1, eventListener.getFiredEvents().size());
        GuardEvent event = eventListener.getEvents().get(0);
        assertTrue(event.isBlocked());

        assertTrue(workflow.can(subject, "t1"));
        assertEquals(2, eventListener.getFiredEvents().size());
        event = eventListener.getEvents().get(1);
        assertFalse(event.isBlocked());

        assertTrue(workflow.can(subject, "t1"));
        assertEquals(3, eventListener.getFiredEvents().size());
        event = eventListener.getEvents().get(2);
        assertFalse(event.isBlocked());

        assertFalse(workflow.can(subject, "t1"));
        assertEquals(4, eventListener.getFiredEvents().size());
        event = eventListener.getEvents().get(3);
        assertTrue(event.isBlocked());
    }

    @Test
    void testCanDoesNotTriggerGuardEventsForNotEnabledTransitions() {
        MultiStateMethodSubject subject = new MultiStateMethodSubject();
        EventDispatcher eventDispatcher = new EventDispatcher();
        Workflow workflow = new Workflow(WorkflowTestHelper.createComplexWorkflowDefinition(), new MethodMarkingStore("status"), "workflow_name", eventDispatcher);
        TestEventListener eventListener = new TestEventListener();

        workflow.apply(subject, "t1");
        workflow.apply(subject, "t2");

        workflow.addEventListener(eventListener, List.of("workflow.workflow_name.guard.t3", "workflow.workflow_name.guard.t4"));

        workflow.can(subject, "t3");

        assertEquals(List.of("workflow.workflow_name.guard.t3"), eventListener.getFiredEvents());
    }

    @Test
    void testCanWithSameNameTransitions() {
        MultiStateMethodSubject subject = new MultiStateMethodSubject();
        Workflow workflow = new Workflow(WorkflowTestHelper.createWorkflowWithSameNameTransitions(), new MethodMarkingStore("status"));

        assertTrue(workflow.can(subject, "a_to_bc"));
        assertFalse(workflow.can(subject, "b_to_c"));
        assertFalse(workflow.can(subject, "to_a"));

        subject.setStatus(List.of("b"));
        assertFalse(workflow.can(subject, "a_to_bc"));
        assertTrue(workflow.can(subject, "b_to_c"));
        assertTrue(workflow.can(subject, "to_a"));
    }

    @Test
    void testBuildTransitionBlockerListReturnsTransitionUndefinedException() {
        MultiStateMethodSubject subject = new MultiStateMethodSubject();
        Workflow workflow = new Workflow(WorkflowTestHelper.createSimpleWorkflowDefinition(), new MethodMarkingStore("status"));

        Throwable exception = assertThrows(TransitionUndefinedException.class, () -> workflow.buildTransitionBlockerList(subject, "nope"));
        assertEquals("Transition nope is not defined for workflow unnamed.", exception.getMessage());
    }

    @Test
    void testBuildTransitionBlockerList() {
        MultiStateMethodSubject subject = new MultiStateMethodSubject();
        Workflow workflow = new Workflow(WorkflowTestHelper.createComplexWorkflowDefinition(), new MethodMarkingStore("status"));

        assertTrue(workflow.buildTransitionBlockerList(subject, "t1").isEmpty());
        assertFalse(workflow.buildTransitionBlockerList(subject, "t2").isEmpty());

        subject.setStatus(List.of("b"));

        assertFalse(workflow.buildTransitionBlockerList(subject, "t1").isEmpty());
        assertFalse(workflow.buildTransitionBlockerList(subject, "t2").isEmpty());

        subject.setStatus(List.of("b", "c"));

        assertFalse(workflow.buildTransitionBlockerList(subject, "t1").isEmpty());
        assertTrue(workflow.buildTransitionBlockerList(subject, "t2").isEmpty());

        subject.setStatus(List.of("f"));

        assertFalse(workflow.buildTransitionBlockerList(subject, "t5").isEmpty());
        assertTrue(workflow.buildTransitionBlockerList(subject, "t6").isEmpty());
    }

    @Test
    void testBuildTransitionBlockerListReturnsReasonsProvidedByMarking() {
        MultiStateMethodSubject subject = new MultiStateMethodSubject();
        Workflow workflow = new Workflow(WorkflowTestHelper.createComplexWorkflowDefinition(), new MethodMarkingStore("status"));

        List<TransitionBlocker> transitionBlockers = workflow.buildTransitionBlockerList(subject, "t2");
        assertEquals(1, transitionBlockers.size());
        TransitionBlocker transitionBlocker = transitionBlockers.get(0);
        assertEquals("The marking does not enable the transition.", transitionBlocker.getMessage());
        assertEquals("blocked_by_marking", transitionBlocker.getCode());
    }

    @Test
    void testBuildTransitionBlockerListReturnsReasonsProvidedInGuards() {
        MultiStateMethodSubject subject = new MultiStateMethodSubject();
        EventDispatcher eventDispatcher = new EventDispatcher();
        Workflow workflow = new Workflow(WorkflowTestHelper.createSimpleWorkflowDefinition(), new MethodMarkingStore("status"), "workflow_name", eventDispatcher);
        GuardBlockEventListener eventListener = new GuardBlockEventListener();
        workflow.addEventListener(eventListener, List.of("workflow.guard"));

        List<TransitionBlocker> transitionBlockerList = workflow.buildTransitionBlockerList(subject, "t1");
        assertEquals(1, transitionBlockerList.size());
        TransitionBlocker transitionBlocker = transitionBlockerList.get(0);
        assertEquals("Blocked by GuardBlockEventListener", transitionBlocker.getMessage());
        assertEquals("blocked_by_unknown", transitionBlocker.getCode());
        assertEquals(0, transitionBlocker.getParameters().size());
    }

    @Test
    void testApplyWithNotExistingTransition() {
        MultiStateMethodSubject subject = new MultiStateMethodSubject();
        Workflow workflow = new Workflow(WorkflowTestHelper.createComplexWorkflowDefinition(), new MethodMarkingStore("status"));

        Throwable exception = assertThrows(TransitionUndefinedException.class, () -> workflow.apply(subject, "nope"));
        assertEquals("Transition nope is not defined for workflow unnamed.", exception.getMessage());
    }

    @Test
    void testApplyWithNotEnabledTransition() {
        MultiStateMethodSubject subject = new MultiStateMethodSubject();
        Workflow workflow = new Workflow(WorkflowTestHelper.createComplexWorkflowDefinition(), new MethodMarkingStore("status"));

        Throwable exception = assertThrows(TransitionNotEnabledException.class, () -> workflow.apply(subject, "t2"));
        assertEquals("Transition t2 is not enabled for workflow unnamed.", exception.getMessage());
    }

    @Test
    void testApply() {
        MultiStateMethodSubject subject = new MultiStateMethodSubject();
        Workflow workflow = new Workflow(WorkflowTestHelper.createComplexWorkflowDefinition(), new MethodMarkingStore("status"));

        Marking marking = workflow.apply(subject, "t1");
        assertFalse(marking.has("a"));
        assertTrue(marking.has("b"));
        assertTrue(marking.has("c"));
    }

    @Test
    void testApplyWithSameNameTransitions() {
        MultiStateMethodSubject subject = new MultiStateMethodSubject();
        Workflow workflow = new Workflow(WorkflowTestHelper.createWorkflowWithSameNameTransitions(), new MethodMarkingStore("status"));

        Marking marking = workflow.apply(subject, "a_to_bc");
        assertFalse(marking.has("a"));
        assertTrue(marking.has("b"));
        assertTrue(marking.has("c"));

        marking = workflow.apply(subject, "to_a");
        assertTrue(marking.has("a"));
        assertFalse(marking.has("b"));
        assertFalse(marking.has("c"));

        workflow.apply(subject, "a_to_bc");
        marking = workflow.apply(subject, "b_to_c");
        assertFalse(marking.has("a"));
        assertFalse(marking.has("b"));
        assertTrue(marking.has("c"));

        marking = workflow.apply(subject, "to_a");
        assertTrue(marking.has("a"));
        assertFalse(marking.has("b"));
        assertFalse(marking.has("c"));
    }

    @Test
    void testApplyWithSameNameTransitions2() {
        MultiStateMethodSubject subject = new MultiStateMethodSubject();
        subject.setStatus(List.of("a", "b"));
        Definition definition = new Definition(
            List.of("a", "b", "c", "d"),
            List.of(new Transition("t", List.of("a"), List.of("c")), new Transition("t", List.of("b"), List.of("d"))),
            List.of()
        );
        Workflow workflow = new Workflow(definition, new MethodMarkingStore("status"));

        Marking marking = workflow.apply(subject, "t");
        assertFalse(marking.has("a"));
        assertFalse(marking.has("b"));
        assertTrue(marking.has("c"));
        assertTrue(marking.has("d"));
    }

    @Test
    void testApplyWithSameNameTransitions3() {
        MultiStateMethodSubject subject = new MultiStateMethodSubject();
        subject.setStatus(List.of("a"));
        Definition definition = new Definition(
            List.of("a", "b", "c", "d"),
            List.of(
                new Transition("t", List.of("a"), List.of("b")),
                new Transition("t", List.of("b"), List.of("c")),
                new Transition("t", List.of("c"), List.of("d"))
            ),
            List.of()
        );
        Workflow workflow = new Workflow(definition, new MethodMarkingStore("status"));

        Marking marking = workflow.apply(subject, "t");
        assertTrue(marking.has("b"));
        assertFalse(marking.has("a"));
        assertFalse(marking.has("c"));
        assertFalse(marking.has("d"));
    }

    @Test
    void testGetEnabledTransitions() {
        MultiStateMethodSubject subject = new MultiStateMethodSubject();
        Workflow workflow = new Workflow(WorkflowTestHelper.createComplexWorkflowDefinition(), new MethodMarkingStore("status"));

        List<Transition> enabledTransitions = workflow.getEnabledTransitions(subject);
        assertEquals(1, enabledTransitions.size());
        assertEquals("t1", enabledTransitions.get(0).getName());

        subject.setStatus(List.of("d"));
        enabledTransitions = workflow.getEnabledTransitions(subject);
        assertEquals(2, enabledTransitions.size());
        assertEquals("t3", enabledTransitions.get(0).getName());
        assertEquals("t4", enabledTransitions.get(1).getName());

        subject.setStatus(List.of("c", "e"));
        enabledTransitions = workflow.getEnabledTransitions(subject);
        assertEquals(1, enabledTransitions.size());
        assertEquals("t5", enabledTransitions.get(0).getName());
    }

    @Test
    void testGetEnabledTransitionsWithSameNameTransitions() {
        MultiStateMethodSubject subject = new MultiStateMethodSubject();
        Workflow workflow = new Workflow(WorkflowTestHelper.createWorkflowWithSameNameTransitions(), new MethodMarkingStore("status"));

        List<Transition> enabledTransitions = workflow.getEnabledTransitions(subject);
        assertEquals(1, enabledTransitions.size());
        assertEquals("a_to_bc", enabledTransitions.get(0).getName());

        subject.setStatus(List.of("b", "c"));
        enabledTransitions = workflow.getEnabledTransitions(subject);
        assertEquals(3, enabledTransitions.size());
        assertEquals("b_to_c", enabledTransitions.get(0).getName());
        assertEquals("to_a", enabledTransitions.get(1).getName());
        assertEquals("to_a", enabledTransitions.get(2).getName());
    }

    @Test
    void testGetEnabledTransition() {
        MultiStateMethodSubject subject = new MultiStateMethodSubject();
        Workflow workflow = new Workflow(WorkflowTestHelper.createComplexWorkflowDefinition(), new MethodMarkingStore("status"));

        Transition enabledTransition = workflow.getEnabledTransition(subject, "t3");
        assertNull(enabledTransition);

        subject.setStatus(List.of("d"));
        enabledTransition = workflow.getEnabledTransition(subject, "t3");
        assertEquals("t3", enabledTransition.getName());

        enabledTransition = workflow.getEnabledTransition(subject, "nope");
        assertNull(enabledTransition);
    }

    @Test
    void testApplyWithEventDispatcher() {
        MultiStateMethodSubject subject = new MultiStateMethodSubject();
        EventDispatcher eventDispatcher = new EventDispatcher();
        Workflow workflow = new Workflow(WorkflowTestHelper.createComplexWorkflowDefinition(), new MethodMarkingStore("status"), "workflow_name", eventDispatcher);
        TestEventListener eventListener = new TestEventListener();

        workflow.addEventListener(eventListener, List.of());

        List<String> expectedEventNames = List.of(
            "workflow.entered",
            "workflow.workflow_name.entered",
            "workflow.workflow_name.entered.a",
            "workflow.guard",
            "workflow.workflow_name.guard",
            "workflow.workflow_name.guard.t1",
            "workflow.leave",
            "workflow.workflow_name.leave",
            "workflow.workflow_name.leave.a",
            "workflow.transition",
            "workflow.workflow_name.transition",
            "workflow.workflow_name.transition.t1",
            "workflow.enter",
            "workflow.workflow_name.enter",
            "workflow.workflow_name.enter.b",
            "workflow.workflow_name.enter.c",
            "workflow.entered",
            "workflow.workflow_name.entered",
            "workflow.workflow_name.entered.b",
            "workflow.workflow_name.entered.c",
            "workflow.completed",
            "workflow.workflow_name.completed",
            "workflow.workflow_name.completed.t1",
            "workflow.announce",
            "workflow.workflow_name.announce",
            "workflow.guard",
            "workflow.workflow_name.guard",
            "workflow.workflow_name.guard.t2",
            "workflow.workflow_name.announce.t2"
        );

        workflow.apply(subject, "t1");

        assertEquals(expectedEventNames.size(), eventListener.getFiredEvents().size());
        assertEquals(expectedEventNames, eventListener.getFiredEvents());
    }

    @Test
    void testApplyWithEventDispatcherCheckEvents() {
        MultiStateMethodSubject subject = new MultiStateMethodSubject();
        EventDispatcher eventDispatcher = new EventDispatcher();
        Workflow workflow = new Workflow(WorkflowTestHelper.createComplexWorkflowDefinition(), new MethodMarkingStore("status"), "workflow_name", eventDispatcher);
        CheckPropertyEventListener eventListener = new CheckPropertyEventListener();

        workflow.addEventListener(eventListener, List.of("workflow.workflow_name.leave.a"));

        workflow.apply(subject, "t1");

        List<String> expectedProperties = List.of(
            "workflow_name",
            "[a]",
            "t1"
        );
        assertEquals(expectedProperties.size(), eventListener.getProperties().size());
        assertEquals(expectedProperties, eventListener.getProperties());
    }

    @Test
    void testApplyWithEventDispatcherWithNotFoundMethod() {
        MultiStateMethodSubject subject = new MultiStateMethodSubject();
        EventDispatcher eventDispatcher = new EventDispatcher();
        Workflow workflow = new Workflow(WorkflowTestHelper.createComplexWorkflowDefinition(), new MethodMarkingStore("status"), "workflow_name", eventDispatcher);
        CheckPropertyEventListener eventListener = new CheckPropertyEventListener();

        workflow.addEventListener(eventListener, List.of("workflow.workflow_name.enter.b"));

        Throwable exception = assertThrows(EventCanNotBeDispatchedException.class, () -> workflow.apply(subject, "t1"));
        assertEquals("The event workflow.workflow_name.enter.b can't be dispatched in CheckPropertyEventListener. The method onEnter does not exist or is not public.", exception.getMessage());
    }

    @Test
    void testApplyWithEventDispatcherWithNotPublicMethod() {
        MultiStateMethodSubject subject = new MultiStateMethodSubject();
        EventDispatcher eventDispatcher = new EventDispatcher();
        Workflow workflow = new Workflow(WorkflowTestHelper.createComplexWorkflowDefinition(), new MethodMarkingStore("status"), "workflow_name", eventDispatcher);
        MethodNotPublicEventListener eventListener = new MethodNotPublicEventListener();

        workflow.addEventListener(eventListener, List.of("workflow.workflow_name.enter.b"));

        Throwable exception = assertThrows(EventCanNotBeDispatchedException.class, () -> workflow.apply(subject, "t1"));
        assertEquals("The event workflow.workflow_name.enter.b can't be dispatched in MethodNotPublicEventListener. The method onEnter does not exist or is not public.", exception.getMessage());
    }

    @Test
    void testApplyWithEventDispatcherAndCustomEventsToBeDispatched() {
        MultiStateMethodSubject subject = new MultiStateMethodSubject();
        EventDispatcher eventDispatcher = new EventDispatcher();
        Workflow workflow = new Workflow(WorkflowTestHelper.createComplexWorkflowDefinition(), new MethodMarkingStore("status"), "workflow_name", eventDispatcher, List.of(Workflow.events.ENTER, Workflow.events.LEAVE));
        TestEventListener eventListener = new TestEventListener();

        workflow.addEventListener(eventListener, List.of());

        List<String> expectedEventNames = List.of(
            "workflow.guard",
            "workflow.workflow_name.guard",
            "workflow.workflow_name.guard.t1",
            "workflow.leave",
            "workflow.workflow_name.leave",
            "workflow.workflow_name.leave.a",
            "workflow.enter",
            "workflow.workflow_name.enter",
            "workflow.workflow_name.enter.b",
            "workflow.workflow_name.enter.c"
        );

        workflow.apply(subject, "t1");
        assertEquals(expectedEventNames.size(), eventListener.getFiredEvents().size());
        assertEquals(expectedEventNames, eventListener.getFiredEvents());
    }

    @Test
    void testApplyWithEventDispatcherAndEmptyEventsToBeDispatched() {
        MultiStateMethodSubject subject = new MultiStateMethodSubject();
        EventDispatcher eventDispatcher = new EventDispatcher();
        Workflow workflow = new Workflow(WorkflowTestHelper.createComplexWorkflowDefinition(), new MethodMarkingStore("status"), "workflow_name", eventDispatcher, List.of());
        TestEventListener eventListener = new TestEventListener();

        workflow.addEventListener(eventListener, List.of());

        workflow.apply(subject, "t1");

        List<String> expectedEvents = List.of(
            "workflow.guard",
            "workflow.workflow_name.guard",
            "workflow.workflow_name.guard.t1"
        );

        assertEquals(expectedEvents.size(), eventListener.getFiredEvents().size());
        assertEquals(expectedEvents, eventListener.getFiredEvents());
    }

    @Test
    void testApplyWithEventDispatcherAndNullEventsToBeDispatched() {
        MultiStateMethodSubject subject = new MultiStateMethodSubject();
        EventDispatcher eventDispatcher = new EventDispatcher();
        Workflow workflow = new Workflow(WorkflowTestHelper.createComplexWorkflowDefinition(), new MethodMarkingStore("status"), "workflow_name", eventDispatcher, null);
        TestEventListener eventListener = new TestEventListener();

        workflow.addEventListener(eventListener, List.of());

        List<String> expectedEventNames = List.of(
            "workflow.entered",
            "workflow.workflow_name.entered",
            "workflow.workflow_name.entered.a",
            "workflow.guard",
            "workflow.workflow_name.guard",
            "workflow.workflow_name.guard.t1",
            "workflow.leave",
            "workflow.workflow_name.leave",
            "workflow.workflow_name.leave.a",
            "workflow.transition",
            "workflow.workflow_name.transition",
            "workflow.workflow_name.transition.t1",
            "workflow.enter",
            "workflow.workflow_name.enter",
            "workflow.workflow_name.enter.b",
            "workflow.workflow_name.enter.c",
            "workflow.entered",
            "workflow.workflow_name.entered",
            "workflow.workflow_name.entered.b",
            "workflow.workflow_name.entered.c",
            "workflow.completed",
            "workflow.workflow_name.completed",
            "workflow.workflow_name.completed.t1",
            "workflow.announce",
            "workflow.workflow_name.announce",
            "workflow.guard",
            "workflow.workflow_name.guard",
            "workflow.workflow_name.guard.t2",
            "workflow.workflow_name.announce.t2"
        );

        workflow.apply(subject, "t1");
        assertEquals(expectedEventNames.size(), eventListener.getFiredEvents().size());
        assertEquals(expectedEventNames, eventListener.getFiredEvents());
    }
}