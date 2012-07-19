package org.cloudfoundry.client.lib.transfer;

/**
 * @author: Thomas Risberg
 */
public class ServicePlanInfo extends BaseInfo {

	String description;

	ServiceInfo serviceInfo;

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public ServiceInfo getServiceInfo() {
		return serviceInfo;
	}

	public void setServiceInfo(ServiceInfo serviceInfo) {
		this.serviceInfo = serviceInfo;
	}
}
