package com.isfett.workflow.markingstore;

import com.isfett.workflow.Marking;
import com.isfett.workflow.WorkflowTestHelper.MultiStateMethodEnumSubject;
import com.isfett.workflow.WorkflowTestHelper.MultiStateMethodEnumSubject.status;
import com.isfett.workflow.WorkflowTestHelper.MultiStateMethodSubject;
import com.isfett.workflow.WorkflowTestHelper.SingleStateMethodEnumSubject;
import com.isfett.workflow.WorkflowTestHelper.SingleStateMethodSubject;
import com.isfett.workflow.WorkflowTestHelper.SingleStatePrivateMethodSubject;
import com.isfett.workflow.exception.InvalidMethodMarkingStoreConfigurationException;
import com.isfett.workflow.exception.MethodNotFoundInClassException;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class MethodMarkingStoreTest {
    @Test
    void testGetSetMarkingWithSingleState() {
        SingleStateMethodSubject subject = new SingleStateMethodSubject();

        MarkingStoreInterface markingStore = new MethodMarkingStore(true, "status");

        assertTrue(markingStore.isSingleState());

        Marking marking = markingStore.getMarking(subject);

        assertEquals(0, marking.getPlaces().size());

        marking.mark("first_place");
        markingStore.setMarking(subject, marking);

        assertEquals("first_place", subject.getStatus());
        assertEquals(1, marking.getPlaces().size());

        Marking marking2 = markingStore.getMarking(subject);
        assertEquals(marking.getPlaces(), marking2.getPlaces());
    }

    @Test
    void testGetSetMarkingWithSingleStateAndEnum() {
        SingleStateMethodEnumSubject subject = new SingleStateMethodEnumSubject();

        MarkingStoreInterface markingStore = new MethodMarkingStore(true, "status");

        Marking marking = markingStore.getMarking(subject);

        assertEquals(0, marking.getPlaces().size());

        marking.mark("FIRST_PLACE");
        markingStore.setMarking(subject, marking);

        assertEquals(status.FIRST_PLACE.toString(), subject.getStatus().toString());
        assertEquals(1, marking.getPlaces().size());

        Marking marking2 = markingStore.getMarking(subject);
        assertEquals(marking.getPlaces(), marking2.getPlaces());
    }

    @Test
    void testGetMarkingWithSingleStateWithNonExistentMethod() {
        SingleStateMethodSubject subject = new SingleStateMethodSubject();

        MarkingStoreInterface markingStore = new MethodMarkingStore(true, "nonexistent");

        Throwable exception = assertThrows(MethodNotFoundInClassException.class, () -> {
            markingStore.getMarking(subject);
        });
        assertEquals("The method getNonexistent::com.isfett.workflow.WorkflowTestHelper$SingleStateMethodSubject() does not exist.", exception.getMessage());
    }

    @Test
    void testGetMarkingWithSingleStateWithPrivateMethod() {
        SingleStatePrivateMethodSubject subject = new SingleStatePrivateMethodSubject();

        MarkingStoreInterface markingStore = new MethodMarkingStore(true, "status");

        Throwable exception = assertThrows(MethodNotFoundInClassException.class, () -> {
            markingStore.getMarking(subject);
        });
        assertEquals("The method getStatus::com.isfett.workflow.WorkflowTestHelper$SingleStatePrivateMethodSubject() does not exist.", exception.getMessage());
    }

    @Test
    void testSetMarkingWithSingleStateWithNonExistentMethod() {
        SingleStateMethodSubject subject = new SingleStateMethodSubject();

        MarkingStoreInterface markingStore = new MethodMarkingStore(true, "nonexistent");

        Marking marking = new Marking(List.of("first_place"));

        marking.mark("first_place");

        Throwable exception = assertThrows(MethodNotFoundInClassException.class, () -> {
            markingStore.setMarking(subject, marking);
        });
        assertEquals("The method setNonexistent::com.isfett.workflow.WorkflowTestHelper$SingleStateMethodSubject() does not exist.", exception.getMessage());
    }

    @Test
    void testSetMarkingWithSingleStateWithNPrivateMethod() {
        SingleStatePrivateMethodSubject subject = new SingleStatePrivateMethodSubject();

        MarkingStoreInterface markingStore = new MethodMarkingStore(true, "status");

        Marking marking = new Marking(List.of("first_place"));

        marking.mark("first_place");

        Throwable exception = assertThrows(MethodNotFoundInClassException.class, () -> {
            markingStore.setMarking(subject, marking);
        });
        assertEquals("The method setStatus::com.isfett.workflow.WorkflowTestHelper$SingleStatePrivateMethodSubject() does not exist.", exception.getMessage());
    }

    @Test
    void testGetMarkingWithSingleStateWithWrongMarkingStoreConfiguration() {
        SingleStateMethodSubject subject = new SingleStateMethodSubject();
        subject.setStatus("draft");

        MarkingStoreInterface markingStore = new MethodMarkingStore(false, "status");

        Throwable exception = assertThrows(InvalidMethodMarkingStoreConfigurationException.class, () -> {
            markingStore.getMarking(subject);
        });
        assertEquals("The method getStatus::com.isfett.workflow.WorkflowTestHelper$SingleStateMethodSubject() threw an exception. Marking store is misconfigured maybe.", exception.getMessage());
    }

    @Test
    void testSetMarkingWithSingleStateWithWrongMarkingStoreConfiguration() {
        SingleStateMethodSubject subject = new SingleStateMethodSubject();

        MarkingStoreInterface markingStore = new MethodMarkingStore(false, "status");

        Marking marking = new Marking(List.of("first_place"));

        Throwable exception = assertThrows(InvalidMethodMarkingStoreConfigurationException.class, () -> {
            markingStore.setMarking(subject, marking);
        });
        assertEquals("The method setStatus::com.isfett.workflow.WorkflowTestHelper$SingleStateMethodSubject() threw an exception. Marking store is misconfigured maybe.", exception.getMessage());
    }

    @Test
    void testGetSetMarkingWithMultiState() {
        MultiStateMethodSubject subject = new MultiStateMethodSubject();

        MarkingStoreInterface markingStore = new MethodMarkingStore("status");

        assertFalse(markingStore.isSingleState());

        Marking marking = markingStore.getMarking(subject);

        assertEquals(0, marking.getPlaces().size());

        marking.mark("first_place");
        markingStore.setMarking(subject, marking);

        assertEquals("first_place", subject.getStatus().get(0));
        assertEquals(1, subject.getStatus().size());
        assertEquals(1, marking.getPlaces().size());

        Marking marking2 = markingStore.getMarking(subject);
        assertEquals(marking.getPlaces(), marking2.getPlaces());
    }

    @Test
    void testGetSetMarkingWithMultiStateAndEnum() {
        MultiStateMethodEnumSubject subject = new MultiStateMethodEnumSubject();

        MarkingStoreInterface markingStore = new MethodMarkingStore("status");

        Marking marking = markingStore.getMarking(subject);

        assertEquals(0, marking.getPlaces().size());

        marking.mark("FIRST_PLACE");
        markingStore.setMarking(subject, marking);

        assertEquals(status.FIRST_PLACE.toString(), subject.getStatus().get(0).toString());
        assertEquals(1, subject.getStatus().size());
        assertEquals(1, marking.getPlaces().size());

        Marking marking2 = markingStore.getMarking(subject);
        assertEquals(marking.getPlaces(), marking2.getPlaces());
    }

    @Test
    void testGetMarkingWithMultiStateWithNonExistentProperty() {
        MultiStateMethodSubject subject = new MultiStateMethodSubject();

        MarkingStoreInterface markingStore = new MethodMarkingStore(false, "nonexistent");

        Throwable exception = assertThrows(MethodNotFoundInClassException.class, () -> {
            markingStore.getMarking(subject);
        });
        assertEquals("The method getNonexistent::com.isfett.workflow.WorkflowTestHelper$MultiStateMethodSubject() does not exist.", exception.getMessage());
    }

    @Test
    void testSetMarkingWithMultiStateWithNonExistentProperty() {
        MultiStateMethodSubject subject = new MultiStateMethodSubject();

        MarkingStoreInterface markingStore = new MethodMarkingStore(false, "nonexistent");

        Marking marking = new Marking(List.of("first_place"));

        marking.mark("first_place");

        Throwable exception = assertThrows(MethodNotFoundInClassException.class, () -> {
            markingStore.setMarking(subject, marking);
        });
        assertEquals("The method setNonexistent::com.isfett.workflow.WorkflowTestHelper$MultiStateMethodSubject() does not exist.", exception.getMessage());
    }


    @Test
    void testGetMarkingWithMultiStateWithWrongMarkingStoreConfiguration() {
        MultiStateMethodSubject subject = new MultiStateMethodSubject();
        subject.setStatus(List.of("draft"));

        MarkingStoreInterface markingStore = new MethodMarkingStore(true, "status");

        Throwable exception = assertThrows(InvalidMethodMarkingStoreConfigurationException.class, () -> {
            markingStore.getMarking(subject);
        });
        assertEquals("The method getStatus::com.isfett.workflow.WorkflowTestHelper$MultiStateMethodSubject() threw an exception. Marking store is misconfigured maybe.", exception.getMessage());
    }

    @Test
    void testSetMarkingWithMultiStateWithWrongMarkingStoreConfiguration() {
        MultiStateMethodSubject subject = new MultiStateMethodSubject();

        MarkingStoreInterface markingStore = new MethodMarkingStore(true, "status");

        Marking marking = markingStore.getMarking(subject);

        Throwable exception = assertThrows(InvalidMethodMarkingStoreConfigurationException.class, () -> {
            markingStore.setMarking(subject, marking);
        });
        assertEquals("The method setStatus::com.isfett.workflow.WorkflowTestHelper$MultiStateMethodSubject() threw an exception. Marking store is misconfigured maybe.", exception.getMessage());
    }

}