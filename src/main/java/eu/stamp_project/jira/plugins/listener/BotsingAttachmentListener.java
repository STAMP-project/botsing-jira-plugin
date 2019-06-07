package eu.stamp_project.jira.plugins.listener;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.ofbiz.core.entity.GenericValue;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.atlassian.event.api.EventListener;
import com.atlassian.event.api.EventPublisher;
import com.atlassian.jira.event.issue.IssueEvent;
import com.atlassian.jira.issue.Issue;
import com.atlassian.jira.issue.attachment.Attachment;
import com.atlassian.jira.issue.label.Label;
import com.atlassian.jira.issue.label.LabelManager;
import com.atlassian.plugin.spring.scanner.annotation.imports.JiraImport;

@Component
public class BotsingAttachmentListener implements InitializingBean, DisposableBean {
	private static final Logger log = LoggerFactory.getLogger(BotsingAttachmentListener.class);

	// label marker
	private static final String LABEL_STAMP = "STAMP";
	private static final String LABEL_REPRODUCTION_DOING = "doing-reproduction";
	private static final String LABEL_REPRODUCTION_DONE = "reproduction-done";

	@JiraImport
	private final EventPublisher eventPublisher;

	@JiraImport
	private final LabelManager labelManager;

	@Autowired
	public BotsingAttachmentListener(final EventPublisher eventPublisher, final LabelManager labelManager) {
		this.eventPublisher = eventPublisher;
		this.labelManager = labelManager;
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

		if (hasLabel(LABEL_STAMP, issue) && !hasLabel(LABEL_REPRODUCTION_DOING, issue)
				&& !hasLabel(LABEL_REPRODUCTION_DONE, issue)) {

			// TODO only for testing purpouse, remove before releasing the plugin
			System.out.println(">>> eventTypeId: " + eventTypeId);

			Collection<Attachment> attachments = issue.getAttachments();
			if (attachments != null && attachments.size() > 0) {

				labelManager.addLabel(issueEvent.getUser(), issue.getId(), LABEL_REPRODUCTION_DOING, false);

				// TODO read botsing configuration
				// TODO check if issue has to be processed
				// TODO launch Botsing in a separate process

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

	private List<String> getAttachmentsInChangeSet(IssueEvent issueEvent) {
		List<String> attachmentIds = new ArrayList<String>();

		try {
			GenericValue cl = issueEvent.getChangeLog();
			List<GenericValue> children = cl.getRelated("ChildChangeItem");

			for (GenericValue child : children) {

				String fieldName = child.getString("field");
				if (fieldName.equals("Attachment")) {
					attachmentIds.add(child.getString("newvalue"));
				}
			}

		} catch (Exception e) {
			log.error("Error reading issue event", e);
		}

		return attachmentIds;
	}
}
