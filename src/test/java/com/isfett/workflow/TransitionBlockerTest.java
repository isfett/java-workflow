package com.isfett.workflow;

import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class TransitionBlockerTest {
    @Test
    void testCreateBlockedByMarking() {
        Marking marking = new Marking(List.of("a"));
        TransitionBlocker transitionBlocker = TransitionBlocker.createBlockedByMarking(marking);

        assertEquals("The marking does not enable the transition.", transitionBlocker.getMessage());
        assertEquals("blocked_by_marking", transitionBlocker.getCode());
        assertEquals(Map.of("marking", marking.toString()), transitionBlocker.getParameters());
    }
}