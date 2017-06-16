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

        final String say = taskContext.getConfigurationMap().get("say");

        PlanKey planKey = taskContext.getBuildContext().getTypedPlanKey();

        buildLogger.addBuildLogEntry(deploymentProjectService.getAllDeploymentProjects().get(0).toString());
        buildLogger.addBuildLogEntry(deploymentProjectService.getAllDeploymentProjects().get(0).getEnvironments().toString());

        buildLogger.addBuildLogEntry(deploymentProjectService.getAllDeploymentProjects().toString());

        buildLogger.addBuildLogEntry("Hello, World! " + say + " " + deploymentProjectService.getDeploymentProjectsRelatedToPlan(planKey));

        return TaskResultBuilder.newBuilder(taskContext).success().build();
    }
}
