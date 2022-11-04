package com.isfett.workflow;

import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class TransitionTest {
    @Test
    void testConstructor() {
        Transition transition = new Transition("name", List.of("a"), List.of("b"));

        assertEquals("name", transition.getName());
        assertEquals(List.of("a"), transition.getFroms());
        assertEquals(List.of("b"), transition.getTos());
    }
}