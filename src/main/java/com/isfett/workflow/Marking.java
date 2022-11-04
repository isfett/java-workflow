package com.isfett.workflow;

import org.hibernate.validator.constraints.NotEmpty;

import javax.validation.constraints.NotNull;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Marking {
    private final Map<String, Integer> places = new HashMap<>();

    public Marking(@NotEmpty List<String> places) {
        places.forEach(this::mark);
    }

    public void mark(@NotNull String place) {
        this.places.put(place, 1);
    }

    public void unmark(@NotNull String place) {
        this.places.remove(place);
    }

    public @NotNull Boolean has(@NotNull String place) {
        return this.places.containsKey(place);
    }

    public @NotEmpty List<String> getPlaces() {
        return new ArrayList<>(this.places.keySet());
    }
}
