package eu.stamp_project.jira.plugins.utils;

import static org.junit.Assert.assertEquals;

import java.io.IOException;

import org.junit.Test;

public class JiraUtilTest {

	@Test
	public void getProjectKey() throws IOException {
		String projectKey = JiraUtil.getProjectKeyFromIssueKey("ABC-123");
		assertEquals("ABC", projectKey);
	}

	@Test
	public void getProjectKeyFromWrongIssueKey() throws IOException {
		String projectKey = JiraUtil.getProjectKeyFromIssueKey("ABC123");
		assertEquals(null, projectKey);
	}

	@Test
	public void getProjectKeyFromNullIssueKey() throws IOException {
		String projectKey = JiraUtil.getProjectKeyFromIssueKey(null);
		assertEquals(null, projectKey);
	}
}
