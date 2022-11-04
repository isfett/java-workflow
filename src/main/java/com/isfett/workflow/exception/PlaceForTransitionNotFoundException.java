package com.isfett.workflow.exception;

import com.isfett.workflow.Transition;

import javax.validation.constraints.NotNull;

public class PlaceForTransitionNotFoundException extends IllegalArgumentException {
    public PlaceForTransitionNotFoundException(@NotNull String place, @NotNull Transition transition) {
        super(getMessage(place, transition));
    }

    private static @NotNull String getMessage(@NotNull String place, @NotNull Transition transition) {
        return "Place " + place + " referenced in transition " + transition.getName() + " does not exist.";
    }
}
