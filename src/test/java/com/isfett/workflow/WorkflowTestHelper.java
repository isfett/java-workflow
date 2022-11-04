package com.isfett.workflow;

import com.isfett.workflow.event.AbstractWorkflowEvent;
import com.isfett.workflow.event.AnnounceEvent;
import com.isfett.workflow.event.CompletedEvent;
import com.isfett.workflow.event.EnterEvent;
import com.isfett.workflow.event.EnteredEvent;
import com.isfett.workflow.event.EventListenerInterface;
import com.isfett.workflow.event.GuardEvent;
import com.isfett.workflow.event.LeaveEvent;
import com.isfett.workflow.event.TransitionEvent;
import java.util.ArrayList;
import java.util.EventListener;
import java.util.List;

@SuppressWarnings("unused")
public class WorkflowTestHelper {
    public static Definition createSimpleWorkflowDefinition() {
        List<String> places = List.of("a", "b", "c");
        List<Transition> transitions = new ArrayList<>();
        transitions.add(new Transition("t1", List.of("a"), List.of("b")));
        transitions.add(new Transition("t2", List.of("b"), List.of("c")));

        return new Definition(places, transitions, List.of());
    }

    public static Definition createComplexWorkflowDefinition() {
        List<String> places = List.of("a", "b", "c", "d", "e", "f", "g");
        List<Transition> transitions = new ArrayList<>();
        transitions.add(new Transition("t1", List.of("a"), List.of("b", "c")));
        transitions.add(new Transition("t2", List.of("b", "c"), List.of("d")));
        transitions.add(new Transition("t3", List.of("d"), List.of("e")));
        transitions.add(new Transition("t4", List.of("d"), List.of("f")));
        transitions.add(new Transition("t5", List.of("e"), List.of("g")));
        transitions.add(new Transition("t6", List.of("f"), List.of("g")));

        return new Definition(places, transitions, List.of());
    }

    public static Definition createWorkflowWithSameNameTransitions() {
        List<String> places = List.of("a", "b", "c");
        List<Transition> transitions = new ArrayList<>();
        transitions.add(new Transition("a_to_bc", List.of("a"), List.of("b", "c")));
        transitions.add(new Transition("b_to_c", List.of("b"), List.of("c")));
        transitions.add(new Transition("to_a", List.of("b"), List.of("a")));
        transitions.add(new Transition("to_a", List.of("c"), List.of("a")));

        return new Definition(places, transitions, List.of());
    }

    public static Definition createComplexStateMachineDefinition() {
        List<String> places = List.of("a", "b", "c", "d");
        List<Transition> transitions = new ArrayList<>();
        transitions.add(new Transition("t1", List.of("a"), List.of("b")));
        transitions.add(new Transition("t1", List.of("d"), List.of("b")));
        transitions.add(new Transition("t2", List.of("b"), List.of("c")));
        transitions.add(new Transition("t3", List.of("b"), List.of("d")));

        return new Definition(places, transitions, List.of());
    }

    final public static class SingleStatePropertySubject {

        public String status;

        public SingleStatePropertySubject() {
        }

        public SingleStatePropertySubject(String status) {
            this.status = status;
        }
    }

    final public static class SingleStatePropertyEnumSubject {
        public enum status {
            FIRST_PLACE,
            SECOND_PLACE
        }

        public status status;

        public SingleStatePropertyEnumSubject() {
        }

        public SingleStatePropertyEnumSubject(status status) {
            this.status = status;
        }
    }

    final public static class SingleStateMethodSubject {

        private String status;

        public SingleStateMethodSubject() {
        }

        public SingleStateMethodSubject(String status) {
            this.status = status;
        }

        public String getStatus() {
            return status;
        }

        public void setStatus(String status) {
            this.status = status;
        }
    }

    final public static class SingleStatePrivateMethodSubject {

        private String status;

        public SingleStatePrivateMethodSubject() {
        }

        public SingleStatePrivateMethodSubject(String status) {
            this.status = status;
        }

        private String getStatus() {
            return status;
        }

        private void setStatus(String status) {
            this.status = status;
        }
    }

    final public static class SingleStateMethodEnumSubject {
        public enum status {
            FIRST_PLACE,
            SECOND_PLACE
        }

        private status status;

        public SingleStateMethodEnumSubject() {
        }

        public SingleStateMethodEnumSubject(status status) {
            this.status = status;
        }

        public status getStatus() {
            return status;
        }

