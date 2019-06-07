package eu.stamp_project.jira.plugins.webworks;

import java.lang.reflect.Type;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;

import javax.inject.Inject;

import com.atlassian.jira.web.HttpServletVariables;
import com.atlassian.jira.web.action.JiraWebActionSupport;
import com.atlassian.jira.web.util.AuthorizationSupport;
import com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport;
import com.atlassian.sal.api.pluginsettings.PluginSettings;
import com.atlassian.sal.api.pluginsettings.PluginSettingsFactory;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import eu.stamp_project.jira.plugins.config.BotsingConfig;


public class BotsingConfigAction extends JiraWebActionSupport implements AuthorizationSupport, HttpServletVariables {

	private static final long serialVersionUID = 1L;

    private final PluginSettings pluginSettings;

    private final Gson gson = new Gson();

	 @ComponentImport
	 PluginSettingsFactory pluginSettingsFactory;

	@Inject
    public BotsingConfigAction(PluginSettingsFactory pluginSettingsFactory) {

    	pluginSettings = pluginSettingsFactory.createSettingsForKey(BotsingConfig.class.getName());

    }

    public Map<String, BotsingConfig> getBotsingConfigMap() {
        return Collections.unmodifiableMap(new TreeMap<>(getBotsingConfigs()));
    }

    public boolean hasAdminPermission() {
        return isSystemAdministrator();
    }

    private Map<String, BotsingConfig> getBotsingConfigs() {
    	Type emptyMapType = new TypeToken<Map<String, BotsingConfig>>() {}.getType();

        final Map<String, BotsingConfig> configs = gson.fromJson((String)pluginSettings.get(BotsingConfig.BOTSING_CONFIG_KEY), emptyMapType);

        return configs == null ? new HashMap<String, BotsingConfig>() : configs;
    }

}
