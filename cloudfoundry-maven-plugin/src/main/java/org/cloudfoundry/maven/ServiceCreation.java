package org.cloudfoundry.maven;

import java.util.ArrayList;
import java.util.List;

import org.apache.maven.plugin.MojoExecutionException;
import org.cloudfoundry.client.lib.CloudFoundryClient;
import org.cloudfoundry.client.lib.CloudFoundryException;

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

	public List<String> createServices() throws MojoExecutionException {
		for (CloudService service: services) {
			Assert.configurationServiceNotNull(service, null);
			try {
				if (service.getTier() == null) {
					service.setTier("free");
				}

				if (service.getVersion() == null) {
					List<String> tmpServices = new ArrayList<String>();

					for (ServiceConfiguration serviceConfiguration : client.getServiceConfigurations()) {
						if (serviceConfiguration.getVendor().equals(service.getVendor())) {
							tmpServices.add(serviceConfiguration.getVersion());
						}
					}

					Collections.sort(tmpServices);
					if (tmpServices.size() > 0) {
						service.setVersion(tmpServices.get(0));
					}
				}

				client.createService(service);
				serviceNames.add(service.getName());

			} catch (CloudFoundryException e) {
				throw new MojoExecutionException(String.format("Not able to create service '%s'.", service.getName()));
			}
		}
		return serviceNames;
	}
}