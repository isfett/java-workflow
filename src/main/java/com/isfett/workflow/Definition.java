package com.isfett.workflow;

import com.isfett.workflow.exception.PlaceForTransitionNotFoundException;
import com.isfett.workflow.exception.PlaceNotFoundForInitialPlaceException;
import org.hibernate.validator.constraints.NotEmpty;

import javax.validation.constraints.NotNull;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Definition {
    private final Map<String, String> places = new HashMap<>();
    private final List<Transition> transitions = new ArrayList<>();
    private Map<Integer, String> initialPlaces = new HashMap<>();

    public Definition(@NotNull List<String> places, @NotNull List<Transition> transitions,
        @NotNull List<String> initialPlaces) {
        places.forEach(this::addPlace);

        transitions.forEach(this::addTransition);

        if (!initialPlaces.isEmpty() && !this.initialPlaces.isEmpty()) {
            this.initialPlaces = new HashMap<>();
        }
        this.setInitialPlaces(initialPlaces);
    }

    public @NotEmpty Map<String, String> getPlaces() {
        return places;
    }

    public @NotEmpty Map<Integer, String> getInitialPlaces() {
        return initialPlaces;
    }

    private void setInitialPlaces(@NotNull List<String> initialPlaces) {
        if (initialPlaces.isEmpty()) {
            return;
        }

        initialPlaces.forEach((String initialPlace) -> {
            if (!this.places.containsKey(initialPlace)) {
                throw new PlaceNotFoundForInitialPlaceException(initialPlace);
            }
            if (!this.initialPlaces.containsValue(initialPlace)) {
                this.initialPlaces.put(this.initialPlaces.size(), initialPlace);
            }
        });
    }

    public @NotEmpty List<Transition> getTransitions() {
        return transitions;
    }

    private void addPlace(@NotNull String place) {
        if (this.places.size() == 0) {
            this.initialPlaces.put(this.initialPlaces.size(), place);
        }

        this.places.put(place, place);
    }

    private void addTransition(@NotNull Transition transition) {
        transition.getFroms().forEach((String from) -> {
            if (!this.places.containsKey(from)) {
                throw new PlaceForTransitionNotFoundException(from, transition);
            }
        });

        transition.getTos().forEach((String to) -> {
            if (!this.places.containsKey(to)) {
                throw new PlaceForTransitionNotFoundException(to, transition);
            }
        });

        this.transitions.add(transition);
    }
}
