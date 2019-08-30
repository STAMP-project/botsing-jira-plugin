package eu.stamp_project.jira.plugins.utils;

import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
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
		newLabelSet.add(BotsingAttachmentListener.LABEL_REPRODUCTION_DONE);

		return newLabelSet;
	}
	
	public static Set<String> getFailedLabelSet(Set<Label> labels) {
		Set<String> newLabelSet = new HashSet<String>();

		// recreate label set without Doing label
		for (Label l : labels) {
			if (!l.getLabel().equals(BotsingAttachmentListener.LABEL_REPRODUCTION_DOING)) {
				newLabelSet.add(l.getLabel());
			}
		}

		// add Done label
		newLabelSet.add(BotsingAttachmentListener.LABEL_REPRODUCTION_FAILED);

		return newLabelSet;
	}

	public static String readFile(String path, Charset encoding) throws IOException {
		byte[] encoded = Files.readAllBytes(Paths.get(path));
		return new String(encoded, encoding);
	}
}
