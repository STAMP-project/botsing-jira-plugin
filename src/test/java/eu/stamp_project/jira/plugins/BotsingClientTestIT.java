package eu.stamp_project.jira.plugins;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import eu.stamp_project.jira.plugins.config.BotsingIssueConfig;
import eu.stamp_project.jira.plugins.config.BotsingProjectConfig;

/**
 * Class to test calls to the Botsing server<br/>
 * Needs to run the server before running the test.
 *
 * @author luca
 *
 */
public class BotsingClientTestIT {

	private BotsingClient botsingClient;

	private String testServerEndpoint = "http://localhost:5000"; // "http://178.170.72.39:5000";

	private String authzforceSimpleRuntimeException = "java.lang.RuntimeException: Failed to load XML schemas: [classpath:pdp.xsd]\n" +
			"at org.ow2.authzforce.core.pdp.impl.SchemaHandler.createSchema(SchemaHandler.java:541)\n" +
			"at org.ow2.authzforce.core.pdp.impl.PdpModelHandler.(PdpModelHandler.java:159)\n" +
			"at org.ow2.authzforce.core.pdp.impl.PdpEngineConfiguration.getInstance(PdpEngineConfiguration.java:682)\n" +
			"at org.ow2.authzforce.core.pdp.impl.PdpEngineConfiguration.getInstance(PdpEngineConfiguration.java:699)\n" +
			"at sun.reflect.NativeMethodAccessorImpl.invoke(NativeMethodAccessorImpl.java:62)\n" +
			"at sun.reflect.DelegatingMethodAccessorImpl.invoke(DelegatingMethodAccessorImpl.java:43)\n" +
			"at java.lang.reflect.Method.invoke(Method.java:498)\n" +
			"at org.springframework.beans.factory.support.SimpleInstantiationStrategy.instantiate(SimpleInstantiationStrategy.java:162)";

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
    	BotsingProjectConfig configProject = new BotsingProjectConfig("PL", "org.ow2.authzforce", "authzforce-ce-core-pdp-testutils", "13.3.1",
    			60, 90, 100, "org.ow2.authzforce", true);
    	BotsingIssueConfig configIssue = new BotsingIssueConfig(configProject, "PL-23", authzforceSimpleRuntimeException);

    	// Call github service on botsing-server on OW2 server
    	String output = botsingClient.postBotsingIssueEventCall(configIssue);

        System.out.println("Output from Server .... \n");
		System.out.println(output);
    }

}
