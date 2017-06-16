package com.bambooplugin.impl;

import com.atlassian.bamboo.build.logger.BuildLogger;
import com.atlassian.bamboo.deployments.projects.service.DeploymentProjectService;
import com.atlassian.bamboo.plan.PlanKey;
import com.atlassian.bamboo.task.*;
import com.atlassian.plugin.spring.scanner.annotation.component.Scanned;
import com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport;
import org.springframework.beans.factory.annotation.Autowired;

@Scanned
public class MyFirstTask implements TaskType {

    @ComponentImport
    private final DeploymentProjectService deploymentProjectService;

    @Autowired
    public MyFirstTask(@ComponentImport DeploymentProjectService deploymentProjectService) {
        this.deploymentProjectService = deploymentProjectService;
    }

    @Override
    public TaskResult execute(final TaskContext taskContext) throws TaskException
    {

        final BuildLogger buildLogger = taskContext.getBuildLogger();

        PlanKey parentPlanKey = taskContext.getBuildContext().getParentBuildContext().getTypedPlanKey();

        buildLogger.addBuildLogEntry("Parent plan key is " + parentPlanKey);

        buildLogger.addBuildLogEntry("Related Plan keys " + deploymentProjectService.getDeploymentProjectsRelatedToPlan(parentPlanKey));

        return TaskResultBuilder.newBuilder(taskContext).success().build();
    }
}
