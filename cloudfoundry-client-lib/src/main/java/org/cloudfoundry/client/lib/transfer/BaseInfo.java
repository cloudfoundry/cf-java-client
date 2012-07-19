package org.cloudfoundry.client.lib.transfer;

import java.util.UUID;

/**
 * @author: Thomas Risberg
 */
public abstract class BaseInfo {

	private UUID guid;
	private String name;

	public UUID getGuid() {
		return guid;
	}

	public void setGuid(UUID guid) {
		this.guid = guid;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
}
