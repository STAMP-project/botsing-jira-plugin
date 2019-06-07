package eu.stamp_project.jira.plugins.config;

public final class BotsingConfig {

	public static final String BOTSING_CONFIG_KEY = "BotsingConfig";

	public static final String PROJECT_KEY_SUFFIX = ".project_key";
	public static final String GROUP_ID_SUFFIX = ".group_id";
	public static final String ARTIFACT_ID_SUFFIX = ".artifact_id";
	public static final String VERSION_SUFFIX = ".version";
	public static final String SEARCH_BUDGET_SUFFIX = ".search_budget";
	public static final String GLOBAL_TIMEOUT_SUFFIX = ".global_timeout";
	public static final String POPULATION_SUFFIX = ".population";
	public static final String PACKAGE_FILTER_SUFFIX = ".package_filter";

	private String projectKey;
	private String groupId;
	private String artifactId;
	private String version;
	private int searchBudget;
	private int globalTimeout;
	private int population;
	private String packageFilter;
	private Boolean enabled;

	public BotsingConfig(String projectKey, String artifactId) {
		super();
		this.projectKey = projectKey;
		this.artifactId = artifactId;
	}

	public BotsingConfig(String projectKey, String groupId, String artifactId, String version, int searchBudget,
			int globalTimeout, int population, String packageFilter, Boolean enabled) {
		super();
		this.projectKey = projectKey;
		this.groupId = groupId;
		this.artifactId = artifactId;
		this.version = version;
		this.searchBudget = searchBudget;
		this.globalTimeout = globalTimeout;
		this.population = population;
		this.packageFilter = packageFilter;
		this.enabled = enabled;
	}

	public String getProjectKey() {
		return projectKey;
	}

	public void setProjectKey(String projectKey) {
		this.projectKey = projectKey;
	}

	public String getGroupId() {
		return groupId;
	}

	public void setGroupId(String groupId) {
		this.groupId = groupId;
	}

	public String getArtifactId() {
		return artifactId;
	}

	public void setArtifactId(String artifactId) {
		this.artifactId = artifactId;
	}

	public String getVersion() {
		return version;
	}

	public void setVersion(String version) {
		this.version = version;
	}

	public int getSearchBudget() {
		return searchBudget;
	}

	public void setSearchBudget(int searchBudget) {
		this.searchBudget = searchBudget;
	}

	public int getGlobalTimeout() {
		return globalTimeout;
	}

	public void setGlobalTimeout(int globalTimeout) {
		this.globalTimeout = globalTimeout;
	}

	public int getPopulation() {
		return population;
	}

	public void setPopulation(int population) {
		this.population = population;
	}

	public String getPackageFilter() {
		return packageFilter;
	}

	public void setPackageFilter(String packageFilter) {
		this.packageFilter = packageFilter;
	}

	public Boolean getEnabled() {
		return enabled;
	}

	public void setEnabled(Boolean enabled) {
		this.enabled = enabled;
	}

	@Override
	public String toString() {
		return "BotsingConfig [projectKey=" + projectKey + ", groupId=" + groupId + ", artifactId=" + artifactId
				+ ", version=" + version + ", searchBudget=" + searchBudget + ", globalTimeout=" + globalTimeout
				+ ", population=" + population + ", packageFilter=" + packageFilter + ", enabled=" + enabled + "]";
	}

}
