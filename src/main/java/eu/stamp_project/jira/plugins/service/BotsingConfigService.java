package eu.stamp_project.jira.plugins.service;

import java.lang.reflect.Type;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.Map;

import javax.inject.Inject;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.DefaultValue;
import javax.ws.rs.FormParam;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.atlassian.jira.component.ComponentAccessor;
import com.atlassian.jira.permission.GlobalPermissionKey;
import com.atlassian.jira.user.ApplicationUser;
import com.atlassian.jira.util.I18nHelper;
import com.atlassian.plugin.spring.scanner.annotation.component.Scanned;
import com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport;
import com.atlassian.sal.api.pluginsettings.PluginSettings;
import com.atlassian.sal.api.pluginsettings.PluginSettingsFactory;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import eu.stamp_project.jira.plugins.config.BotsingConfig;

@Path("/")
@Scanned
public class BotsingConfigService {

	private static final Logger log = LoggerFactory.getLogger(BotsingConfigService.class);

	@ComponentImport
	PluginSettingsFactory pluginSettingsFactory;

    @Context
    private HttpServletRequest request;

	private final I18nHelper i18n;

	@Inject
	public BotsingConfigService(PluginSettingsFactory pluginSettingsFactory) {

		this.pluginSettingsFactory = pluginSettingsFactory;
		pluginSettings = this.pluginSettingsFactory.createSettingsForKey(BotsingConfig.class.getName());
        this.i18n = ComponentAccessor.getJiraAuthenticationContext().getI18nHelper();
	}

	@POST
	@Path("config/add")
	@Produces(MediaType.TEXT_HTML)
	public Response addBotsingConfig(@FormParam("project_key") @DefaultValue("") String projectKey,
			@FormParam("group_id") @DefaultValue("") String groupId,
			@FormParam("artifact_id") @DefaultValue("") String artifactId,
			@FormParam("version") @DefaultValue("") String version,
			@FormParam("search_budget") @DefaultValue("") Integer searchBudget,
			@FormParam("global_timeout") @DefaultValue("") Integer globalTimeout,
			@FormParam("population") @DefaultValue("") Integer population,
			@FormParam("package_filter") @DefaultValue("") String packageFilter) {

		// check admin authorization
        final Response response = checkAdminPermissions(ComponentAccessor.getJiraAuthenticationContext().getLoggedInUser());
        if (response != null) {
            return response;
        }

		// check if configuration already exist
		final BotsingConfig existingBotsingConfig = getBotsingConfig(projectKey);
		if (existingBotsingConfig != null) {
			return Response.ok(i18n.getText("botsing.error.params.config.exists", projectKey))
					.status(Response.Status.BAD_REQUEST).build();
		}

		final BotsingConfig newBotsingConfig = new BotsingConfig(projectKey, groupId, artifactId, version, searchBudget,
				globalTimeout, population, packageFilter, true);

		addBotsingConfig(newBotsingConfig);

		log.info("Added new Botsing configuration for project '" + projectKey + "'");
		return Response.ok().build();
	}

	@POST
    @Path("config/edit")
    @Produces(MediaType.TEXT_HTML)
    public Response editConfig(@FormParam("project_key") @DefaultValue("") String projectKey,
			@FormParam("group_id") @DefaultValue("") String groupId,
			@FormParam("artifact_id") @DefaultValue("") String artifactId,
			@FormParam("version") @DefaultValue("") String version,
			@FormParam("search_budget") @DefaultValue("") Integer searchBudget,
			@FormParam("global_timeout") @DefaultValue("") Integer globalTimeout,
			@FormParam("population") @DefaultValue("") Integer population,
			@FormParam("package_filter") @DefaultValue("") String packageFilter) {

		// check admin authorization
        final Response response = checkAdminPermissions(ComponentAccessor.getJiraAuthenticationContext().getLoggedInUser());
        if (response != null) {
            return response;
        }

        // check mandatory parameters
        if (projectKey.isEmpty()) {
            return Response.ok(i18n.getText("botsing.error.params.empty")).status(Response.Status.BAD_REQUEST).build();
        }

        // get existing configuration
        final BotsingConfig existingBotsingConfig = getBotsingConfig(projectKey);
        if (existingBotsingConfig == null) {
            return Response.ok(i18n.getText("botsing.error.params.invalid")).status(Response.Status.BAD_REQUEST).build();
        }

        // override existing botsing config
        final BotsingConfig newBotsingConfig = new BotsingConfig(projectKey, groupId, artifactId, version, searchBudget,
				globalTimeout, population, packageFilter, existingBotsingConfig.getEnabled());

		addBotsingConfig(newBotsingConfig);

        log.info("Edited Botsing configuration for project '" + projectKey + "'");
        return Response.ok().build();
    }

