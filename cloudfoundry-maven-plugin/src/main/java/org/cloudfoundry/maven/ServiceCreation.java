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
import java.util.List;

import org.apache.maven.plugin.MojoExecutionException;
import org.cloudfoundry.client.lib.CloudFoundryClient;
import org.cloudfoundry.client.lib.CloudFoundryException;

import org.cloudfoundry.client.lib.domain.CloudInfo;
import org.cloudfoundry.client.lib.domain.CloudService;
import org.cloudfoundry.client.lib.domain.ServiceConfiguration;

import org.cloudfoundry.maven.common.Assert;

import edu.emory.mathcs.backport.java.util.Collections;

public class ServiceCreation {

	private List<CloudService> services;
	private CloudFoundryClient client;
	private List<String> serviceNames = new ArrayList<String>();

	public ServiceCreation() {}

	public ServiceCreation(CloudFoundryClient client, List<CloudService> services) {
		this.client = client;
		this.services = services;
	}

	public void setServices(List<CloudService> services) {
		this.services = services;
	}

	public void setClient(CloudFoundryClient client) {
		this.client = client;
	}

/**
 *
 * @return
 * @throws MojoExecutionException
 */
	public List<String> createServices() throws MojoExecutionException {
		for (CloudService service: services) {
			if (client.getCloudInfo().getCloudControllerMajorVersion() == CloudInfo.CC_MAJOR_VERSION.V2) {
				Assert.configurationServiceNotNullV2(service, null);

				if (service.getProvider() == null) {
					service.setProvider("core");
				}
				setServiceVersion(service);

				if (service.getPlan() == null && (service.getTier() == null || service.getTier().equals("free"))) {
					service.setPlan("D100");
				}
			} else {
				Assert.configurationServiceNotNullV1(service, null);

				if (service.getTier() == null) {
					service.setTier("free");
				}
				setServiceVersion(service);
			}

			try {
				client.createService(service);
				serviceNames.add(service.getName());
			} catch (CloudFoundryException e) {
				throw new MojoExecutionException(String.format("Not able to create service '%s'.", service.getName()));
			}
		}

		return serviceNames;
	}

/**
 *
 * @param service
 */
	protected void setServiceVersion(CloudService service) {
		if (service.getVersion() == null) {
			List<String> tmpServices = new ArrayList<String>();

			if (client.getCloudInfo().getCloudControllerMajorVersion() == CloudInfo.CC_MAJOR_VERSION.V2) {
				for (ServiceConfiguration serviceConfiguration : client.getServiceConfigurations()) {
					if (serviceConfiguration.getCloudServiceOffering().getLabel().equals(service.getLabel())) {
						tmpServices.add(serviceConfiguration.getCloudServiceOffering().getVersion());
					}
				}
			} else {
				for (ServiceConfiguration serviceConfiguration : client.getServiceConfigurations()) {
					if (serviceConfiguration.getVendor().equals(service.getVendor())) {
						tmpServices.add(serviceConfiguration.getVersion());
					}
				}
			}

			Collections.sort(tmpServices);
			if (tmpServices.size() > 0) {
				service.setVersion(tmpServices.get(0));
			}
		}
	}
}