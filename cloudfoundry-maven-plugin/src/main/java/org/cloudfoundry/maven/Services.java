package org.cloudfoundry.maven;

import java.util.List;

import org.cloudfoundry.client.lib.domain.CloudService;
import org.cloudfoundry.client.lib.domain.ServiceConfiguration;

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
		final List<ServiceConfiguration> serviceConfigurations = getClient().getServiceConfigurations();
		List<CloudService> services = getClient().getServices();

		getLog().info("============== System Services ==============");
		getLog().info("\n" + UiUtils.renderServiceConfigurationDataAsTable(serviceConfigurations));
		getLog().info("============== Provisioned Services ==============");
		getLog().info("\n" + UiUtils.renderServiceDataAsTable(services));
	}
}