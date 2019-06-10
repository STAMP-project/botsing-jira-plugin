package eu.stamp_project.jira.plugins.service;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.InetSocketAddress;
import java.net.Proxy;
import java.net.URL;

import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.WebResource;
import com.sun.jersey.api.client.config.ClientConfig;
import com.sun.jersey.api.client.config.DefaultClientConfig;
import com.sun.jersey.client.urlconnection.HttpURLConnectionFactory;
import com.sun.jersey.client.urlconnection.URLConnectionClientHandler;

public class BotsingServerClient {


	public static void main(String[] args) {
		try {

			ClientConfig config = new DefaultClientConfig();
			Client client = new Client(new URLConnectionClientHandler(new HttpURLConnectionFactory() {
				Proxy p = null;

				@Override
				public HttpURLConnection getHttpURLConnection(URL url) throws IOException {
					if (p == null) {
						p = new Proxy(Proxy.Type.HTTP, new InetSocketAddress("proxy.eng.it", 3128));
					}
					return (HttpURLConnection) url.openConnection(p);
				}
			}), config);

			WebResource webResource = client.resource("http://178.170.72.39:5000/test");

			ClientResponse response = webResource.accept("application/json")
	                   .get(ClientResponse.class);


//	        ClientResponse response = resource1.type(MediaType.APPLICATION_JSON_TYPE).post(ClientResponse.class, newBlog);
//	        assertThat(response.getStatus(), is(Response.Status.CREATED.getStatusCode()));

			if (response.getStatus() != 200) {
			   throw new RuntimeException("Failed : HTTP error code : "
				+ response.getStatus());
			}

			String output = response.getEntity(String.class);

			System.out.println("Output from Server .... \n");
			System.out.println(output);

		  } catch (Exception e) {

			e.printStackTrace();

		  }

		}

	public void callBotsingServer() {
		Client client = Client.create();

	}


}
