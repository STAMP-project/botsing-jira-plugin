package eu.stamp_project.jira.plugins.service;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.InetSocketAddress;
import java.net.Proxy;
import java.net.URL;

import javax.ws.rs.core.MediaType;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.google.gson.Gson;
import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.WebResource;
import com.sun.jersey.api.client.config.ClientConfig;
import com.sun.jersey.api.client.config.DefaultClientConfig;
import com.sun.jersey.client.urlconnection.HttpURLConnectionFactory;
import com.sun.jersey.client.urlconnection.URLConnectionClientHandler;

import eu.stamp_project.jira.plugins.config.BotsingConfigIssue;

public class BotsingServerClientTest {

	private Client client;
	private final Gson gson = new Gson();

    @Before
    public void setUp() {
		ClientConfig config = new DefaultClientConfig();
		client = new Client(new URLConnectionClientHandler(new HttpURLConnectionFactory() {
			Proxy p = null;

			@Override
			public HttpURLConnection getHttpURLConnection(URL url) throws IOException {
				if (p == null) {
					p = new Proxy(Proxy.Type.HTTP, new InetSocketAddress("proxy.eng.it", 3128));
				}
				return (HttpURLConnection) url.openConnection(p);
			}
		}), config);
    }

    @After
    public void tearDownTest() {
        client.destroy();
    }

    @Test
    public void simpleGetCall() {
    	// Call test service on botsing-server on OW2 server
	    WebResource webResource = client.resource("http://178.170.72.39:5000/test");

		ClientResponse response = webResource.accept(MediaType.APPLICATION_JSON_TYPE).get(ClientResponse.class);

		if (response.getStatus() != 200) {
		   throw new RuntimeException("Failed : HTTP error code : "
			+ response.getStatus());
		}

		String output = response.getEntity(String.class);

		System.out.println("Output from Server .... \n");
		System.out.println(output);
    }

    @Test
    public void testPost() throws Exception {
    	// Call github service on botsing-server on OW2 server
    	WebResource webResource = client.resource("http://178.170.72.39:5000/botsing-github-app");

    	// payload
    	BotsingConfigIssue configIssue = new BotsingConfigIssue("ABC", "botsing-jira-plugin");
    	configIssue.setIssueKey("ABC-123");

        ClientResponse response = webResource.type(MediaType.APPLICATION_JSON_TYPE).post(ClientResponse.class, gson.toJson(configIssue));

        System.out.println(response.getStatus());

        System.out.println("Output from Server .... \n");
		System.out.println(response.getEntity(String.class));
    }

}
