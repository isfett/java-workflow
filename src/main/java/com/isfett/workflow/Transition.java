package com.isfett.workflow;

import org.hibernate.validator.constraints.NotEmpty;

import javax.validation.constraints.NotNull;
import java.util.List;

public class Transition {
    private final String name;
    private final List<String> froms;
    private final List<String> tos;

    public Transition(@NotNull String name, @NotEmpty List<String> froms, @NotEmpty List<String> tos) {
        this.name = name;
        this.froms = froms;
        this.tos = tos;
    }

    public @NotNull String getName() {
        return name;
    }

    public @NotEmpty List<String> getFroms() {
        return froms;
    }

    public @NotEmpty List<String> getTos() {
        return tos;
    }
}
