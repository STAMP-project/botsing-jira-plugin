package eu.stamp_project.jira.plugins.config;

/**
 * Configuration parameters to call Botsing server
 * @author luca
 *
 */
public final class BotsingServerConfig {

	public static final String BOTSING_SERVER_CONFIG_KEY = "BotsingServerConfig";

	private String baseUrl;
	private String user;
	private String password;
	private String proxyHost;
	private String proxyPort;

	public BotsingServerConfig(String baseUrl) {
		this.baseUrl = baseUrl;
	}

	public BotsingServerConfig(String baseUrl, String user, String password, String proxyHost, String proxyPort) {
		this.baseUrl = baseUrl;
		this.user = user;
		this.password = password;
		this.proxyHost = proxyHost;
		this.proxyPort = proxyPort;
	}

	public String getBaseUrl() {
		return baseUrl;
	}

	public void setBaseUrl(String baseUrl) {
		this.baseUrl = baseUrl;
	}

	public String getUser() {
		return user;
	}

	public void setUser(String user) {
		this.user = user;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getProxyHost() {
		return proxyHost;
	}

	public void setProxyHost(String proxyHost) {
		this.proxyHost = proxyHost;
	}

	public String getProxyPort() {
		return proxyPort;
	}

	public void setProxyPort(String proxyPort) {
		this.proxyPort = proxyPort;
	}

}
