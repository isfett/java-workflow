package com.isfett.workflow.markingstore;

import com.isfett.workflow.Marking;

import javax.validation.constraints.NotNull;

public interface MarkingStoreInterface {
    @NotNull
    Marking getMarking(@NotNull Object subject);

    void setMarking(@NotNull Object subject, @NotNull Marking marking);

    @NotNull Boolean isSingleState();
}
