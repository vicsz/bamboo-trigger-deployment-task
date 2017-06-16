package com.bambooplugin.impl;

import com.atlassian.bamboo.build.logger.BuildLogger;
import com.atlassian.bamboo.task.*;

public class MyFirstTask implements TaskType {

    @Override
    public TaskResult execute(final TaskContext taskContext) throws TaskException
    {
        final BuildLogger buildLogger = taskContext.getBuildLogger();

        buildLogger.addBuildLogEntry("Hello, World!");

        return TaskResultBuilder.create(taskContext).success().build();
    }
}
