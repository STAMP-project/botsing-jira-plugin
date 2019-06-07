package eu.stamp_project.jira.plugins.util;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.atlassian.plugin.PluginAccessor;
import com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport;
import com.atlassian.sal.api.ApplicationProperties;

/**
 * This class exists solely to cause Spring Scanner to import the required OSGi
 * services. It should never be instantiated.
 */
@Component
public class ComponentImporter {

	@Autowired
	public ComponentImporter(
			@ComponentImport PluginAccessor pluginAccessor,
			@ComponentImport ApplicationProperties applicationProperties
			//@ComponentImport MessageUserProcessor messageUserProcessor
			) {
		// no need to actually do anything with it...
	}

}
