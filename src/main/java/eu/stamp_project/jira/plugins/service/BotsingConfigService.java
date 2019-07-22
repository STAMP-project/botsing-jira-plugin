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
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.atlassian.jira.component.ComponentAccessor;
import com.atlassian.jira.issue.MutableIssue;
import com.atlassian.jira.issue.label.LabelManager;
import com.atlassian.jira.permission.GlobalPermissionKey;
import com.atlassian.jira.user.ApplicationUser;
import com.atlassian.jira.util.I18nHelper;
import com.atlassian.plugin.spring.scanner.annotation.component.Scanned;
import com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport;
import com.atlassian.sal.api.pluginsettings.PluginSettings;
import com.atlassian.sal.api.pluginsettings.PluginSettingsFactory;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import eu.stamp_project.jira.plugins.config.BotsingProjectConfig;
import eu.stamp_project.jira.plugins.config.BotsingServerConfig;
import eu.stamp_project.jira.plugins.utils.JiraUtil;

@Path("/")
@Scanned
public class BotsingConfigService {

	private static final Logger log = LoggerFactory.getLogger(BotsingConfigService.class);

	@ComponentImport
	PluginSettingsFactory pluginSettingsFactory;

//	@JiraImport
//	private final LabelManager labelManager;

    @Context
    private HttpServletRequest request;

	private final I18nHelper i18n;

	@Inject
	public BotsingConfigService(PluginSettingsFactory pluginSettingsFactory, final LabelManager labelManager) {

		this.pluginSettingsFactory = pluginSettingsFactory;
//		this.labelManager = labelManager;
		pluginSettings = this.pluginSettingsFactory.createSettingsForKey(BotsingConfigService.class.getName());
        this.i18n = ComponentAccessor.getJiraAuthenticationContext().getI18nHelper();
	}


    @GET
    @Path("config/hello")
    @Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    public Response getMessage(@QueryParam("key") String key)
    {
    	// TODO: classe per testare se i servizi sono raggiungibili
        if(key!=null)
            return Response.ok(new BotsingProjectConfig("PRJ", "artifactId")).build();
        else
            return Response.ok(new BotsingProjectConfig("PRJ2", "artifactId222")).build();
    }

	@POST
	@Path("config/server/edit")
	@Produces(MediaType.TEXT_HTML)
	public Response editBotsingServerConfig(@FormParam("base_url") @DefaultValue("") String baseUrl,
			@FormParam("user") String user, @FormParam("password") String password,
			@FormParam("proxy_host") String proxyHost, @FormParam("proxy_port") String proxyPort) {

		// check admin authorization
        final Response response = checkAdminPermissions(ComponentAccessor.getJiraAuthenticationContext().getLoggedInUser());
        if (response != null) {
            return response;
        }

		final BotsingServerConfig newBotsingServerConfig = new BotsingServerConfig(baseUrl, user, password, proxyHost, proxyPort);
		addBotsingServerConfig(newBotsingServerConfig);

		log.info("Added new Botsing server configuration");
		return Response.ok().build();
	}

	/**
	 * Service to add a new Botsing Jira project configuration
	 * @param projectKey
	 * @param groupId
	 * @param artifactId
	 * @param version
	 * @param searchBudget
	 * @param globalTimeout
	 * @param population
	 * @param packageFilter
	 * @return
	 */
	@POST
	@Path("config/project/add")
	@Produces(MediaType.TEXT_HTML)
	public Response addBotsingProjectConfig(@FormParam("project_key") @DefaultValue("") String projectKey,
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
		final BotsingProjectConfig existingBotsingProjectConfig = getBotsingProjectConfig(projectKey);
		if (existingBotsingProjectConfig != null) {
			return Response.ok(i18n.getText("botsing.error.params.config.exists", projectKey))
					.status(Response.Status.BAD_REQUEST).build();
		}

		final BotsingProjectConfig newBotsingProjectConfig = new BotsingProjectConfig(projectKey, groupId, artifactId, version, searchBudget,
				globalTimeout, population, packageFilter, true);

		addBotsingProjectConfig(newBotsingProjectConfig);

		log.info("Added new Botsing configuration for project '" + projectKey + "'");
		return Response.ok().build();
	}

