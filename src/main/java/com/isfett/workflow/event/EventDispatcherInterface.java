package com.isfett.workflow.event;

import java.util.List;
import javax.validation.constraints.NotNull;
import org.hibernate.validator.constraints.NotEmpty;

public interface EventDispatcherInterface {
    void addListener(@NotNull EventListenerInterface eventListener, @NotEmpty List<String> subscribedEventNames);

    void dispatch(AbstractWorkflowEvent event, String eventName);
}
