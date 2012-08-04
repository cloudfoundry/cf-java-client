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

	private String description;

	private List<CloudServicePlan> cloudServicePlans = new ArrayList<CloudServicePlan>();

	public CloudServiceOffering() {
	}

	public CloudServiceOffering(Meta meta, String name) {
		super(meta, name);
	}

	public String getLabel() {
		return getName();
	}

	public void setLabel(String label) {
		setName(label);
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public List<CloudServicePlan> getCloudServicePlans() {
		return cloudServicePlans;
	}

	public void addCloudServicePlan(CloudServicePlan cloudServicePlan) {
		this.cloudServicePlans.add(cloudServicePlan);
	}
}