	/**
	 * Service to modify an existing Botsing Jira project configuration
	 * @param projectKey
	 * @param groupId
	 * @param artifactId
	 * @param version
	 * @param searchBudget
	 * @param globalTimeout
	 * @param population
	 * @param packageFilter
	 * @return
	 */
	@POST
    @Path("config/project/edit")
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
        final BotsingProjectConfig existingBotsingProjectConfig = getBotsingProjectConfig(projectKey);
        if (existingBotsingProjectConfig == null) {
            return Response.ok(i18n.getText("botsing.error.params.invalid")).status(Response.Status.BAD_REQUEST).build();
        }

        // override existing botsing config
        final BotsingProjectConfig newBotsingProjectConfig = new BotsingProjectConfig(projectKey, groupId, artifactId, version, searchBudget,
				globalTimeout, population, packageFilter, existingBotsingProjectConfig.getEnabled());

		addBotsingProjectConfig(newBotsingProjectConfig);

        log.info("Edited Botsing configuration for project '" + projectKey + "'");
        return Response.ok().build();
    }

	/**
	 * Service to enable or disable an existing Botsing Jira project configuration
	 * @param projectKey
	 * @param enabled
	 * @return
	 */
	@POST
    @Path("config/project/{projectKey:.+}/activity")
    @Produces(MediaType.TEXT_HTML)
    public Response disableConfig(@PathParam("projectKey") @DefaultValue("") String projectKey,
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
        final BotsingProjectConfig existingBotsingProjectConfig = getBotsingProjectConfig(projectKey);
        if (existingBotsingProjectConfig == null) {
            return Response.ok(i18n.getText("botsing.error.params.invalid")).status(Response.Status.BAD_REQUEST).build();
        }

        // override existing botsing config
		final BotsingProjectConfig newBotsingProjectConfig = new BotsingProjectConfig(projectKey,
				existingBotsingProjectConfig.getGroupId(), existingBotsingProjectConfig.getArtifactId(),
				existingBotsingProjectConfig.getVersion(), existingBotsingProjectConfig.getSearchBudget(),
				existingBotsingProjectConfig.getGlobalTimeout(), existingBotsingProjectConfig.getPopulation(),
				existingBotsingProjectConfig.getPackageFilter(), enabled);

        addBotsingProjectConfig(newBotsingProjectConfig);

        log.info("Botsing configuration was "+ (enabled ? "enabled" : "disabled") +"for project '" + projectKey + "'");
        return getReferrerResponse(request);
    }

	/**
	 * Service to remove an existing Botsing Jira project configuration
	 * @param projectKey
	 * @return
	 */
    @POST
    @Path("config/project/{projectKey:.+}/remove")
    @Produces(MediaType.TEXT_HTML)
    public Response removeBotsingProjectConfig( @PathParam("projectKey") @DefaultValue("") String projectKey) {

		// check admin authorization
        final Response response = checkAdminPermissions(ComponentAccessor.getJiraAuthenticationContext().getLoggedInUser());
        if (response != null) {
            return response;
        }

        if (projectKey.isEmpty()) {
            return Response.ok(i18n.getText("config.error.params.empty")).status(Response.Status.BAD_REQUEST).build();
        }

        deleteBotsingProjectConfig(projectKey);

        log.info("Removed Botsing configuration for project '" + projectKey + "'");
        return getReferrerResponse(request);
    }

    // /rest/botsing-config/1.0/reproduction/ABC-123/add"
    /**
     * Service to add Botsing reproduction test to the issue
     * @param issueKey
     * @return
     */
	@POST
    @Path("reproduction/{issue:.+}/add")
    @Produces(MediaType.TEXT_HTML)
    public Response createAttachment(@PathParam("issue") @DefaultValue("") String issueKey) {

		// parameters to get from the request:
		// issuekey
		// test file
		// interface test file

		// check issueKey
		if (issueKey.isEmpty()) {
            return Response.ok(i18n.getText("config.error.params.empty")).status(Response.Status.BAD_REQUEST).build();
        }

		// check existing issue
		MutableIssue issue = ComponentAccessor.getIssueManager().getIssueObject(issueKey);
        if (issue == null) {
            return Response.ok(i18n.getText("botsing.error.params.invalid")).status(Response.Status.BAD_REQUEST).build();
        }

        // get existing configuration
        final BotsingProjectConfig existingBotsingProjectConfig = getBotsingProjectConfig(JiraUtil.getProjectKeyFromIssueKey(issueKey));
        if (existingBotsingProjectConfig == null) {
            return Response.ok(i18n.getText("botsing.error.params.invalid")).status(Response.Status.BAD_REQUEST).build();
        }

        // get user to create comment and attachments
        ApplicationUser user = ComponentAccessor.getUserManager().getUserByKey("stamp");

		// create comment
        // TODO i18n for this comment
        ComponentAccessor.getCommentManager().create(issue, user, "Reproduction test had been generated", true);

		// add test as attachments
        //CreateAttachmentParamsBean capb = new CreateAttachmentParamsBean(file, filename, contentType, author, issue, zip, thumbnailable, attachmentProperties, createdTime, copySourceFile);
        //ComponentAccessor.getAttachmentManager().createAttachment(createAttachmentParamsBean)Attachment(file, filename, contentType, author, issue);

        // add done label
        // TODO make user configurable!

//        labelManager.setLabels(user, issue.getId(), JiraUtil.getDoneLabelSet(labelManager.getLabels(issue.getId())), false, true);

		return Response.ok().build();

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

    private BotsingServerConfig getBotsingServerConfig() {
    	Type emptyType = new TypeToken<BotsingServerConfig>() {}.getType();

        final BotsingServerConfig config = gson.fromJson((String)pluginSettings.get(BotsingServerConfig.BOTSING_SERVER_CONFIG_KEY), emptyType);

        return config;
    }

	public void addBotsingServerConfig(BotsingServerConfig config) {

        pluginSettings.put(BotsingServerConfig.BOTSING_SERVER_CONFIG_KEY, gson.toJson(config));
    }

	public BotsingProjectConfig getBotsingProjectConfig(String projectKey) {
        final Map<String, BotsingProjectConfig> configs = getBotsingProjectConfigMap();

        return configs.get(projectKey);
    }

	public void addBotsingProjectConfig(BotsingProjectConfig config) {
        final Map<String, BotsingProjectConfig> configs = getBotsingProjectConfigMap();

        configs.put(config.getProjectKey(), config);

        pluginSettings.put(BotsingProjectConfig.BOTSING_PROJECT_CONFIG_KEY, gson.toJson(configs));
    }

	public void deleteBotsingProjectConfig(String projectKey) {
        final Map<String, BotsingProjectConfig> configs = getBotsingProjectConfigMap();

        configs.remove(projectKey);

        pluginSettings.put(BotsingProjectConfig.BOTSING_PROJECT_CONFIG_KEY, gson.toJson(configs));
    }

    private Map<String, BotsingProjectConfig> getBotsingProjectConfigMap() {
    	Type emptyMapType = new TypeToken<Map<String, BotsingProjectConfig>>() {}.getType();

        final Map<String, BotsingProjectConfig> configs = gson.fromJson((String)pluginSettings.get(BotsingProjectConfig.BOTSING_PROJECT_CONFIG_KEY), emptyMapType);

        return configs == null ? new HashMap<String, BotsingProjectConfig>() : configs;
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
