package com.isfett.workflow;

import javax.validation.constraints.NotNull;
import java.util.HashMap;
import java.util.Map;

final public class TransitionBlocker {
    private final String message;
    private final String code;
    private final Map<String, String> parameters;

    public TransitionBlocker(@NotNull String message, @NotNull String code, @NotNull Map<String, String> parameters) {
        this.message = message;
        this.code = code;
        this.parameters = parameters;
    }

    public static @NotNull TransitionBlocker createBlockedByMarking(@NotNull Marking marking) {
        Map<String, String> parameters = new HashMap<>();
        parameters.put("marking", marking.toString());
        return new TransitionBlocker("The marking does not enable the transition.", "blocked_by_marking", parameters);
    }

    public static @NotNull TransitionBlocker createUnknown(String message) {
        return new TransitionBlocker(message, "blocked_by_unknown", new HashMap<>());
    }

    public static @NotNull TransitionBlocker createUnknown() {
        return new TransitionBlocker("The transition has been blocked by a guard", "blocked_by_unknown", new HashMap<>());
    }

    public @NotNull String getMessage() {
        return message;
    }

    public @NotNull String getCode() {
        return code;
    }

    public @NotNull Map<String, String> getParameters() {
        return parameters;
    }
}
