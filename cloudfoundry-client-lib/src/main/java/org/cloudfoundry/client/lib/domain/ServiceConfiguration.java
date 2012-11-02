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

import org.cloudfoundry.client.lib.util.CloudUtil;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class ServiceConfiguration {

	public static class Option {

		private String description;
		private final String name;
		private Map<String, Integer> priceByValue = new LinkedHashMap<String, Integer>();
		private String type;

		public Option(String name, Map<String, Object> attributes) {
			this.name = name;
			this.description = CloudUtil.parse(attributes.get("description"));
			this.type = CloudUtil.parse(attributes.get("type"));
			@SuppressWarnings("unchecked")
			List<String> list = CloudUtil.parse(List.class,
					attributes.get("values"));
			if (list != null) {
				for (String value : list) {
					priceByValue.put(value, null);
				}
			}
		}

		public String getDescription() {
			return description;
		}

		public String getName() {
			return name;
		}

		public Map<String, Integer> getPriceByValue() {
			return priceByValue;
		}

		public String getType() {
			return type;
		}
	}

	public static class Tier {

		private String description;
		private List<Option> options = new ArrayList<ServiceConfiguration.Option>();
		private int order;
		private String pricingPeriod;
		private String pricingType;
		private String type;

		public Tier(String type, Map<String, Object> attributes) {
			this.type = type;
			order = CloudUtil.parse(Integer.class, attributes.get("order"));
			description = CloudUtil.parse(attributes.get("description"));
			@SuppressWarnings("unchecked")
			Map<String, Object> optionsMap = CloudUtil.parse(Map.class,
					attributes.get("options"));
			if (optionsMap != null) {
				for (Map.Entry<String, Object> entry : optionsMap.entrySet()) {
					@SuppressWarnings("unchecked")
					Map<String, Object> optionAsMap = CloudUtil.parse(
							Map.class, entry.getValue());
					if (optionAsMap != null) {
						options.add(new Option(entry.getKey(), optionAsMap));
					}
				}
			}
			@SuppressWarnings("unchecked")
			Map<String, Object> pricingMap = CloudUtil.parse(Map.class,
					attributes.get("pricing"));
			if (pricingMap != null) {
				pricingPeriod = CloudUtil.parse(pricingMap.get("period"));
				pricingType = CloudUtil.parse(pricingMap.get("type"));
				// XXX how are options and prices associated?
				for (Option option : options) {
					@SuppressWarnings("unchecked")
					Map<String, Object> pricingValuesMap = CloudUtil.parse(
							Map.class, pricingMap.get("values"));
					if (pricingValuesMap != null) {
						for (Map.Entry<String, Object> entry : pricingValuesMap
								.entrySet()) {
							if (entry.getValue() instanceof Integer
									&& option.priceByValue.containsKey(entry.getKey())) {
								option.priceByValue.put(entry.getKey(), (Integer) entry.getValue());
							}
						}
					}
				}
			}
		}

		public String getDescription() {
			return description;
		}

		public List<Option> getOptions() {
			return options;
		}

		public int getOrder() {
			return order;
		}

		public String getPricingPeriod() {
			return pricingPeriod;
		}

		public String getPricingType() {
			return pricingType;
		}

		public String getType() {
			return type;
		}
	}

	private String description;
	private String version;
	private CloudEntity.Meta meta;

	// v1 only attributes
	private List<Tier> tiers = new ArrayList<ServiceConfiguration.Tier>();
	private String type;
	private String vendor;

	// v2 only attributes
	private CloudServiceOffering cloudServiceOffering;

	/**
	 * Constructor used by v1 services
	 *
	 * @param attributes
	 */
	public ServiceConfiguration(Map<String, Object> attributes) {
		this.meta = new CloudEntity.Meta(null, null, null);
		type = CloudUtil.parse(attributes.get("type"));
		version = CloudUtil.parse(attributes.get("version"));
		vendor = CloudUtil.parse(attributes.get("vendor"));
		description = CloudUtil.parse(attributes.get("description"));
		@SuppressWarnings("unchecked")
		Map<String, Object> tiersAsMap = CloudUtil.parse(Map.class,
				attributes.get("tiers"));
		if (tiersAsMap != null) {
			for (Map.Entry<String, Object> tierEntry : tiersAsMap.entrySet()) {
				@SuppressWarnings("unchecked")
				Map<String, Object> tierMap = CloudUtil.parse(Map.class,
						tierEntry.getValue());
				if (tierMap != null) {
					tiers.add(new Tier(tierEntry.getKey(), tierMap));
				}
			}
		}
	}

	public ServiceConfiguration(CloudServiceOffering cloudServiceOffering) {
		this.cloudServiceOffering = cloudServiceOffering;
		this.meta = cloudServiceOffering.getMeta();
		this.version = cloudServiceOffering.getVersion();
		this.description = cloudServiceOffering.getDescription();
	}

	public String getDescription() {
		return description;
	}

	public List<Tier> getTiers() {
		return tiers;
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

	public CloudServiceOffering getCloudServiceOffering() {
		return cloudServiceOffering;
	}

	public CloudEntity.Meta getMeta() {
		return meta;
	}
}
