package org.cloudfoundry.maven;

import org.apache.maven.plugin.MojoExecutionException;
import org.cloudfoundry.client.lib.CloudFoundryException;

/**
 * Shows application logs
 *
 * @author Ali Moghadam
 * @since 1.0.0
 *
 * @goal logs
 * @phase process-sources
 */

public class Logs extends AbstractApplicationAwareCloudFoundryMojo {

	@Override
	protected void doExecute() throws MojoExecutionException {

		try {
			super.getClient().getApplication(this.getAppname());

			super.getLog().info("============== /logs/stderr.log ==============" + "\n");
			super.getLog().info(super.getClient().getFile(this.getAppname(), 0, "logs/stderr.log"));

			super.getLog().info("============== /logs/stdout.log ==============" + "\n");
			super.getLog().info(super.getClient().getFile(this.getAppname(), 0, "logs/stdout.log"));
		} catch (CloudFoundryException e) {
			super.getLog().info("Application Not Found");
		}
	}
}