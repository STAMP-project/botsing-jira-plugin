package eu.stamp_project.jira.plugins.config;

public class BotsingConfig {

	private BotsingProjectConfig botsingProjectConfig;
	private BotsingIssueConfig botsingIssueConfig;
	private String jiraCallbackUrl;

	public BotsingConfig(BotsingProjectConfig botsingProjectConfig,
			BotsingIssueConfig botsingIssueConfig, String jiraCallbackUrl) {

		super();

		this.botsingProjectConfig = botsingProjectConfig;
		this.botsingIssueConfig = botsingIssueConfig;
		this.jiraCallbackUrl = jiraCallbackUrl;
	}

	public BotsingProjectConfig getBotsingProjectConfig() {
		return botsingProjectConfig;
	}

	public void setBotsingProjectConfig(BotsingProjectConfig botsingProjectConfig) {
		this.botsingProjectConfig = botsingProjectConfig;
	}

	public BotsingIssueConfig getBotsingIssueConfig() {
		return botsingIssueConfig;
	}

	public void setBotsingIssueConfig(BotsingIssueConfig botsingIssueConfig) {
		this.botsingIssueConfig = botsingIssueConfig;
	}

	public String getJiraCallbackUrl() {
		return jiraCallbackUrl;
	}

	public void setJiraCallbackUrl(String jiraCallbackUrl) {
		this.jiraCallbackUrl = jiraCallbackUrl;
	}

}