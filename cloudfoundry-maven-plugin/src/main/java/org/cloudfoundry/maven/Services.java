/*

 * Copyright 2009-2013 the original author or authors.
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

package org.cloudfoundry.maven;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.cloudfoundry.client.lib.domain.CloudApplication;
import org.cloudfoundry.client.lib.domain.CloudService;
import org.cloudfoundry.client.lib.domain.CloudServiceOffering;

import org.cloudfoundry.maven.common.UiUtils;

/**
 * Creates a service
 *
 * @author Ali Moghadam
 * @author Scott Frederick
 * @since 1.0.0
 *
 * @goal services
 * @phase process-sources
 */

public class Services extends AbstractCloudFoundryMojo {

	@Override
	protected void doExecute() {
		final List<CloudService> services = getClient().getServices();
		final List<CloudApplication> apps = getClient().getApplications();
		final Map<String, List<String>> servicesToApps = mapServicesToApps(services, apps);
		getLog().info("Services instances");
		getLog().info("\n" + UiUtils.renderServiceDataAsTable(services, servicesToApps));

		final List<CloudServiceOffering> serviceOfferings = getClient().getServiceOfferings();
		getLog().info("Available Services");
		getLog().info("\n" + UiUtils.renderServiceOfferingDataAsTable(serviceOfferings));
	}


	protected Map<String, List<String>> mapServicesToApps(List<CloudService> services, List<CloudApplication> apps) {
		Map<String, List<String>> servicesToApps = new HashMap<String, List<String>>(services.size());

		for (CloudApplication app : apps) {
			// todo: when client.getApplications() fills out service names, remove this extra call
			app = client.getApplication(app.getName());
			for (String serviceName : app.getServices()) {
				List<String> appNames = servicesToApps.get(serviceName);
				if (appNames == null) {
					appNames = new ArrayList<String>();
				}
				appNames.add(app.getName());
				servicesToApps.put(serviceName, appNames);
			}
		}

		return servicesToApps;
	}
}