	@POST
    @Path("/config/{config:.+}/activity")
    @Produces(MediaType.TEXT_HTML)
    public Response disableConfig(@PathParam("config") @DefaultValue("") String projectKey,
                                @FormParam("enabled") Boolean enabled) {

		// check admin authorization
        final Response response = checkAdminPermissions(ComponentAccessor.getJiraAuthenticationContext().getLoggedInUser());
        if (response != null) {
            return response;
        }

        // check input
        if (enabled == null || projectKey.isEmpty()) {
            return Response.ok(i18n.getText("botsing.error.params.empty")).status(Response.Status.BAD_REQUEST).build();
        }

        // get existing configuration
        final BotsingConfig existingBotsingConfig = getBotsingConfig(projectKey);
        if (existingBotsingConfig == null) {
            return Response.ok(i18n.getText("botsing.error.params.invalid")).status(Response.Status.BAD_REQUEST).build();
        }

        // override existing botsing config
        final BotsingConfig newBotsingConfig = new BotsingConfig(projectKey, existingBotsingConfig.getGroupId(), existingBotsingConfig.getArtifactId(), existingBotsingConfig.getVersion(), existingBotsingConfig.getSearchBudget(),
        		existingBotsingConfig.getGlobalTimeout(), existingBotsingConfig.getPopulation(), existingBotsingConfig.getPackageFilter(), enabled);

        addBotsingConfig(newBotsingConfig);

        log.info("Botsing configuration was "+ (enabled ? "enabled" : "disabled") +"for project '" + projectKey + "'");
        return getReferrerResponse(request);
    }

    @POST
    @Path("/config/{config:.+}/remove")
    @Produces(MediaType.TEXT_HTML)
    public Response removeBotsingConfig( @PathParam("config") @DefaultValue("") String projectKey) {

		// check admin authorization
        final Response response = checkAdminPermissions(ComponentAccessor.getJiraAuthenticationContext().getLoggedInUser());
        if (response != null) {
            return response;
        }

        if (projectKey.isEmpty()) {
            return Response.ok(i18n.getText("config.error.params.empty")).status(Response.Status.BAD_REQUEST).build();
        }

        deleteBotsingConfig(projectKey);

        log.info("Removed Botsing configuration for project '" + projectKey + "'");
        return getReferrerResponse(request);
    }

    private static Response getReferrerResponse(HttpServletRequest request) {
        try {
            return Response.seeOther(new URI(request.getHeader("referer"))).build();
        } catch (URISyntaxException e) {
            return Response.ok(e.getMessage()).status(Response.Status.INTERNAL_SERVER_ERROR).build();
        }
    }

    // TODO these methods should go in a separate component. Cannot do it due to atlassian spring scanner errors

	private final PluginSettings pluginSettings;

    private final Gson gson = new Gson();

	public BotsingConfig getBotsingConfig(String projectKey) {
        final Map<String, BotsingConfig> configs = getBotsingConfigMap();

        return configs.get(projectKey);
    }

	public void addBotsingConfig(BotsingConfig config) {
        final Map<String, BotsingConfig> configs = getBotsingConfigMap();

        configs.put(config.getProjectKey(), config);

        pluginSettings.put(BotsingConfig.BOTSING_CONFIG_KEY, gson.toJson(configs));
    }

	public void deleteBotsingConfig(String projectKey) {
        final Map<String, BotsingConfig> configs = getBotsingConfigMap();

        configs.remove(projectKey);

        pluginSettings.put(BotsingConfig.BOTSING_CONFIG_KEY, gson.toJson(configs));
    }

    private Map<String, BotsingConfig> getBotsingConfigMap() {
    	Type emptyMapType = new TypeToken<Map<String, BotsingConfig>>() {}.getType();

        final Map<String, BotsingConfig> configs = gson.fromJson((String)pluginSettings.get(BotsingConfig.BOTSING_CONFIG_KEY), emptyMapType);

        return configs == null ? new HashMap<String, BotsingConfig>() : configs;
    }

    private Response checkAdminPermissions(ApplicationUser user) {

    	if (user == null) {
            return Response.ok("User is not logged in").status(Response.Status.UNAUTHORIZED).build();
        }

        if (!ComponentAccessor.getGlobalPermissionManager().hasPermission(GlobalPermissionKey.SYSTEM_ADMIN, user)) {
            return Response.ok("Invalid permissions for " + user.getName()).status(Response.Status.FORBIDDEN).build();
        }

        return null;
    }

}
