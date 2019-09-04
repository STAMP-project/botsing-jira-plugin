# Botsing Jira plugin

This plugin analyze issues that have stacktrace as attachments and the label 'STAMP' and run Botsing to generate a test to replicate the stacktrace.

The execution of Botsing will be done in an instance of [Botsing server](https://github.com/STAMP-project/botsing-server) and the test generated will be added as attachments in the issue itself. 

A comment will be added at the start and at the end of this process in the issue. 

## Prerequisites

This plugin has been developed for Jira 7.13.0.

This plugin connects to a [Botsing server](https://github.com/STAMP-project/botsing-server) that can be configured in the botsing-jira-plugin configuration page.

### Plugin configuration

The plugin can be configured accessing the "Botsing plugin configuration" in the "Add-on" administration section.

In this page you can add a Botsing project specific configuration, such as:

* Jira Project key
* Group id
* Artifact id
* Version
* Search budget
* Global timeout
* Population
* Package filter

With the button 'Edit server' it is possible to configure Botsing server endpoint. 

### Jira Configuration

There is non other configuration to do in Jira, the issues that will trigger Botsing are only the ones with one attachment and the label 'STAMP'.

## Compile and package

To compile the plugin you need to have installed the Atlassian SDK ([Introduction to the Atlassian Plugin SDK](https://developer.atlassian.com/display/DOCS/Introduction+to+the+Atlassian+Plugin+SDK)) and can be done with this command:

```
atlas-mvn compile
```

To create the plugin to install in Jira you can do it with:

```
atlas-mvn package
```

# Installation

Installation steps:

1) Access Jira administration section "Add ons" > "Manage add-ons"

1) Click "Upload add-on"

1) Select the jar created in package section

1) Click Upload

1) Wait till you see a success installation popup window 

## License

This plugin is released under the [Apache2](http://opensource.org/licenses/Apache-2.0) license

## Acknowledgement

This plugin is partially funded by research project H2020 [STAMP](http://stamp-project.eu/).
