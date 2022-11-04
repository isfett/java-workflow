package com.isfett.workflow;

import java.util.Arrays;
import org.hibernate.validator.constraints.NotEmpty;

import javax.validation.constraints.NotNull;
import java.util.ArrayList;
import java.util.List;

public class DefinitionBuilder {
    private List<String> places = new ArrayList<>();
    private List<Transition> transitions = new ArrayList<>();
    private List<String> initialPlaces = new ArrayList<>();

    public DefinitionBuilder() {
    }

    public DefinitionBuilder(@NotEmpty List<String> places) {
        this.places = places;
    }

    public DefinitionBuilder(@NotEmpty List<String> places, @NotEmpty List<Transition> transitions) {
        this.places = places;
        this.transitions = transitions;
    }

    public @NotNull Definition build() {
        return new Definition(this.places, this.transitions, this.initialPlaces);
    }

    public @NotNull DefinitionBuilder clear() {
        this.places = new ArrayList<>();
        this.transitions = new ArrayList<>();
        this.initialPlaces = new ArrayList<>();

        return this;
    }

    public @NotNull DefinitionBuilder setInitialPlaces(@NotEmpty List<String> initialPlaces) {
        this.initialPlaces = initialPlaces;

        return this;
    }

    public @NotNull DefinitionBuilder addPlace(@NotNull String place) {
        if (this.places.size() == 0) {
            this.setInitialPlaces(List.of(place));
        }

        this.places.add(place);

        return this;
    }

    public @NotNull DefinitionBuilder addPlaces(@NotEmpty List<String> places) {
        for (String place : places) {
            this.addPlace(place);
        }

        return this;
    }

    public @NotNull <T extends Enum<T>> DefinitionBuilder addPlacesFromEnumValues(@NotNull T[] places) {
        for (Enum<T> place: Arrays.stream(places).toList()) {
            this.addPlace(place.toString());
        }

        return this;
    }

    public @NotNull DefinitionBuilder addTransition(@NotNull Transition transition) {
        this.transitions.add(transition);

        return this;
    }

    public @NotNull DefinitionBuilder addTransitions(@NotEmpty List<Transition> transitions) {
        for (Transition transition : transitions) {
            this.addTransition(transition);
        }

        return this;
    }
}
