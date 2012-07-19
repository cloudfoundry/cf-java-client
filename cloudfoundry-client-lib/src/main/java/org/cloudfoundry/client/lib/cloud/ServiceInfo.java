package org.cloudfoundry.client.lib.cloud;

/**
 * @author: Thomas Risberg
 */
public class ServiceInfo extends BaseInfo {

	private String label;
	private String provider;
	private String version;

	public String getLabel() {
		return label;
	}

	public void setLabel(String label) {
		this.label = label;
	}

	public String getProvider() {
		return provider;
	}

	public void setProvider(String provider) {
		this.provider = provider;
	}

	public String getVersion() {
		return version;
	}

	public void setVersion(String version) {
		this.version = version;
	}
}
