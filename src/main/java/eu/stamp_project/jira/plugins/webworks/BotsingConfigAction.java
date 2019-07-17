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

import eu.stamp_project.jira.plugins.config.BotsingProjectConfig;
import eu.stamp_project.jira.plugins.config.BotsingServerConfig;
import eu.stamp_project.jira.plugins.service.BotsingConfigService;

public class BotsingConfigAction extends JiraWebActionSupport implements AuthorizationSupport, HttpServletVariables {

	private static final long serialVersionUID = 1L;

	private final PluginSettings pluginSettings;

	private final Gson gson = new Gson();

	@ComponentImport
	PluginSettingsFactory pluginSettingsFactory;

	@Inject
	public BotsingConfigAction(PluginSettingsFactory pluginSettingsFactory) {

		pluginSettings = pluginSettingsFactory.createSettingsForKey(BotsingConfigService.class.getName());

	}

	public BotsingServerConfig getBotsingServerConfig() {
		Type emptyMapType = new TypeToken<BotsingServerConfig>() {}.getType();

		final BotsingServerConfig config = gson.fromJson((String) pluginSettings.get(BotsingServerConfig.BOTSING_SERVER_CONFIG_KEY), emptyMapType);

		return config;
	}

	public Map<String, BotsingProjectConfig> getBotsingProjectConfigMap() {
		return Collections.unmodifiableMap(new TreeMap<>(getBotsingProjectConfigs()));
	}

	public boolean hasAdminPermission() {
		return isSystemAdministrator();
	}

	private Map<String, BotsingProjectConfig> getBotsingProjectConfigs() {
		Type emptyMapType = new TypeToken<Map<String, BotsingProjectConfig>>() {}.getType();

		final Map<String, BotsingProjectConfig> configs = gson
				.fromJson((String) pluginSettings.get(BotsingProjectConfig.BOTSING_PROJECT_CONFIG_KEY), emptyMapType);

		return configs == null ? new HashMap<String, BotsingProjectConfig>() : configs;
	}

}
