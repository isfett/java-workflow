package com.isfett.workflow.exception;

import javax.validation.constraints.NotNull;

public class PlaceNotFoundForInitialPlaceException extends IllegalArgumentException {
    public PlaceNotFoundForInitialPlaceException(@NotNull String place) {
        super(getMessage(place));
    }

    private static @NotNull String getMessage(@NotNull String place) {
        return "Place " + place + " cannot be the initial place as it does not exist.";
    }
}
