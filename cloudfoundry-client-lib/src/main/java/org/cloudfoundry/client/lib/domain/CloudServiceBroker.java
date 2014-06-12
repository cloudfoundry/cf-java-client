package org.cloudfoundry.client.lib.domain;

public class CloudServiceBroker extends CloudEntity {
	private String url;
	private String username;

	public CloudServiceBroker(String url, String username) {
		this.url = url;
		this.username = username;
	}

	public CloudServiceBroker(Meta meta, String name, String url, String username) {
		super(meta, name);
		this.url = url;
		this.username = username;
	}

	public String getUrl() {
		return url;
	}

	public String getUsername() {
		return username;
	}
}
