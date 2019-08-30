package eu.stamp_project.jira.plugins.listener;

import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Type;
import java.util.Collection;
import java.util.Map;

import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.atlassian.event.api.EventListener;
import com.atlassian.event.api.EventPublisher;
import com.atlassian.jira.component.ComponentAccessor;
import com.atlassian.jira.config.properties.APKeys;
import com.atlassian.jira.event.issue.IssueEvent;
import com.atlassian.jira.issue.AttachmentManager;
import com.atlassian.jira.issue.Issue;
import com.atlassian.jira.issue.attachment.Attachment;
import com.atlassian.jira.issue.label.Label;
import com.atlassian.jira.issue.label.LabelManager;
import com.atlassian.jira.util.io.InputStreamConsumer;
import com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport;
import com.atlassian.plugin.spring.scanner.annotation.imports.JiraImport;
import com.atlassian.sal.api.pluginsettings.PluginSettings;
import com.atlassian.sal.api.pluginsettings.PluginSettingsFactory;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import eu.stamp_project.jira.plugins.BotsingClient;
import eu.stamp_project.jira.plugins.config.BotsingConfig;
import eu.stamp_project.jira.plugins.config.BotsingIssueConfig;
import eu.stamp_project.jira.plugins.config.BotsingProjectConfig;
import eu.stamp_project.jira.plugins.config.BotsingServerConfig;
import eu.stamp_project.jira.plugins.service.BotsingConfigService;

@Component
public class BotsingAttachmentListener implements InitializingBean, DisposableBean {
	private static final Logger log = LoggerFactory.getLogger(BotsingAttachmentListener.class);

	// label marker
	public static final String LABEL_STAMP = "STAMP";
	public static final String LABEL_REPRODUCTION_DOING = "doing-reproduction";
	public static final String LABEL_REPRODUCTION_DONE = "reproduction-done";
	public static final String LABEL_REPRODUCTION_FAILED = "reproduction-failed";

	@JiraImport
	private final EventPublisher eventPublisher;

	@JiraImport
	private final LabelManager labelManager;

	@JiraImport
	private final AttachmentManager attachmentManager;

    private final PluginSettings pluginSettings;

    private final Gson gson = new Gson();

	@ComponentImport
	PluginSettingsFactory pluginSettingsFactory;

	@Autowired
	public BotsingAttachmentListener(final EventPublisher eventPublisher, final LabelManager labelManager,
			final AttachmentManager attachmentManager, final PluginSettingsFactory pluginSettingsFactory) {

		this.eventPublisher = eventPublisher;
		this.labelManager = labelManager;
		this.attachmentManager = attachmentManager;
		pluginSettings = pluginSettingsFactory.createSettingsForKey(BotsingConfigService.class.getName());
	}

	/**
	 * Called when the plugin has been enabled.
	 *
	 * @throws Exception
	 */
	@Override
	public void afterPropertiesSet() throws Exception {
		eventPublisher.register(this);
	}

	/**
	 * Called when the plugin is being disabled or removed.
	 *
	 * @throws Exception
	 */
	@Override
	public void destroy() throws Exception {
		eventPublisher.unregister(this);
	}

	@EventListener
	public void onIssueEvent(IssueEvent issueEvent) {
		Long eventTypeId = issueEvent.getEventTypeId();
		Issue issue = issueEvent.getIssue();

		// check labels
		if (hasLabel(LABEL_STAMP, issue) && !hasLabel(LABEL_REPRODUCTION_DOING, issue)
				&& !hasLabel(LABEL_REPRODUCTION_DONE, issue)) {

			// TODO only for testing purpose, remove before releasing the plugin
			System.out.println(">>> eventTypeId: " + eventTypeId);

			// check attachment
			String attachment = getAttachmentAsString(issue);
			if (attachment != null) {

				// check botsing configuration
				final BotsingProjectConfig botsingProjectConfig = getBotsingProjectConfig(issue.getProjectObject().getKey());
				log.debug(botsingProjectConfig.toString());
				if (botsingProjectConfig != null && botsingProjectConfig.getEnabled()) {
					BotsingIssueConfig issueConfig = new BotsingIssueConfig(botsingProjectConfig, issue.getKey(), attachment);

					String callbackURL = ComponentAccessor.getApplicationProperties().getString(APKeys.JIRA_BASEURL)+"/rest/botsing-config/1.0/reproduction/"+issue.getKey()+"/add";
					BotsingConfig config = new BotsingConfig(botsingProjectConfig, issueConfig, callbackURL);

					// add label to exclude new calls while it is working
					labelManager.addLabel(issueEvent.getUser(), issue.getId(), LABEL_REPRODUCTION_DOING, false);

					// call Botsing-server service
					BotsingClient botsingClient = new BotsingClient(getBotsingServerConfig().getBaseUrl());
					botsingClient.postBotsingIssueEventCall(config);

					// add comment
					ComponentAccessor.getCommentManager().create(issue, issueEvent.getUser(), "Sent request to Botsing server", false);

				} else {
					log.warn("Received Botsing event, but no configuration found for project '"+issue.getProjectObject().getKey()+"' in botsing-jira-plugin.");
				}
			}
		}
	}

	private boolean hasLabel(String label, Issue issue) {
		for (Label l : issue.getLabels()) {
			if (l.getLabel().equalsIgnoreCase(label)) {
				return true;
			}
		}

		return false;
	}

	public BotsingServerConfig getBotsingServerConfig() {
		Type emptyMapType = new TypeToken<BotsingServerConfig>() {}.getType();

		final BotsingServerConfig config = gson
				.fromJson((String) pluginSettings.get(BotsingServerConfig.BOTSING_SERVER_CONFIG_KEY), emptyMapType);

		return config;
	}

	public BotsingProjectConfig getBotsingProjectConfig(String projectKey) {
		Type emptyMapType = new TypeToken<Map<String, BotsingProjectConfig>>() {}.getType();

		final Map<String, BotsingProjectConfig> configs = gson
				.fromJson((String) pluginSettings.get(BotsingProjectConfig.BOTSING_PROJECT_CONFIG_KEY), emptyMapType);

		return configs.get(projectKey);
	}

	@SuppressWarnings("unchecked")
	private String getAttachmentAsString(Issue issue) {
		String result = null;

		// check attachment
		Collection<Attachment> attachments = issue.getAttachments();
		if (attachments == null || attachments.size() <= 0) {
			log.warn("No attachment found in issue '"+issue.getKey()+"'");
			return null;

		} else if (attachments.size() > 1) {
			log.warn("Multiple attachments found in issue '"+issue.getKey()+"', cannot call Botsing");
			return null;
		}

		Attachment a = attachments.iterator().next();
		try {

			result = (String)attachmentManager.streamAttachmentContent(a, new InputStreamConsumer() {
				@Override
				public String withInputStream(final InputStream is) throws IOException {
					return IOUtils.toString(is);
				}
			});

		} catch (IOException e) {
			log.error("Cannot read attachment '" + a.getFilename() + "'", e);
		}

		return result;
	}

}
