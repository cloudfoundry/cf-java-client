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
public class CloudDomain extends CloudEntity {

	private CloudOrganization owner;

	public CloudDomain(Meta meta, String name, CloudOrganization owner) {
		super(meta, name);
		this.owner = owner;
	}

	public CloudOrganization getOwner() {
		return owner;
	}

	@Override
	public String toString() {
		return getName() + " (" + (owner != null ? owner.getName() : "-") + ")";
	}
}
