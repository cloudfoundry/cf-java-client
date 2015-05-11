package org.cloudfoundry.client.lib.domain;

/**
 * @author Olivier Orand
 */
public class CloudUser extends CloudEntity{

	private boolean admin;
	private boolean active;
	private String defaultSpaceGuid;
	private String username;

	private CloudOrganization organization;

	public boolean isAdmin() {
		return admin;
	}

	public boolean isActive() {
		return active;
	}

	public String getDefaultSpaceGuid() {
		return defaultSpaceGuid;
	}

	public String getUsername() {
		return username;
	}

	public CloudUser(Meta meta, String username, boolean admin, boolean active, String defaultSpaceGuid) {
		super(meta, username);
		this.username=username;
		this.admin=admin;
		this.active=active;

		this.defaultSpaceGuid =defaultSpaceGuid;
		//this.organization = organization;
	}

	public CloudOrganization getOrganization() {
		return organization;
	}

}
