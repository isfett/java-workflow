package com.isfett.workflow;

import com.isfett.workflow.exception.NoWorkflowFoundInRegistryException;
import com.isfett.workflow.exception.TooManyWorkflowsFoundInRegistryException;
import com.isfett.workflow.supportstrategy.SupportStrategyInterface;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class WorkflowRegistryTest {
    private WorkflowRegistry workflowRegistry;

    @BeforeEach
    void setup() {
        this.workflowRegistry = new WorkflowRegistry();

        SupportStrategyInterface strategy1 = mock(SupportStrategyInterface.class);
        lenient().when(strategy1.supports(any(WorkflowInterface.class), any(Subject1.class))).thenReturn(true);
        lenient().when(strategy1.supports(any(WorkflowInterface.class), any(Subject2.class))).thenReturn(false);

        SupportStrategyInterface strategy2 = mock(SupportStrategyInterface.class);
        lenient().when(strategy2.supports(any(WorkflowInterface.class), any(Subject1.class))).thenReturn(false);
        lenient().when(strategy2.supports(any(WorkflowInterface.class), any(Subject2.class))).thenReturn(true);

        SupportStrategyInterface strategy3 = mock(SupportStrategyInterface.class);
        lenient().when(strategy3.supports(any(WorkflowInterface.class), any(Subject1.class))).thenReturn(false);
        lenient().when(strategy3.supports(any(WorkflowInterface.class), any(Subject2.class))).thenReturn(true);

        WorkflowInterface workflow1 = mock(WorkflowInterface.class);
        lenient().when(workflow1.getName()).thenReturn("workflow_1");

        WorkflowInterface workflow2 = mock(WorkflowInterface.class);
        lenient().when(workflow2.getName()).thenReturn("workflow_2");

        WorkflowInterface workflow3 = mock(WorkflowInterface.class);
        lenient().when(workflow3.getName()).thenReturn("workflow_3");

        this.workflowRegistry.addWorkflow(workflow1, strategy1);
        this.workflowRegistry.addWorkflow(workflow2, strategy2);
        this.workflowRegistry.addWorkflow(workflow3, strategy3);
    }

    @Test
    void testHasWithMatch() {
        assertTrue(this.workflowRegistry.has(new Subject1()));
    }

    @Test
    void testHasWithMatchWithWorkflowName() {
        assertTrue(this.workflowRegistry.has(new Subject1(), "workflow_1"));
    }

    @Test
    void testHasWithoutMatch() {
        assertFalse(this.workflowRegistry.has(new Subject3()));
    }

    @Test
    void testHasWithoutMatchWithWorkflowName() {
        assertFalse(this.workflowRegistry.has(new Subject1(), "not_existent_workflow_name"));
    }

    @Test
    void testGet() {
        WorkflowInterface workflow = this.workflowRegistry.get(new Subject1());
        assertEquals("workflow_1", workflow.getName());

        workflow = this.workflowRegistry.get(new Subject1(), "workflow_1");
        assertEquals("workflow_1", workflow.getName());

        workflow = this.workflowRegistry.get(new Subject2(), "workflow_2");
        assertEquals("workflow_2", workflow.getName());
    }

    @Test
    void testGetWithMultipleMatch() {
        Throwable exception = assertThrows(TooManyWorkflowsFoundInRegistryException.class, () -> this.workflowRegistry.get(new Subject2()));
        assertEquals("Too many workflows (workflow_2, workflow_3) match this subject (Subject2); set a different name on each and use the second (name).", exception.getMessage());
    }

    @Test
    void testGetWithMultipleMatchWithWorkflowName() {
        WorkflowInterface workflow4 = mock(WorkflowInterface.class);
        lenient().when(workflow4.getName()).thenReturn("workflow_2");

        SupportStrategyInterface strategy4 = mock(SupportStrategyInterface.class);
        lenient().when(strategy4.supports(any(WorkflowInterface.class), any(Subject1.class))).thenReturn(false);
        lenient().when(strategy4.supports(any(WorkflowInterface.class), any(Subject2.class))).thenReturn(true);

        this.workflowRegistry.addWorkflow(workflow4, strategy4);
        WorkflowInterface workflow = this.workflowRegistry.get(new Subject2(), "workflow_2");
        assertEquals("workflow_2", workflow.getName());
    }

    @Test
    void testGetWithNoMatch() {
        Throwable exception = assertThrows(NoWorkflowFoundInRegistryException.class, () -> this.workflowRegistry.get(new Object()));
        assertEquals("Unable to find a workflow for class Object.", exception.getMessage());
    }

    @Test
    void testAllWithOneMatch() {
        List<WorkflowInterface> workflows = this.workflowRegistry.all(new Subject1());
        assertEquals(1, workflows.size());
        assertEquals("workflow_1", workflows.get(0).getName());
    }

    @Test
    void testAllWithMultipleMatch() {
        List<WorkflowInterface> workflows = this.workflowRegistry.all(new Subject2());
        assertEquals(2, workflows.size());
        assertEquals("workflow_2", workflows.get(0).getName());
        assertEquals("workflow_3", workflows.get(1).getName());
    }

    @Test
    void testAllWithNoMatch() {
        List<WorkflowInterface> workflows = this.workflowRegistry.all(new Object());
        assertEquals(0, workflows.size());
    }

    public static class Subject1 {

    }

    public static class Subject2 {

    }

    public static class Subject3 {

    }
}