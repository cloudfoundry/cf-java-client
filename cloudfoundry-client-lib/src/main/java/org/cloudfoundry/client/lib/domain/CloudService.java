/*
 * Copyright 2009-2012 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.cloudfoundry.client.lib.domain;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import static org.cloudfoundry.client.lib.util.CloudUtil.parse;

/**
 * Class representing an instance of a service created for a user (v1) or a space (v2).
 *
 * There are some differences between the v1 and v2 information and to determine the
 * version (v1 or v2) use getMeta().getVersion()
 *
 * v1 only attributes: tier, type, vendor, options
 *
 * v2 only attributes: label, plan
 *
 * similar properties:
 *  - tier and plan
 *  - vendor and label
 *
 * @author: Thomas Risberg
 */
public class CloudService extends CloudEntity {

	private String version;
	private String provider;

	// v1 only attributes
	private String tier;
	private String type;
	private String vendor;
	private Map<String, String> options = new HashMap<String, String>();

	// v2 only attributes
	private String label;
	private String plan;

	public CloudService() {
		super();
	}

	public CloudService(Meta meta, String name) {
		super(meta, name);
	}

	/**
	 * Constructor used by v1 services
	 *
	 * @param servicesAsMap
	 */
	public CloudService(Map<String, Object> servicesAsMap) {
		super(CloudEntity.Meta.defaultMeta(), parse(servicesAsMap.get("name")));
		type = parse(servicesAsMap.get("type"));
		vendor = parse(servicesAsMap.get("vendor"));
		version = parse(servicesAsMap.get("version"));
		provider = parse(servicesAsMap.get("provider"));
		@SuppressWarnings("unchecked")
		Map<String, Object> optionsValue = parse(Map.class,
				servicesAsMap.get("options"));
		if (optionsValue != null) {
			for (Map.Entry<String, Object> entry : optionsValue.entrySet()) {
				String value = entry.getValue().toString();
				if (value != null) {
					options.put(entry.getKey(), value);
				}
			}
		}
		tier = parse(servicesAsMap.get("tier"));
		@SuppressWarnings("unchecked")
		Map<String, Object> metaValue = parse(Map.class,
				servicesAsMap.get("meta"));
		if (metaValue != null) {
			long created = parse(Long.class, metaValue.get("created"));
			Meta meta = null;
			if (created != 0) {
				meta = new Meta(null, new Date(created * 1000), null);
			}
			else {
				meta = new Meta(null, null, null);
			}
			setMeta(meta);
		}
	}

	public Map<String, String> getOptions() {
		return options;
	}

	public String getTier() {
		return tier;
	}

	public String getType() {
		return type;
	}

	public String getVendor() {
		return vendor;
	}

	public String getVersion() {
		return version;
	}

	public String getLabel() {
		return label;
	}

	public String getProvider() {
		return provider;
	}

	public String getPlan() {
		return plan;
	}

	public void setTier(String tier) {
		this.tier = tier;
	}

	public void setType(String type) {
		this.type = type;
	}

	public void setVendor(String vendor) {
		this.vendor = vendor;
	}

	public void setVersion(String version) {
		this.version = version;
	}

	public void setLabel(String label) {
		this.label = label;
	}

	public void setProvider(String provider) {
		this.provider = provider;
	}

	public void setPlan(String plan) {
		this.plan = plan;
	}
}
