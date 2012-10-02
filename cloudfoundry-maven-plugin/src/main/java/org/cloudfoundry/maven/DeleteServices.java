package org.cloudfoundry.maven;

import org.apache.maven.plugin.MojoExecutionException;
import org.cloudfoundry.client.lib.CloudFoundryException;
import org.cloudfoundry.client.lib.CloudService;
import org.springframework.http.HttpStatus;

/**
 * Delete Services
 *
 * @author Ali Moghadam
 * @since 1.0.0
 *
 * @goal delete-services
 * @phase process-sources
 */

public class DeleteServices extends AbstractApplicationAwareCloudFoundryMojo {

	@Override
	protected void doExecute() throws MojoExecutionException {
		for (CloudService service : getServices()) {
			try {
				getClient().deleteService(service.getName());
				getLog().info(String.format("Deleting service '%s': OK", service.getName()));
			} catch (CloudFoundryException e) {
				if (HttpStatus.NOT_FOUND.equals(e.getStatusCode())) {
					getLog().info(String.format("Service '%s' doesn't exist", service.getName()));
				}
			}
		}
	}
}