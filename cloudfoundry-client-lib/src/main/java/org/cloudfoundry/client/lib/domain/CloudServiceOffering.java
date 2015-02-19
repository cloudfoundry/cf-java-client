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

import java.util.ArrayList;
import java.util.List;

public class CloudServiceOffering extends CloudEntity {

	//Note name is used for label
	private String provider;
	private String version;
	private String description;
	private boolean active;
	private boolean bindable;
	private String url;
	private String infoUrl;
	private String uniqueId;
	private String extra;
	private String docUrl;
	
	private List<CloudServicePlan> cloudServicePlans = new ArrayList<CloudServicePlan>();

	public CloudServiceOffering(Meta meta, String name) {
		super(meta, name);
	}

	public CloudServiceOffering(Meta meta, String name, String provider, String version) {
		super(meta, name);
		this.provider = provider;
		this.version = version;
	}

	public CloudServiceOffering(Meta meta, String name, String provider, String version, 
								String description, boolean active, boolean bindable,
								String url, String infoUrl, String uniqueId, String extra,
								String docUrl) {
		super(meta, name);
		this.provider = provider;
		this.version = version;
		this.description = description;
		this.active = active;
		this.bindable = bindable;
		this.url = url;
		this.infoUrl = infoUrl;
		this.uniqueId = uniqueId;
		this.extra = extra;
		this.docUrl = docUrl;
	}

	public String getLabel() {
		return getName();
	}

	public String getDescription() {
		return description;
	}

	public String getProvider() {
		return provider;
	}

	public String getVersion() {
		return version;
	}

	public boolean isActive() {
		return active;
	}

	public boolean isBindable() {
		return bindable;
	}

	public String getUrl() {
		return url;
	}

	public String getInfoUrl() {
		return infoUrl;
	}

	public String getUniqueId() {
		return uniqueId;
	}

	public String getExtra() {
		return extra;
	}

	public String getDocumentationUrl() {
		return docUrl;
	}

	public List<CloudServicePlan> getCloudServicePlans() {
		return cloudServicePlans;
	}

	public void addCloudServicePlan(CloudServicePlan cloudServicePlan) {
		this.cloudServicePlans.add(cloudServicePlan);
	}
}
