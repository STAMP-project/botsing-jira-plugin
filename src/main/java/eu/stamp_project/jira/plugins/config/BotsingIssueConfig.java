package eu.stamp_project.jira.plugins.config;

/**
 * Configuration parameters to call Botsing server contained in the issue
 * @author luca
 *
 */
public final class BotsingIssueConfig extends BotsingProjectConfig {

	private String issueKey;
	private String testFileBody;

	public BotsingIssueConfig(BotsingProjectConfig botsingProjectConfig, String issueKey, String testFileBody) {
		super(botsingProjectConfig.getProjectKey(), botsingProjectConfig.getGroupId(), botsingProjectConfig.getArtifactId(),
				botsingProjectConfig.getVersion(), botsingProjectConfig.getSearchBudget(), botsingProjectConfig.getGlobalTimeout(),
				botsingProjectConfig.getPopulation(), botsingProjectConfig.getPackageFilter(), botsingProjectConfig.getEnabled());

		setIssueKey(issueKey);
		setTestFileBody(testFileBody);
	}

	public String getIssueKey() {
		return issueKey;
	}

	public void setIssueKey(String issueKey) {
		this.issueKey = issueKey;
	}

	public String getTestFileBody() {
		return testFileBody;
	}

	public void setTestFileBody(String testFileBody) {
		this.testFileBody = testFileBody;
	}

}
