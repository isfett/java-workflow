package com.isfett.workflow.markingstore;

import com.isfett.workflow.Marking;
import com.isfett.workflow.WorkflowTestHelper.MultiStatePropertyEnumSubject;
import com.isfett.workflow.WorkflowTestHelper.MultiStatePropertySubject;
import com.isfett.workflow.WorkflowTestHelper.SingleStateMethodEnumSubject.status;
import com.isfett.workflow.WorkflowTestHelper.SingleStatePropertyEnumSubject;
import com.isfett.workflow.WorkflowTestHelper.SingleStatePropertySubject;
import com.isfett.workflow.exception.InvalidPropertyMarkingStoreConfigurationException;
import com.isfett.workflow.exception.PropertyNotFoundInClassException;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class PropertyMarkingStoreTest {
    @Test
    void testGetSetMarkingWithSingleState() {
        SingleStatePropertySubject subject = new SingleStatePropertySubject();

        MarkingStoreInterface markingStore = new PropertyMarkingStore(true, "status");

        assertTrue(markingStore.isSingleState());

        Marking marking = markingStore.getMarking(subject);

        assertEquals(0, marking.getPlaces().size());

        marking.mark("first_place");
        markingStore.setMarking(subject, marking);

        assertEquals("first_place", subject.status);
        assertEquals(1, marking.getPlaces().size());

        Marking marking2 = markingStore.getMarking(subject);
        assertEquals(marking.getPlaces(), marking2.getPlaces());
    }

    @Test
    void testGetSetMarkingWithSingleStateAndEnum() {
        SingleStatePropertyEnumSubject subject = new SingleStatePropertyEnumSubject();

        MarkingStoreInterface markingStore = new PropertyMarkingStore(true, "status");

        Marking marking = markingStore.getMarking(subject);

        assertEquals(0, marking.getPlaces().size());

        marking.mark("FIRST_PLACE");
        markingStore.setMarking(subject, marking);

        assertEquals(status.FIRST_PLACE.toString(), subject.status.toString());
        assertEquals(1, marking.getPlaces().size());

        Marking marking2 = markingStore.getMarking(subject);
        assertEquals(marking.getPlaces(), marking2.getPlaces());
    }

    @Test
    void testGetMarkingWithSingleStateWithNonExistentProperty() {
        SingleStatePropertySubject subject = new SingleStatePropertySubject();

        MarkingStoreInterface markingStore = new PropertyMarkingStore(true, "nonexistent");

        Throwable exception = assertThrows(PropertyNotFoundInClassException.class, () -> {
            markingStore.getMarking(subject);
        });
        assertEquals("The property nonexistent in com.isfett.workflow.WorkflowTestHelper$SingleStatePropertySubject() does not exist or is not public.", exception.getMessage());
    }

    @Test
    void testSetMarkingWithSingleStateWithNonExistentProperty() {
        SingleStatePropertySubject subject = new SingleStatePropertySubject();

        MarkingStoreInterface markingStore = new PropertyMarkingStore(true, "nonexistent");

        Marking marking = new Marking(List.of("first_place"));

        marking.mark("first_place");

        Throwable exception = assertThrows(PropertyNotFoundInClassException.class, () -> {
            markingStore.setMarking(subject, marking);
        });
        assertEquals("The property nonexistent in com.isfett.workflow.WorkflowTestHelper$SingleStatePropertySubject() does not exist or is not public.", exception.getMessage());
    }

    @Test
    void testGetMarkingWithSingleStateWithWrongMarkingStoreConfiguration() {
        SingleStatePropertySubject subject = new SingleStatePropertySubject();
        subject.status = "draft";

        MarkingStoreInterface markingStore = new PropertyMarkingStore(false, "status");

        assertFalse(markingStore.isSingleState());

        Throwable exception = assertThrows(InvalidPropertyMarkingStoreConfigurationException.class, () -> {
            markingStore.getMarking(subject);
        });
        assertEquals("The property status in com.isfett.workflow.WorkflowTestHelper$SingleStatePropertySubject() threw an exception. Marking store is misconfigured maybe.", exception.getMessage());
    }

    @Test
    void testSetMarkingWithSingleStateWithWrongMarkingStoreConfiguration() {
        SingleStatePropertySubject subject = new SingleStatePropertySubject();

        MarkingStoreInterface markingStore = new PropertyMarkingStore(false, "status");

        Marking marking = new Marking(List.of("first_place"));

        Throwable exception = assertThrows(InvalidPropertyMarkingStoreConfigurationException.class, () -> {
            markingStore.setMarking(subject, marking);
        });
        assertEquals("The property status in com.isfett.workflow.WorkflowTestHelper$SingleStatePropertySubject() threw an exception. Marking store is misconfigured maybe.", exception.getMessage());
    }

    @Test
    void testGetSetMarkingWithMultiState() {
        MultiStatePropertySubject subject = new MultiStatePropertySubject();

        MarkingStoreInterface markingStore = new PropertyMarkingStore("status");

        Marking marking = markingStore.getMarking(subject);

        assertEquals(0, marking.getPlaces().size());

        marking.mark("first_place");
        markingStore.setMarking(subject, marking);

        assertEquals("first_place", subject.status.get(0));
        assertEquals(1, subject.status.size());
        assertEquals(1, marking.getPlaces().size());

        Marking marking2 = markingStore.getMarking(subject);
        assertEquals(marking.getPlaces(), marking2.getPlaces());
    }

    @Test
    void testGetSetMarkingWithMultiStateAndEnum() {
        MultiStatePropertyEnumSubject subject = new MultiStatePropertyEnumSubject();

        MarkingStoreInterface markingStore = new PropertyMarkingStore("status");

        Marking marking = markingStore.getMarking(subject);

        assertEquals(0, marking.getPlaces().size());

        marking.mark("FIRST_PLACE");
        markingStore.setMarking(subject, marking);

        assertEquals(status.FIRST_PLACE.toString(), subject.status.get(0).toString());
        assertEquals(1, subject.status.size());
        assertEquals(1, marking.getPlaces().size());

        Marking marking2 = markingStore.getMarking(subject);
        assertEquals(marking.getPlaces(), marking2.getPlaces());
    }

    @Test
    void testGetMarkingWithMultiStateWithNonExistentProperty() {
        MultiStatePropertySubject subject = new MultiStatePropertySubject();

        MarkingStoreInterface markingStore = new PropertyMarkingStore(false, "nonexistent");

        Throwable exception = assertThrows(PropertyNotFoundInClassException.class, () -> {
            markingStore.getMarking(subject);
        });
        assertEquals("The property nonexistent in com.isfett.workflow.WorkflowTestHelper$MultiStatePropertySubject() does not exist or is not public.", exception.getMessage());
    }

    @Test
    void testSetMarkingWithMultiStateWithNonExistentProperty() {
        MultiStatePropertySubject subject = new MultiStatePropertySubject();

        MarkingStoreInterface markingStore = new PropertyMarkingStore(false, "nonexistent");

        Marking marking = new Marking(List.of("first_place"));

        marking.mark("first_place");

        Throwable exception = assertThrows(PropertyNotFoundInClassException.class, () -> {
            markingStore.setMarking(subject, marking);
        });
        assertEquals("The property nonexistent in com.isfett.workflow.WorkflowTestHelper$MultiStatePropertySubject() does not exist or is not public.", exception.getMessage());
    }


    @Test
    void testGetMarkingWithMultiStateWithWrongMarkingStoreConfiguration() {
        MultiStatePropertySubject subject = new MultiStatePropertySubject();
        subject.status = List.of("draft");

        MarkingStoreInterface markingStore = new PropertyMarkingStore(true, "status");

        Throwable exception = assertThrows(InvalidPropertyMarkingStoreConfigurationException.class, () -> {
            markingStore.getMarking(subject);
        });
        assertEquals("The property status in com.isfett.workflow.WorkflowTestHelper$MultiStatePropertySubject() threw an exception. Marking store is misconfigured maybe.", exception.getMessage());
    }

    @Test
    void testSetMarkingWithMultiStateWithWrongMarkingStoreConfiguration() {
        MultiStatePropertySubject subject = new MultiStatePropertySubject();

        MarkingStoreInterface markingStore = new PropertyMarkingStore(true, "status");

        Marking marking = new Marking(List.of("first_place"));

        Throwable exception = assertThrows(InvalidPropertyMarkingStoreConfigurationException.class, () -> {
            markingStore.setMarking(subject, marking);
        });
        assertEquals("The property status in com.isfett.workflow.WorkflowTestHelper$MultiStatePropertySubject() threw an exception. Marking store is misconfigured maybe.", exception.getMessage());
    }

}