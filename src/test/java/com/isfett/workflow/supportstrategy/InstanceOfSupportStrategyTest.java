package com.isfett.workflow.supportstrategy;

import com.isfett.workflow.WorkflowInterface;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
public class InstanceOfSupportStrategyTest {
    @Mock
    WorkflowInterface workflow;

    @Test
    void testSupportsIfClassInstance() {
        SupportStrategyInterface strategy = new InstanceOfSupportStrategy(Object1.class);

        assertTrue(strategy.supports(this.workflow, new Object1()));
    }

    @Test
    void testSupportsIfNotClassInstance() {
        SupportStrategyInterface strategy = new InstanceOfSupportStrategy(Object1.class);

        assertFalse(strategy.supports(this.workflow, new Object2()));
    }

    public static class Object1 {

    }

    public static class Object2 {

    }
}
