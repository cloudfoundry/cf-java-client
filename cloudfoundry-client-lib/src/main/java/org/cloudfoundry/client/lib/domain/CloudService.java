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


/**
 * Class representing service plan info for a service instance.
 *
 * @author Thomas Risberg
 */
public class CloudService extends CloudEntity {

	private String version;
	private String provider;

	private String label;
	private String plan;

	public CloudService() {
		super();
	}

	public CloudService(Meta meta, String name) {
		super(meta, name);
	}

	public boolean isUserProvided() {
		return plan == null && provider == null && version == null;
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
