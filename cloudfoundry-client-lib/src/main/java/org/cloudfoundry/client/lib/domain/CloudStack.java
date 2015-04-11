package org.cloudfoundry.client.lib.domain;

public class CloudStack  extends CloudEntity {
	private String description;

	public CloudStack(Meta meta, String name, String description) {
		super(meta, name);
		this.description = description;
	}

	public String getDescription() {
		return description;
	}
}
