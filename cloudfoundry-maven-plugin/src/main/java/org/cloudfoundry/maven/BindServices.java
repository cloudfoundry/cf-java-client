package org.cloudfoundry.maven;

import org.apache.maven.plugin.MojoExecutionException;
import org.cloudfoundry.client.lib.domain.CloudService;

/**
 * Bind Services
 *
 * @author Ali Moghadam
 * @since 1.0.0
 *
 * @goal bind-services
 * @phase process-sources
 */

public class BindServices extends AbstractApplicationAwareCloudFoundryMojo {

	@Override
	protected void doExecute() throws MojoExecutionException {
		for (CloudService service : getServices()) {
			if (getClient().getService(service.getName()) == null) {
				throw new MojoExecutionException(String.format("The Service '%s' does not exist.",
						service.getName()));
			}

			if (getClient().getApplication(getAppname()).getServices().contains(service.getName())) {
				getLog().info(String.format("Binding Service '%s': Already Binded", service.getName()));
			} else {
				getClient().bindService(getAppname(), service.getName());
				getLog().info(String.format("Binding Service '%s': OK", service.getName()));
			}
		}
	}
}