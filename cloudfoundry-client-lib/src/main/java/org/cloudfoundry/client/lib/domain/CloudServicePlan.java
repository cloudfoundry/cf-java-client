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

public class CloudServicePlan extends CloudEntity {

	private CloudServiceOffering serviceOffering;

	public CloudServicePlan() {
	}

	public CloudServicePlan(Meta meta, String name, CloudServiceOffering serviceOffering) {
		super(meta, name);
		this.serviceOffering = serviceOffering;
	}

	public CloudServiceOffering getServiceOffering() {
		return serviceOffering;
	}

	public void setServiceOffering(CloudServiceOffering serviceOffering) {
		this.serviceOffering = serviceOffering;
	}
}
