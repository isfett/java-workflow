package com.isfett.workflow;

import com.isfett.workflow.exception.PlaceForTransitionNotFoundException;
import com.isfett.workflow.exception.PlaceNotFoundForInitialPlaceException;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class DefinitionTest {
    @Test
    void testAddPlaces() {
        List<String> places = new ArrayList<>();
        for (char c = 'a'; c <= 'e'; c++) {
            places.add(String.valueOf(c));
        }
        List<Transition> transitions = new ArrayList<>();
        List<String> initialPlaces = new ArrayList<>();

        Definition definition = new Definition(places, transitions, initialPlaces);

        assertEquals(5, definition.getPlaces().size());
        assertEquals(Map.of(0, "a"), definition.getInitialPlaces());
    }

    @Test
    void testSetInitialPlace() {
        List<String> places = new ArrayList<>();
        for (char c = 'a'; c <= 'e'; c++) {
            places.add(String.valueOf(c));
        }
        List<Transition> transitions = new ArrayList<>();
        List<String> initialPlaces = new ArrayList<>();
        initialPlaces.add(places.get(3));

        Definition definition = new Definition(places, transitions, initialPlaces);

        assertEquals(Map.of(0, places.get(3)), definition.getInitialPlaces());
    }

    @Test
    void testSetInitialPlaceWhenAlreadyExists() {
        List<String> places = new ArrayList<>();
        for (char c = 'a'; c <= 'e'; c++) {
            places.add(String.valueOf(c));
        }
        List<Transition> transitions = new ArrayList<>();
        List<String> initialPlaces = new ArrayList<>();
        initialPlaces.add(places.get(3));
        initialPlaces.add(places.get(3));

        Definition definition = new Definition(places, transitions, initialPlaces);

        assertEquals(Map.of(0, places.get(3)), definition.getInitialPlaces());
    }

    @Test
    void testSetInitialPlaces() {
        List<String> places = new ArrayList<>();
        for (char c = 'a'; c <= 'e'; c++) {
            places.add(String.valueOf(c));
        }
        List<Transition> transitions = new ArrayList<>();
        List<String> initialPlaces = new ArrayList<>();
        initialPlaces.add("a");
        initialPlaces.add("e");

        Definition definition = new Definition(places, transitions, initialPlaces);

        assertEquals(Map.of(0, "a", 1, "e"), definition.getInitialPlaces());
    }

    @Test
    void testSetInitialPlaceAndPlaceIsNotDefined() {
        List<String> places = new ArrayList<>();
        List<Transition> transitions = new ArrayList<>();
        List<String> initialPlaces = new ArrayList<>();
        initialPlaces.add("a");

        Throwable exception = assertThrows(PlaceNotFoundForInitialPlaceException.class, () -> {
            new Definition(places, transitions, initialPlaces);
        });
        assertEquals("Place a cannot be the initial place as it does not exist.", exception.getMessage());
    }

    @Test
    void testAddTransition() {
        List<String> places = new ArrayList<>();
        for (char c = 'a'; c <= 'e'; c++) {
            places.add(String.valueOf(c));
        }
        List<Transition> transitions = new ArrayList<>();
        Transition transition = new Transition("name", List.of(places.get(0)), List.of(places.get(1)));
        transitions.add(transition);
        List<String> initialPlaces = new ArrayList<>();

        Definition definition = new Definition(places, transitions, initialPlaces);

        assertEquals(1, definition.getTransitions().size());
        assertSame(transition, definition.getTransitions().get(0));
    }


    @Test
    void testAddTransitionFromAPlaceThatIsNotDefined() {
        List<String> places = new ArrayList<>();
        for (char c = 'a'; c <= 'e'; c++) {
            places.add(String.valueOf(c));
        }
        List<Transition> transitions = new ArrayList<>();
        Transition transition = new Transition("name", List.of("z"), List.of(places.get(1)));
        transitions.add(transition);
        List<String> initialPlaces = new ArrayList<>();

        Throwable exception = assertThrows(PlaceForTransitionNotFoundException.class, () -> {
            new Definition(places, transitions, initialPlaces);
        });
        assertEquals("Place z referenced in transition name does not exist.", exception.getMessage());
    }

    @Test
    void testAddTransitionToAPlaceThatIsNotDefined() {
        List<String> places = new ArrayList<>();
        for (char c = 'a'; c <= 'e'; c++) {
            places.add(String.valueOf(c));
        }
        List<Transition> transitions = new ArrayList<>();
        Transition transition = new Transition("name", List.of(places.get(1)), List.of("z"));
        transitions.add(transition);
        List<String> initialPlaces = new ArrayList<>();

        Throwable exception = assertThrows(PlaceForTransitionNotFoundException.class, () -> {
            new Definition(places, transitions, initialPlaces);
        });
        assertEquals("Place z referenced in transition name does not exist.", exception.getMessage());
    }
}