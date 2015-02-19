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
 * @author Thomas Risberg
 */
public class CloudOrganization extends CloudEntity {

	private boolean billingEnabled = false;
	private CloudQuota quota;

	public CloudOrganization(Meta meta, String name) {
		this(meta, name, false);
	}

	public CloudOrganization(Meta meta, String name, boolean billingEnabled) {
		super(meta, name);
		this.billingEnabled = billingEnabled;
	}

	public CloudOrganization(Meta meta, String name, CloudQuota quota, boolean billingEnabled) {
		super(meta, name);
		this.quota=quota;
		this.billingEnabled = billingEnabled;
	}

	public boolean isBillingEnabled() {
		return billingEnabled;
	}

	public CloudQuota getQuota() {
		return quota;
	}

	public void setQuota(CloudQuota quota) {
		this.quota = quota;
	}
}
