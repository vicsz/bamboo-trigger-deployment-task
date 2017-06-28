
[![Build Status](https://travis-ci.org/vicsz/bamboo-trigger-deployment-task.svg?branch=master)](https://travis-ci.org/vicsz/bamboo-trigger-deployment-task)

Bamboo Deployment Trigger Plugin
- Updated for latest Version of Bamboo 6.0

You have successfully created an Atlassian Plugin!

Here are the SDK commands you'll use immediately:

* atlas-run   -- installs this plugin into the product and starts it on localhost
* atlas-debug -- same as atlas-run, but allows a debugger to attach at port 5005
* atlas-cli   -- after atlas-run or atlas-debug, opens a Maven command line window:
                 - 'pi' reinstalls the plugin into the running product instance
* atlas-help  -- prints description for all commands in the SDK

Full documentation is always available at:

https://developer.atlassian.com/display/DOCS/Introduction+to+the+Atlassian+Plugin+SDK


## Working with Intellij

1. Import project into Intellij using the pox.xml
2. Run atlas-run once to pull down dependencies ..
2. Add /target/bamboo/webapp/WEB-INF/lib to project structure libraries to resolve missing dependecies.

## Development

Starting up Sample Bamboo with your Plugin
1. Run #atlas-run
2. Go to : localhost:6990/bamboo
3. Default username / password is admin/admin