        public void setStatus(status status) {
            this.status = status;
        }
    }

    final public static class MultiStatePropertySubject {

        public List<String> status;

        public MultiStatePropertySubject() {
        }

        public MultiStatePropertySubject(List<String> status) {
            this.status = status;
        }
    }

    final public static class MultiStatePropertyEnumSubject {
        public enum status {
            FIRST_PLACE,
            SECOND_PLACE
        }

        public List<status> status;

        public MultiStatePropertyEnumSubject() {
        }

        public MultiStatePropertyEnumSubject(List<status> status) {
            this.status = status;
        }
    }

    final public static class MultiStateMethodSubject {

        private List<String> status;

        public MultiStateMethodSubject() {
        }

        public MultiStateMethodSubject(List<String> status) {
            this.status = status;
        }

        public List<String> getStatus() {
            return status;
        }

        public void setStatus(List<String> status) {
            this.status = status;
        }
    }

    final public static class MultiStateMethodEnumSubject {
        public enum status {
            FIRST_PLACE,
            SECOND_PLACE
        }

        private List<status> status;

        public MultiStateMethodEnumSubject() {
        }

        public MultiStateMethodEnumSubject(List<status> status) {
            this.status = status;
        }

        public List<status> getStatus() {
            return status;
        }

        public void setStatus(List<status> status) {
            this.status = status;
        }
    }

    public static class GuardBlockEventListener implements EventListenerInterface {

        private final List<String> firedEvents = new ArrayList<>();
        private final List<GuardEvent> events = new ArrayList<>();

        public void onGuard(GuardEvent event, String eventName) {
            this.on(event, eventName);

            event.setBlocked(true, "Blocked by GuardBlockEventListener");
        }

        private void on(GuardEvent event, String eventName) {
            this.firedEvents.add(eventName);
            this.events.add(event);
        }

        public List<String> getFiredEvents() {
            return firedEvents;
        }

        public List<GuardEvent> getEvents() {
            return events;
        }
    }

    public static class GuardBlockUnblockEventListener implements EventListenerInterface {

        private final List<String> firedEvents = new ArrayList<>();
        private final List<GuardEvent> events = new ArrayList<>();
        private Integer counter = 0;

        public void onGuard(GuardEvent event, String eventName) {
            this.on(event, eventName);

            if (this.counter == 0) {
                event.setBlocked(true);
            } else if (this.counter == 1) {
                event.setBlocked(false);
            } else if (this.counter == 2) {
                event.setBlocked(false, "Foo");
            } else {
                event.setBlocked(true, "Blocked by GuardBlockEventListener");
            }
            this.counter++;
        }

        private void on(GuardEvent event, String eventName) {
            this.firedEvents.add(eventName);
            this.events.add(event);
        }

        public List<String> getFiredEvents() {
            return firedEvents;
        }

        public List<GuardEvent> getEvents() {
            return events;
        }
    }

    public static class CheckPropertyEventListener implements EventListenerInterface {

        private final List<String> properties = new ArrayList<>();

        public void onLeave(LeaveEvent event, String eventName) {
            this.properties.add(event.getWorkflow().getName());
            this.properties.add(event.getMarking().getPlaces().toString());
            this.properties.add(event.getTransition().getName());
        }

        public List<String> getProperties() {
            return properties;
        }
    }

    public static class MethodNotPublicEventListener implements EventListenerInterface {

        private void onEnter(EnterEvent event, String eventName) {
            System.out.println("nope");
        }
    }

    public static class TestEventListener implements EventListenerInterface {

        private final List<String> firedEvents = new ArrayList<>();

        public void onLeave(LeaveEvent event, String eventName) {
            this.on(event, eventName);
        }

        public void onEnter(EnterEvent event, String eventName) {
            this.on(event, eventName);
        }

        public void onEntered(EnteredEvent event, String eventName) {
            this.on(event, eventName);
        }

        public void onTransition(TransitionEvent event, String eventName) {
            this.on(event, eventName);
        }

        public void onAnnounce(AnnounceEvent event, String eventName) {
            this.on(event, eventName);
        }

        public void onCompleted(CompletedEvent event, String eventName) {
            this.on(event, eventName);
        }

        public void onGuard(GuardEvent event, String eventName) {
            this.on(event, eventName);
        }

        private void on(AbstractWorkflowEvent event, String eventName) {
            this.firedEvents.add(eventName);
        }

        public List<String> getFiredEvents() {
            return firedEvents;
        }
    }
}
