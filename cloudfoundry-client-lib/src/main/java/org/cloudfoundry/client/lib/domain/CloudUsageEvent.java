/**
 * 
 */
package org.cloudfoundry.client.lib.domain;

import java.util.UUID;

import org.codehaus.jackson.annotate.JsonAutoDetect;
import org.codehaus.jackson.annotate.JsonAutoDetect.Visibility;

/**
 * @author Christian Brinker, evoila.
 *
 */
@JsonAutoDetect(fieldVisibility = Visibility.ANY, getterVisibility = Visibility.NONE, creatorVisibility = Visibility.NONE)
public class CloudUsageEvent extends CloudEntity{
	private CloudApplication.AppState state;
	private int memoryInMBPerInstance;
	private int instanceCount;
	private UUID appGUID;
	private String appName;
	private UUID spaceGUID;
	private String spaceName;
	private UUID orgGUID;
	
	public CloudUsageEvent(Meta meta, String name) {
		super(meta, name);
	}

	public CloudApplication.AppState getState() {
		return state;
	}

	public void setState(CloudApplication.AppState state) {
		this.state = state;
	}

	public int getMemoryInMBPerInstance() {
		return memoryInMBPerInstance;
	}

	public void setMemoryInMBPerInstance(int memoryInMBPerInstance) {
		this.memoryInMBPerInstance = memoryInMBPerInstance;
	}

	public int getInstanceCount() {
		return instanceCount;
	}

	public void setInstanceCount(int instanceCount) {
		this.instanceCount = instanceCount;
	}

	public UUID getAppGUID() {
		return appGUID;
	}

	public void setAppGUID(UUID appGUID) {
		this.appGUID = appGUID;
	}

	public String getAppName() {
		return appName;
	}

	public void setAppName(String appName) {
		this.appName = appName;
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

	public UUID getOrgGUID() {
		return orgGUID;
	}

	public void setOrgGUID(UUID orgGUID) {
		this.orgGUID = orgGUID;
	}
}
