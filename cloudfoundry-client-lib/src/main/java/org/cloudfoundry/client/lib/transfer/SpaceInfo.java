package org.cloudfoundry.client.lib.transfer;

/**
 * @author: Thomas Risberg
 */
public class SpaceInfo extends BaseInfo {

	@Override
	public String toString() {
		return this.getClass().getSimpleName() + ": " + getName() + " [" + getGuid() + "]";
	}
}
