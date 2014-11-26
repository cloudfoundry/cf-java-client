package org.cloudfoundry.client.lib.domain;

/**
 * @author Olivier Orand
 */
public class CloudUser extends CloudEntity{
	
	private CloudOrganization organization;

	public CloudUser(Meta meta, String name, CloudOrganization organization) {
		super(meta, name);
		this.organization = organization;
	}

	public CloudOrganization getOrganization() {
		return organization;
	}

}
