package eu.stamp_project.jira.plugins;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.InetSocketAddress;
import java.net.Proxy;
import java.net.URL;

import javax.ws.rs.core.MediaType;

import com.google.gson.Gson;
import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.WebResource;
import com.sun.jersey.api.client.config.ClientConfig;
import com.sun.jersey.api.client.config.DefaultClientConfig;
import com.sun.jersey.client.urlconnection.HttpURLConnectionFactory;
import com.sun.jersey.client.urlconnection.URLConnectionClientHandler;

import eu.stamp_project.jira.plugins.config.BotsingConfigIssue;

/**
 * Class to call the Botsing server.
 * @author luca
 *
 */
public class BotsingClient {

	private static final String JIRA_EVENT_HEADER_PARAM = "X-Jira-Event";
	private static final String JIRA_NEW_BOTSING_ISSUE_EVENT = "newBotsingIssueEvent";

	private static final String BOTSING_SERVER_JIRA_URL = "/jira/botsing-jira-app";
	private static final String BOTSING_SERVER_TEST_URL = "/jira/test";

	private Client client;

	private String baseUrl;

	private final Gson gson = new Gson();

	public BotsingClient(String baseUrl) {
		this.baseUrl = baseUrl;
		initClient(null, null);
	}

	public BotsingClient(String baseUrl, String proxyHost, Integer proxyPort) {
		this.baseUrl = baseUrl;
		initClient(proxyHost, proxyPort);
	}

	private void initClient(String proxyHost, Integer proxyPort) {
		ClientConfig config = new DefaultClientConfig();
		client = new Client(new URLConnectionClientHandler(new HttpURLConnectionFactory() {
			Proxy p = null;

			@Override
			public HttpURLConnection getHttpURLConnection(URL url) throws IOException {

				if (proxyHost != null && proxyPort != null) {
					if (p == null) {
						p = new Proxy(Proxy.Type.HTTP, new InetSocketAddress(proxyHost, proxyPort));
					}
					return (HttpURLConnection) url.openConnection(p);

				} else {
					return (HttpURLConnection) url.openConnection();
				}

			}
		}), config);
	}

	public void destroy() {
		if (client != null) {
			client.destroy();
		}
	}

	public String getTestBotsingCall() {
		WebResource webResource = client.resource(baseUrl + BOTSING_SERVER_TEST_URL);

		ClientResponse response = webResource.accept(MediaType.APPLICATION_JSON_TYPE).get(ClientResponse.class);

		if (response.getStatus() != 200) {
			throw new RuntimeException("Failed : HTTP error code : " + response.getStatus());
		}

		return response.getEntity(String.class);
	}

	/**
	 * Make a call to the Botsing server
	 * @param url
	 * @param issueConfig
	 * @return
	 */
	public String postBotsingIssueEventCall(BotsingConfigIssue issueConfig) {
		WebResource webResource = client.resource(baseUrl + BOTSING_SERVER_JIRA_URL);
		webResource.header(JIRA_EVENT_HEADER_PARAM, JIRA_NEW_BOTSING_ISSUE_EVENT);

		ClientResponse response = webResource.type(MediaType.APPLICATION_JSON_TYPE).post(ClientResponse.class, gson.toJson(issueConfig));

		if (response.getStatus() != 200) {
			throw new RuntimeException("Failed : HTTP error code : " + response.getStatus());
		}

		// this method does not expect any specific response, because the server should accept the payload and put it in a queue

		return response.getEntity(String.class);
	}


}
