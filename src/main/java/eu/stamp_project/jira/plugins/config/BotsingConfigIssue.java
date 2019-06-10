package eu.stamp_project.jira.plugins.config;

import java.util.List;

public final class BotsingConfigIssue extends BotsingConfig {

	private String issueKey;
	private List<String> testFileBody;

	public BotsingConfigIssue(String projectKey, String artifactId) {
		super(projectKey, artifactId);
	}

	public BotsingConfigIssue(BotsingConfig botsingConfig) {
		super(botsingConfig.getProjectKey(), botsingConfig.getGroupId(), botsingConfig.getArtifactId(),
				botsingConfig.getVersion(), botsingConfig.getSearchBudget(), botsingConfig.getGlobalTimeout(),
				botsingConfig.getPopulation(), botsingConfig.getPackageFilter(), botsingConfig.getEnabled());
	}

	public String getIssueKey() {
		return issueKey;
	}

	public void setIssueKey(String issueKey) {
		this.issueKey = issueKey;
	}

	public List<String> getTestFileBody() {
		return testFileBody;
	}

	public void setTestFileBody(List<String> testFileBody) {
		this.testFileBody = testFileBody;
	}

}
