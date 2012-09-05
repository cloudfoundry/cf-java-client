package org.cloudfoundry.maven;

import java.util.List;

import org.cloudfoundry.client.lib.CloudService;
import org.cloudfoundry.client.lib.ServiceConfiguration;
import org.cloudfoundry.maven.common.UiUtils;

/**
 * Creates a service
 *
 * @author Ali Moghadam
 * @since 1.0.0
 *
 * @goal services
 * @phase process-sources
 */

public class Services extends AbstractCloudFoundryMojo {

	@Override
	protected void doExecute() {
		final List<ServiceConfiguration> serviceConfigurations = super.getClient().getServiceConfigurations();
		List<CloudService> services = super.getClient().getServices();

		super.getLog().info("============== System Services ==============");
		super.getLog().info("\n" + UiUtils.renderServiceConfigurationDataAsTable(serviceConfigurations));
		super.getLog().info("============== Provisioned Services ==============");
		super.getLog().info("\n" + UiUtils.renderServiceDataAsTable(services));
	}
}