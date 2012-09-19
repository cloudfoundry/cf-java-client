package org.cloudfoundry.maven;

import java.util.List;

import org.apache.maven.plugin.MojoExecutionException;
import org.cloudfoundry.client.lib.CloudFoundryException;
import org.cloudfoundry.client.lib.CloudService;
import org.cloudfoundry.maven.common.Assert;

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

		List<CloudService> services = super.getNonCreatedServices();

		for (CloudService service: services) {
			Assert.configurationServiceNotNull(service, null);
			try {
				super.getClient().createService(service);
				super.getLog().info(String.format("Creating Service '%s': OK", service.getName()));
			} catch (CloudFoundryException e) {
				throw new MojoExecutionException(String.format("Not able to create service '%s'.", service.getName()));
			}
		}
	}
}