package com.isfett.workflow.supportstrategy;

import com.isfett.workflow.WorkflowInterface;

final public class InstanceOfSupportStrategy implements SupportStrategyInterface {
    private final Class<?> className;

    public InstanceOfSupportStrategy(Class<?> className) {
        this.className = className;
    }

    public Boolean supports(WorkflowInterface workflowInterface, Object subject) {
        return this.className.toString().equals(subject.getClass().toString());
    }
}
