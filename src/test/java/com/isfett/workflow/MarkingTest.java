package com.isfett.workflow;

import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class MarkingTest {
    @Test
    void testMarking() {
        List<String> places = new ArrayList<>();
        places.add("a");

        Marking marking = new Marking(places);

        assertTrue(marking.has("a"));
        assertFalse(marking.has("b"));
        assertEquals(1, marking.getPlaces().size());

        marking.mark("b");

        assertTrue(marking.has("a"));
        assertTrue(marking.has("b"));
        assertEquals(2, marking.getPlaces().size());

        marking.unmark("a");

        assertFalse(marking.has("a"));
        assertTrue(marking.has("b"));
        assertEquals(1, marking.getPlaces().size());

        marking.unmark("b");

        assertFalse(marking.has("a"));
        assertFalse(marking.has("b"));
        assertEquals(0, marking.getPlaces().size());
    }
}