package eu.stamp_project.jira.plugins.utils;

import java.util.HashSet;
import java.util.Set;

import com.atlassian.jira.issue.label.Label;

import eu.stamp_project.jira.plugins.listener.BotsingAttachmentListener;

public class JiraUtil {

	public static String getProjectKeyFromIssueKey(String issueKey) {
		String result = null;

		if (issueKey == null) {
			return result;
		}

		int index = issueKey.indexOf("-");

		if (index > 0) {
			result = issueKey.substring(0, index);
		}

		return result;
	}

	public static Set<String> getDoneLabelSet(Set<Label> labels) {
		Set<String> newLabelSet = new HashSet<String>();

		// recreate label set without Doing label
		for (Label l : labels) {
			if (!l.getLabel().equals(BotsingAttachmentListener.LABEL_REPRODUCTION_DOING)) {
				newLabelSet.add(l.getLabel());
			}
		}

		// add Done label
		newLabelSet.add(BotsingAttachmentListener.LABEL_REPRODUCTION_DOING);

		return newLabelSet;
	}
}
