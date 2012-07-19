/*
 * Copyright 2009-2011 the original author or authors.
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

public class CloudService {

	public static class Meta {

		private Date created;
		private int version;

		public Date getCreated() {
			return created;
		}

		public int getVersion() {
			return version;
		}
	}

	private Meta meta;
	private String name;
	private Map<String, String> options = new HashMap<String, String>();
	private String tier;
	private String type;
	private String vendor;
	private String version;

	public CloudService(Map<String, Object> servicesAsMap) {
		type = parse(servicesAsMap.get("type"));
		vendor = parse(servicesAsMap.get("vendor"));
		version = parse(servicesAsMap.get("version"));
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
		name = parse(servicesAsMap.get("name"));
		@SuppressWarnings("unchecked")
		Map<String, Object> metaValue = parse(Map.class,
				servicesAsMap.get("meta"));
		if (metaValue != null) {
			meta = new Meta();
			meta.version = parse(Integer.class, metaValue.get("version"));
			long created = parse(Long.class, metaValue.get("created"));
			if (created != 0) {
				meta.created = new Date(created * 1000);
			}
		}
	}

	public CloudService() {
	}

	public Meta getMeta() {
		return meta;
	}

	public String getName() {
		return name;
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

	public void setName(String name) {
		this.name = name;
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
}
