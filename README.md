# Botsing Jira plugin

This plugin analyze issues that have stacktrace as attachments and the label 'STAMP' and run Botsing to generate a test to replicate the stacktrace.

## Prerequisites

This plugin has been developed for Jira 7.13.0.

### Plugin configuration

The plugin can be configured accessing the "Botsing plugin configuration" in the "Add-on" administration section.

In this page you can add a Botsing project specific configuration, such as:

* Project key
* Group id
* Artifact id
* Version
* Search budget
* Global timeout
* Population
* Package filter

### Jira Configuration

There is non other configuration to do in Jira, the issues that will trigger Botsing are only the ones with an attachment and the label 'STAMP'.

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
