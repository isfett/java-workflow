package com.isfett.workflow.event;

import com.isfett.workflow.exception.EventCanNotBeDispatchedException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.*;

public class EventDispatcher implements EventDispatcherInterface {
    private final Map<EventListenerInterface, List<String>> eventListeners = new HashMap<>();

    public void addListener(EventListenerInterface eventListener, List<String> subscribedEventNames) {
        this.eventListeners.put(eventListener, subscribedEventNames);
    }

    public void dispatch(AbstractWorkflowEvent event, String eventName) {
        this.eventListeners.forEach(
            (EventListenerInterface eventListener, List<String> subscribedEventNames) -> {
                if (subscribedEventNames.contains(eventName) || subscribedEventNames.isEmpty()) {
                    Method foundMethod = null;
                    String methodName = "on" + event.getClass().getSimpleName().replace("Event", "");
                    for (Method method : eventListener.getClass().getDeclaredMethods()) {
                        if (method.getName().equals(methodName)) {
                            foundMethod = method;
                            break;
                        }
                    }
                    if (null == foundMethod) {
                        throw new EventCanNotBeDispatchedException(eventName, methodName, eventListener.getClass().getSimpleName());
                    }

                    try {
                        foundMethod.invoke(eventListener, event, eventName);
                    } catch (IllegalAccessException | InvocationTargetException exception) {
                        throw new EventCanNotBeDispatchedException(eventName, methodName, eventListener.getClass().getSimpleName(), exception);
                    }
                }
            }
        );
    }
}
