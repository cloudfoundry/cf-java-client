package org.cloudfoundry.client.lib.cloud;

/**
 * @author: Thomas Risberg
 */
public class ServiceInfo extends BaseInfo {

	private String type;
	private String vendor;
	private String version;
	private ServicePlanInfo servicePlanInfo;

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getVendor() {
		return vendor;
	}

	public void setVendor(String vendor) {
		this.vendor = vendor;
	}

	public String getVersion() {
		return version;
	}

	public void setVersion(String version) {
		this.version = version;
	}

	public ServicePlanInfo getServicePlanInfo() {
		return servicePlanInfo;
	}

	public void setServicePlanInfo(ServicePlanInfo servicePlanInfo) {
		this.servicePlanInfo = servicePlanInfo;
	}

}
