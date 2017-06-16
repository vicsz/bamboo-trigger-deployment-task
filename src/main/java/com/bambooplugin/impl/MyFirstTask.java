package com.bambooplugin.impl;

import com.atlassian.bamboo.build.logger.BuildLogger;
import com.atlassian.bamboo.task.*;

public class MyFirstTask implements TaskType {

    @Override
    public TaskResult execute(final TaskContext taskContext) throws TaskException
    {
        final BuildLogger buildLogger = taskContext.getBuildLogger();

        final String say = taskContext.getConfigurationMap().get("say");

        buildLogger.addBuildLogEntry("Hello, World! " + say);

        return TaskResultBuilder.newBuilder(taskContext).success().build();
    }
}
