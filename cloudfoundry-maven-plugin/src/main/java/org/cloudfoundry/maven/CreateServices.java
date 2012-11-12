package org.cloudfoundry.maven;

import java.util.List;

import org.apache.maven.plugin.MojoExecutionException;

/**
 * Create Services
 *
 * @author Ali Moghadam
 * @since 1.0.0
 *
 * @goal create-services
 * @phase process-sources
 */

public class CreateServices extends AbstractApplicationAwareCloudFoundryMojo {

	@Override
	protected void doExecute() throws MojoExecutionException {

		ServiceCreation serviceCreation = new ServiceCreation(getClient(), getNonCreatedServices());

		List<String> serviceNames = serviceCreation.createServices();

		if (serviceNames.isEmpty()) {
			getLog().info(String.format("Service(s) have been already created"));
		} else {
			for (String serviceName : serviceNames) {
				getLog().info(String.format("Creating Service '%s': OK", serviceName));
			}
		}
	}
}