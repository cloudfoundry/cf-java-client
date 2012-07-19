package org.cloudfoundry.client.lib.cloud;

/**
 * @author: Thomas Risberg
 */
public class SpaceInfo extends BaseInfo {

	@Override
	public String toString() {
		return this.getClass().getSimpleName() + ": " + getName() + " [" + getGuid() + "]";
	}
}
