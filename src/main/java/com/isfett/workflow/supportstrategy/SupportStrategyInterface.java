package com.isfett.workflow.supportstrategy;

import com.isfett.workflow.WorkflowInterface;

public interface SupportStrategyInterface {
    Boolean supports(WorkflowInterface workflow, Object subject);
}
