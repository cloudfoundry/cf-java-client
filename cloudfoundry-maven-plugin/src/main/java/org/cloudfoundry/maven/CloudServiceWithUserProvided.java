package org.cloudfoundry.maven;

import org.cloudfoundry.client.lib.domain.CloudService;

import java.util.Map;

public class CloudServiceWithUserProvided extends CloudService {
	private Map<String, Object> userProvidedCredentials;

	public CloudServiceWithUserProvided() {
		super();
	}

	public Map<String, Object> getUserProvidedCredentials() {
		return userProvidedCredentials;
	}

	public void setUserProvidedCredentials(Map<String, Object> userProvidedCredentials) {
		this.userProvidedCredentials = userProvidedCredentials;
	}
}
