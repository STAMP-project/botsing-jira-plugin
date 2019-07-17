package eu.stamp_project.jira.plugins;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import eu.stamp_project.jira.plugins.BotsingClient;
import eu.stamp_project.jira.plugins.config.BotsingIssueConfig;

public class BotsingClientTest {

	private BotsingClient botsingClient;

	private String testServerEndpoint = "http://localhost:5000"; // "http://178.170.72.39:5000";

    @Before
    public void setUp() {
    	botsingClient = new BotsingClient(testServerEndpoint);
    	//botsingClient = new BotsingServerClient("proxy.eng.it", 3128);
    }

    @After
    public void tearDownTest() {
    	botsingClient.destroy();
    }

    @Test
    public void simpleGetCall() {
    	// Call test service on botsing-server on OW2 server
    	String output = botsingClient.getTestBotsingCall();

		System.out.println("Output from Server .... \n");
		System.out.println(output);
    }

    @Test
    public void testPost() throws Exception {

    	// payload
    	BotsingIssueConfig configIssue = new BotsingIssueConfig("ABC", "botsing-jira-plugin");
    	configIssue.setIssueKey("ABC-123");

    	// Call github service on botsing-server on OW2 server
    	String output = botsingClient.postBotsingIssueEventCall(configIssue);

        System.out.println("Output from Server .... \n");
		System.out.println(output);
    }

}
