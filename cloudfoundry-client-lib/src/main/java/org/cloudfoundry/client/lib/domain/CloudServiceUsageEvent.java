/**
 * 
 */
package org.cloudfoundry.client.lib.domain;

import java.util.UUID;

/**
 * @author Christian Brinker, evoila.
 *
 */
public class CloudServiceUsageEvent extends CloudEntity {
	private InstanceState state;
	private UUID orgGUID;
	private UUID spaceGUID;
	private String spaceName;
	private UUID serviceInstanceGUID;
	private String serviceInstanceName;
	private ServiceInstanceType serviceInstanceType;
	private UUID servicePlanGUID;
	private String servicePlanName;
	private UUID serviceGUID;
	private String serviceLabel;
	
	public CloudServiceUsageEvent(Meta meta, String name) {
		super(meta, name);
	}

	public static enum ServiceInstanceType {
		MANAGED_SERVICE_INSTANCE, USER_PROVIDED_SERVICE_INSTANCE, UNKNOWN;

		public static ServiceInstanceType valueOfWithDefault(String type) {
			try {
				return ServiceInstanceType.valueOf(type);
			} catch (IllegalArgumentException e) {
				return ServiceInstanceType.UNKNOWN;
			}
		}
	}

	public InstanceState getState() {
		return state;
	}

	public void setState(InstanceState state) {
		this.state = state;
	}

	public UUID getOrgGUID() {
		return orgGUID;
	}

	public void setOrgGUID(UUID orgGUID) {
		this.orgGUID = orgGUID;
	}

	public UUID getSpaceGUID() {
		return spaceGUID;
	}

	public void setSpaceGUID(UUID spaceGUID) {
		this.spaceGUID = spaceGUID;
	}

	public String getSpaceName() {
		return spaceName;
	}

	public void setSpaceName(String spaceName) {
		this.spaceName = spaceName;
	}

	public UUID getServiceInstanceGUID() {
		return serviceInstanceGUID;
	}

	public void setServiceInstanceGUID(UUID serviceInstanceGUID) {
		this.serviceInstanceGUID = serviceInstanceGUID;
	}

	public String getServiceInstanceName() {
		return serviceInstanceName;
	}

	public void setServiceInstanceName(String serviceInstanceName) {
		this.serviceInstanceName = serviceInstanceName;
	}

	public ServiceInstanceType getServiceInstanceType() {
		return serviceInstanceType;
	}

	public void setServiceInstanceType(ServiceInstanceType serviceInstanceType) {
		this.serviceInstanceType = serviceInstanceType;
	}

	public UUID getServicePlanGUID() {
		return servicePlanGUID;
	}

	public void setServicePlanGUID(UUID servicePlanGUID) {
		this.servicePlanGUID = servicePlanGUID;
	}

	public String getServicePlanName() {
		return servicePlanName;
	}

	public void setServicePlanName(String servicePlanName) {
		this.servicePlanName = servicePlanName;
	}

	public UUID getServiceGUID() {
		return serviceGUID;
	}

	public void setServiceGUID(UUID serviceGUID) {
		this.serviceGUID = serviceGUID;
	}

	public String getServiceLabel() {
		return serviceLabel;
	}

	public void setServiceLabel(String serviceLabel) {
		this.serviceLabel = serviceLabel;
	}
}
