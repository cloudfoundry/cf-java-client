package org.cloudfoundry.maven;

import org.apache.maven.plugin.MojoExecutionException;
import org.cloudfoundry.client.lib.domain.CloudService;

/**
 * Unbind Services
 *
 * @author Ali Moghadam
 * @since 1.0.0
 *
 * @goal unbind-services
 * @phase process-sources
 */

public class UnbindServices extends AbstractApplicationAwareCloudFoundryMojo {

	@Override
	protected void doExecute() throws MojoExecutionException {
		for (CloudService service : getServices()) {
			if (getClient().getService(service.getName()) == null) {
				throw new MojoExecutionException(String.format("The Service '%s' does not exist.",
						service.getName()));
			}

			if (getClient().getApplication(getAppname()).getServices().contains(service.getName())) {
				getClient().unbindService(getAppname(), service.getName());
				getLog().info(String.format("Unbinding Service '%s': OK", service.getName()));
			} else {
				getLog().info(String.format("Unbinding Service '%s': Already Unbinded", service.getName()));
			}
		}
	}
}