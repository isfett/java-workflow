package com.isfett.workflow;

import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class DefinitionBuilderTest {
    @Test
    void testSetInitialPlaces() {
        List<String> places = new ArrayList<>();
        for (char c = 'a'; c <= 'e'; c++) {
            places.add(String.valueOf(c));
        }
        DefinitionBuilder builder = new DefinitionBuilder(places);
        builder.setInitialPlaces(List.of("b"));
        Definition definition = builder.build();

        assertEquals(Map.of(0, "b"), definition.getInitialPlaces());
    }

    @Test
    void testAddTransition() {
        List<String> places = new ArrayList<>();
        for (char c = 'a'; c <= 'e'; c++) {
            places.add(String.valueOf(c));
        }
        List<Transition> transitions = new ArrayList<>();
        Transition transition0 = new Transition("name0", List.of(places.get(0)), List.of(places.get(1)));
        transitions.add(transition0);
        DefinitionBuilder builder = new DefinitionBuilder(places, transitions);
        Transition transition1 = new Transition("name1", List.of(places.get(0)), List.of(places.get(1)));
        builder.addTransition(transition1);

        Definition definition = builder.build();

        assertEquals(2, definition.getTransitions().size());
        assertSame(transition0, definition.getTransitions().get(0));
        assertSame(transition1, definition.getTransitions().get(1));
    }

    @Test
    void testAddTransitions() {
        List<String> places = new ArrayList<>();
        for (char c = 'a'; c <= 'e'; c++) {
            places.add(String.valueOf(c));
        }
        List<Transition> transitions = new ArrayList<>();
        Transition transition0 = new Transition("name0", List.of(places.get(0)), List.of(places.get(1)));
        Transition transition1 = new Transition("name1", List.of(places.get(0)), List.of(places.get(1)));
        transitions.add(transition0);
        transitions.add(transition1);
        DefinitionBuilder builder = new DefinitionBuilder(places);
        builder.addTransitions(transitions);

        Definition definition = builder.build();

        assertEquals(2, definition.getTransitions().size());
        assertSame(transition0, definition.getTransitions().get(0));
        assertSame(transition1, definition.getTransitions().get(1));
    }

    @Test
    void testAddPlace() {
        DefinitionBuilder builder = new DefinitionBuilder();
        for (char c = 'a'; c <= 'e'; c++) {
            builder.addPlace(String.valueOf(c));
        }

        Definition definition = builder.build();

        assertEquals(5, definition.getPlaces().size());
        assertTrue(definition.getPlaces().values().containsAll(List.of("a", "b", "c", "d", "e")));
    }

    @Test
    void testAddPlaces() {
        List<String> places = new ArrayList<>();
        for (char c = 'a'; c <= 'e'; c++) {
            places.add(String.valueOf(c));
        }
        DefinitionBuilder builder = new DefinitionBuilder();
        builder.addPlaces(places);

        Definition definition = builder.build();

        assertEquals(5, definition.getPlaces().size());
        assertTrue(definition.getPlaces().values().containsAll(List.of("a", "b", "c", "d", "e")));
    }

    @Test
    void testAddPlacesFromEnumValues() {
        enum placesEnum {
            A,
            B,
            C,
            D,
            E
        }

        DefinitionBuilder builder = new DefinitionBuilder();
        builder.addPlacesFromEnumValues(placesEnum.values());

        Definition definition = builder.build();

        assertEquals(5, definition.getPlaces().size());
        assertTrue(definition.getPlaces().values().containsAll(List.of("A", "B", "C", "D", "E")));
    }

    @Test
    void testClear() {
        List<String> places = new ArrayList<>();
        for (char c = 'a'; c <= 'e'; c++) {
            places.add(String.valueOf(c));
        }
        DefinitionBuilder builder = new DefinitionBuilder();
        builder.addPlaces(places);

        Definition definition = builder.build();

        assertEquals(5, definition.getPlaces().size());
        assertTrue(definition.getPlaces().values().containsAll(List.of("a", "b", "c", "d", "e")));

        builder.clear();
        builder.addPlace("a");

        definition = builder.build();
        assertEquals(1, definition.getPlaces().size());
    }
}