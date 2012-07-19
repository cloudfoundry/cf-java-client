package org.cloudfoundry.client.lib.cloud;

/**
 * @author: Thomas Risberg
 */
public class ServiceInstanceInfo extends BaseInfo {

	private String vendor;
	private ServicePlanInfo servicePlanInfo;

	public String getVendor() {
		return vendor;
	}

	public void setVendor(String vendor) {
		this.vendor = vendor;
	}

	public ServicePlanInfo getServicePlanInfo() {
		return servicePlanInfo;
	}

	public void setServicePlanInfo(ServicePlanInfo servicePlanInfo) {
		this.servicePlanInfo = servicePlanInfo;
	}

}
