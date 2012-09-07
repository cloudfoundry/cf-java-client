package org.cloudfoundry.maven;

import org.apache.maven.plugin.MojoExecutionException;
import org.cloudfoundry.maven.common.Assert;
import org.cloudfoundry.maven.common.SystemProperties;

/**
 * Shows application logs
 *
 * @author Ali Moghadam
 * @since 1.0.0
 *
 * @goal show-logs
 * @phase process-sources
 */

public class ShowLogs extends AbstractApplicationAwareCloudFoundryMojo {

	@Override
	protected void doExecute() throws MojoExecutionException {

		if (this.getAppname().equals(this.getArtifactId())) {
			//At this point, there was no appname provided
			Assert.configurationNotNull(null, "appname", SystemProperties.APP_NAME);
		}

		super.getLog().info("============== /logs/stderr.log ==============" + "\n");
		super.getLog().info(super.getClient().getFile(this.getAppname(), 0, "logs/stderr.log"));

		super.getLog().info("============== /logs/stdout.log ==============" + "\n");
		super.getLog().info(super.getClient().getFile(this.getAppname(), 0, "logs/stdout.log"));
	}
}