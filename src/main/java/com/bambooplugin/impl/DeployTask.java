package com.bambooplugin.impl;

import com.atlassian.bamboo.build.logger.BuildLogger;
import com.atlassian.bamboo.deployments.environments.Environment;
import com.atlassian.bamboo.deployments.execution.triggering.EnvironmentTriggeringAction;
import com.atlassian.bamboo.deployments.execution.triggering.EnvironmentTriggeringActionFactory;
import com.atlassian.bamboo.deployments.projects.DeploymentProject;
import com.atlassian.bamboo.deployments.projects.service.DeploymentProjectService;
import com.atlassian.bamboo.plan.NonBlockingPlanExecutionService;
import com.atlassian.bamboo.plan.PlanResultKey;
import com.atlassian.bamboo.task.*;
import com.atlassian.plugin.spring.scanner.annotation.component.Scanned;
import com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

@Scanned
public class DeployTask implements TaskType {

    @ComponentImport
    private final DeploymentProjectService deploymentProjectService;

    @ComponentImport
    private final EnvironmentTriggeringActionFactory triggeringActionFactory;

    @ComponentImport
    private final NonBlockingPlanExecutionService nonBlockingPlanExecutionService;

    @Autowired
    public DeployTask(@ComponentImport DeploymentProjectService deploymentProjectService,
                      @ComponentImport EnvironmentTriggeringActionFactory triggeringActionFactory,
                      @ComponentImport NonBlockingPlanExecutionService nonBlockingPlanExecutionService)
    {
        this.deploymentProjectService = deploymentProjectService;
        this.triggeringActionFactory = triggeringActionFactory;
        this.nonBlockingPlanExecutionService = nonBlockingPlanExecutionService;
    }

    @Override
    public TaskResult execute(final TaskContext taskContext) throws TaskException
    {

        final BuildLogger buildLogger = taskContext.getBuildLogger();

        try {

            long deploymentProjectId, environmentId;

            deploymentProjectId = Long.parseLong(taskContext.getConfigurationMap().get("deploymentProjectId"));
            environmentId = Long.parseLong(taskContext.getConfigurationMap().get("environmentId"));

            DeploymentProject deploymentProject = deploymentProjectService.getDeploymentProject(deploymentProjectId);
            Environment environment = getMatchingEnvironment(deploymentProject, environmentId);
            PlanResultKey planResultKey = taskContext.getBuildContext().getParentBuildContext().getPlanResultKey();

            buildLogger.addBuildLogEntry("Starting Deployment of " + planResultKey + " to " + deploymentProject.getName() + " / " + environment.getName());

            EnvironmentTriggeringAction environmentTriggeringAction = triggeringActionFactory.createAfterSuccessfulPlanEnvironmentTriggerAction(environment, planResultKey);
            nonBlockingPlanExecutionService.tryToStart(environment, environmentTriggeringAction);

            return TaskResultBuilder.newBuilder(taskContext).success().build();

        }  catch (Exception exception){

            buildLogger.addErrorLogEntry(exception.getMessage());

            return TaskResultBuilder.newBuilder(taskContext).failed().build();
        }

    }

    private Environment getMatchingEnvironment(DeploymentProject deploymentProject, long id) {

        for (Environment environment : deploymentProject.getEnvironments()) {
            if(environment.getId() == id)
                return environment;
        }

        throw new RuntimeException("Unable to find environment id: " + id);
    }

}