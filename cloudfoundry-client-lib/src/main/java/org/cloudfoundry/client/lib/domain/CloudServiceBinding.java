package org.cloudfoundry.client.lib.domain;

import java.util.Map;
import java.util.UUID;

/**
 * Class representing the binding of a service instance.
 *
 * @author Scott Frederick
 */
public class CloudServiceBinding extends CloudEntity {
	private UUID appGuid;
	private Map<String, Object> credentials;
	private Map<String, Object> bindingOptions;
	private String syslogDrainUrl;

	public CloudServiceBinding() {
		super();
	}

	public CloudServiceBinding(Meta meta, String name) {
		super(meta, name);
	}

	public UUID getAppGuid() {
		return appGuid;
	}

	public void setAppGuid(UUID appGuid) {
		this.appGuid = appGuid;
	}

	public Map<String, Object> getCredentials() {
		return credentials;
	}

	public void setCredentials(Map<String, Object> credentials) {
		this.credentials = credentials;
	}

	public Map<String, Object> getBindingOptions() {
		return bindingOptions;
	}

	public void setBindingOptions(Map<String, Object> bindingOptions) {
		this.bindingOptions = bindingOptions;
	}

	public String getSyslogDrainUrl() {
		return syslogDrainUrl;
	}

	public void setSyslogDrainUrl(String syslogDrainUrl) {
		this.syslogDrainUrl = syslogDrainUrl;
	}
}
