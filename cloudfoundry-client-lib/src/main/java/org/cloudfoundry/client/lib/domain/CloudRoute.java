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
public class CloudRoute extends CloudEntity {

	private String host;

	private CloudDomain domain;

	private int appsUsingRoute;

	public CloudRoute(Meta meta, String host, CloudDomain domain, int appsUsingRoute) {
		super(meta, host + "." + domain.getName());
		this.host = host;
		this.domain = domain;
		this.appsUsingRoute = appsUsingRoute;
	}

	public String getHost() {
		return host;
	}

	public CloudDomain getDomain() {
		return domain;
	}

	public int getAppsUsingRoute() {
		return appsUsingRoute;
	}

	public boolean inUse() {
		return appsUsingRoute > 0;
	}

	@Override
	public String toString() {
		return getName();
	}
}
