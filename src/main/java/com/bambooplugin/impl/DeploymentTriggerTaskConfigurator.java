package com.bambooplugin.impl;

import com.atlassian.bamboo.collections.ActionParametersMap;
import com.atlassian.bamboo.deployments.environments.Environment;
import com.atlassian.bamboo.deployments.projects.DeploymentProject;
import com.atlassian.bamboo.deployments.projects.service.DeploymentProjectService;
import com.atlassian.bamboo.task.AbstractTaskConfigurator;
import com.atlassian.bamboo.task.TaskDefinition;
import com.atlassian.bamboo.utils.error.ErrorCollection;
import com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport;
import org.apache.commons.lang.StringUtils;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;
import java.util.Map;

public class DeploymentTriggerTaskConfigurator extends AbstractTaskConfigurator {

    @ComponentImport
    private final DeploymentProjectService deploymentProjectService;


    @Autowired
    public DeploymentTriggerTaskConfigurator(@ComponentImport DeploymentProjectService deploymentProjectService) {
        this.deploymentProjectService = deploymentProjectService;
    }

    public Map<String, String> generateTaskConfigMap(@NotNull final ActionParametersMap params, final TaskDefinition previousTaskDefinition)
    {
        final Map<String, String> config = super.generateTaskConfigMap(params, previousTaskDefinition);

        final String deploymentProjectName = params.getString("deploymentProjectName");
        final String environmentName = params.getString("environmentName");

        DeploymentProject deploymentProject = getMatchingDeploymentProject(deploymentProjectName);

        Environment environment = getMatchingEnvironment(deploymentProject, environmentName);

        config.put("deploymentProjectId", Long.toString(deploymentProject.getId()));
        config.put("environmentId", Long.toString(environment.getId()));

        return config;
    }

    public void populateContextForEdit(@NotNull final Map<String, Object> context, @NotNull final TaskDefinition taskDefinition)
    {
        super.populateContextForEdit(context, taskDefinition);

        long deploymentProjectId, environmentId;

        try {
            deploymentProjectId = Long.parseLong(taskDefinition.getConfiguration().get("deploymentProjectId"));
            environmentId = Long.parseLong(taskDefinition.getConfiguration().get("environmentId"));
        } catch (NumberFormatException exception){
           return;
        }

        DeploymentProject deploymentProject = deploymentProjectService.getDeploymentProject(deploymentProjectId);

        Environment environment = getMatchingEnvironment(deploymentProject, environmentId);

        context.put("deploymentProjectName", deploymentProject.getName());
        context.put("environmentName", environment.getName());

    }

    public void validate(@NotNull final ActionParametersMap params, @NotNull final ErrorCollection errorCollection)
    {
        super.validate(params, errorCollection);

        final String deploymentProjectName = params.getString("deploymentProjectName");
        final String environmentName = params.getString("environmentName");

        if (StringUtils.isEmpty(deploymentProjectName))
            errorCollection.addError("deploymentProjectName", "Deployment Project cannot be empty.");

        if (StringUtils.isEmpty(environmentName))
            errorCollection.addError("environmentName", "Environment can not be empty.");

        if (StringUtils.isEmpty(environmentName) || StringUtils.isEmpty(deploymentProjectName))
            return;

        DeploymentProject deploymentProject = getMatchingDeploymentProject(deploymentProjectName);

        if (deploymentProject == null){
            errorCollection.addError("deploymentProjectName", "Unable to find Deployment Project named: " + deploymentProjectName);
            return;
        }

        if(getMatchingEnvironment(deploymentProject, environmentName) == null)
            errorCollection.addError("environmentName", "Unable to find Environment named: " + environmentName + " for deployment project : " + deploymentProjectName);

    }

    private DeploymentProject getMatchingDeploymentProject(String name){

        List<DeploymentProject> allDeploymentProjects = deploymentProjectService.getAllDeploymentProjects();

        for (DeploymentProject deploymentProject : allDeploymentProjects) {
            if(deploymentProject.getName().equals(name))
                return deploymentProject;
        }

        return null;
    }

    private Environment getMatchingEnvironment(DeploymentProject deploymentProject, String name) {

        for (Environment environment : deploymentProject.getEnvironments()) {
            if(environment.getName().equals(name))
                return environment;
        }

        return null;
    }

    private Environment getMatchingEnvironment(DeploymentProject deploymentProject, long id) {

        for (Environment environment : deploymentProject.getEnvironments()) {
            if(environment.getId() == id)
                return environment;
        }

        return null;
    }
}