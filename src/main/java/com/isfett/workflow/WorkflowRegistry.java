package com.isfett.workflow;

import com.isfett.workflow.exception.NoWorkflowFoundInRegistryException;
import com.isfett.workflow.exception.TooManyWorkflowsFoundInRegistryException;
import com.isfett.workflow.supportstrategy.SupportStrategyInterface;

import javax.validation.constraints.NotNull;
import java.util.*;

public class WorkflowRegistry {
    private final Map<SupportStrategyInterface, WorkflowInterface> workflows = new HashMap<>();

    public void addWorkflow(@NotNull WorkflowInterface workflow, @NotNull SupportStrategyInterface supportStrategy) {
        this.workflows.put(supportStrategy, workflow);
    }

    public @NotNull Boolean has(@NotNull Object subject) {
        for (SupportStrategyInterface supportStrategy : this.workflows.keySet()) {
            WorkflowInterface workflow = this.workflows.get(supportStrategy);
            if (this.supports(workflow, supportStrategy, subject)) {
                return true;
            }
        }

        return false;
    }

    public @NotNull Boolean has(@NotNull Object subject, @NotNull String workflowName) {
        for (SupportStrategyInterface supportStrategy : this.workflows.keySet()) {
            WorkflowInterface workflow = this.workflows.get(supportStrategy);
            if (this.supports(workflow, supportStrategy, subject, workflowName)) {
                return true;
            }
        }

        return false;
    }

    public @NotNull WorkflowInterface get(@NotNull Object subject) {
        List<WorkflowInterface> supportedWorkflows = this.all(subject);

        this.checkSupportedWorkflowsLength(supportedWorkflows, subject, true);

        return supportedWorkflows.get(0);
    }

    public @NotNull WorkflowInterface get(@NotNull Object subject, @NotNull String workflowName) {
        List<WorkflowInterface> supportedWorkflows = this.all(subject, workflowName);

        this.checkSupportedWorkflowsLength(supportedWorkflows, subject, false);

        return supportedWorkflows.get(0);
    }

    public @NotNull List<WorkflowInterface> all(@NotNull Object subject) {
        List<WorkflowInterface> supportedWorkflows = new ArrayList<>();

        for (SupportStrategyInterface supportStrategy : this.workflows.keySet()) {
            WorkflowInterface workflow = this.workflows.get(supportStrategy);
            if (this.supports(workflow, supportStrategy, subject)) {
                supportedWorkflows.add(workflow);
            }
        }

        return supportedWorkflows.stream().sorted(Comparator.comparing(WorkflowInterface::getName)).toList();
    }

    public @NotNull List<WorkflowInterface> all(@NotNull Object subject, @NotNull String workflowName) {
        List<WorkflowInterface> supportedWorkflows = new ArrayList<>();

        for (SupportStrategyInterface supportStrategy : this.workflows.keySet()) {
            WorkflowInterface workflow = this.workflows.get(supportStrategy);
            if (this.supports(workflow, supportStrategy, subject, workflowName)) {
                supportedWorkflows.add(workflow);
            }
        }

        return supportedWorkflows.stream().sorted(Comparator.comparing(WorkflowInterface::getName)).toList();
    }

    private @NotNull Boolean supports(@NotNull WorkflowInterface workflow, @NotNull SupportStrategyInterface supportStrategy, @NotNull Object subject) {
        return supportStrategy.supports(workflow, subject);
    }

    private @NotNull Boolean supports(@NotNull WorkflowInterface workflow, @NotNull SupportStrategyInterface supportStrategy, @NotNull Object subject, @NotNull String workflowName) {
        if (!workflowName.equals(workflow.getName())) {
            return false;
        }

        return this.supports(workflow, supportStrategy, subject);
    }

    private void checkSupportedWorkflowsLength(@NotNull List<WorkflowInterface> supportedWorkflows, @NotNull Object subject, @NotNull Boolean checkForMoreThanOne) {
        if (supportedWorkflows.isEmpty()) {
            throw new NoWorkflowFoundInRegistryException(subject.getClass().getSimpleName());
        }

        if (supportedWorkflows.size() > 1 && checkForMoreThanOne) {
            List<String> workflowNames = new ArrayList<>();
            for (WorkflowInterface workflow : supportedWorkflows) {
                workflowNames.add(workflow.getName());
            }
            throw new TooManyWorkflowsFoundInRegistryException(workflowNames,
                subject.getClass().getSimpleName());
        }
    }
}
