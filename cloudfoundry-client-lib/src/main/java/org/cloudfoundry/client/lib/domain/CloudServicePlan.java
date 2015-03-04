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


public class CloudServicePlan extends CloudEntity {

	private boolean free;
	private boolean _public;
	private String description;
	private String extra;
	private String uniqueId;
	
	private CloudServiceOffering serviceOffering;

	public CloudServicePlan() {
	}

	public CloudServicePlan(Meta meta, String name) {
		super(meta, name);
	}

	public CloudServicePlan(Meta meta, String name, String description, boolean free,
							boolean _public, String extra, String uniqueId) {
		super(meta, name);
		this.description = description;
		this.free = free;
		this._public = _public;
		this.extra = extra;
		this.uniqueId = uniqueId;
	}

	public boolean isFree() {
		return this.free;
	}
	
	public boolean isPublic() {
		return this._public;
	}
	
	public String getDescription() {
		return description;
	}
	
	public String getExtra() {
		return extra;
	}
	
	public String getUniqueId() {
		return uniqueId;
	}
	
	public CloudServiceOffering getServiceOffering() {
		return serviceOffering;
	}

	public void setServiceOffering(CloudServiceOffering serviceOffering) {
		this.serviceOffering = serviceOffering;
	}
}
