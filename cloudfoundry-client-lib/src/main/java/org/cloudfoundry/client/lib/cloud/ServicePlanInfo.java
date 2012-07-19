package org.cloudfoundry.client.lib.cloud;

/**
 * @author: Thomas Risberg
 */
public class ServicePlanInfo extends BaseInfo {

	String description;

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}
}
