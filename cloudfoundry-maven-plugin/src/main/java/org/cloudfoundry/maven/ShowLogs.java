package org.cloudfoundry.maven;

import org.apache.maven.plugin.MojoExecutionException;
import org.cloudfoundry.client.lib.CloudFoundryException;
import org.springframework.http.HttpStatus;

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

		super.getLog().info("============== /logs/stderr.log ==============" + "\n");
		try {
			super.getLog().info(super.getClient().getFile(this.getAppname(), 0, "logs/stderr.log"));
		} catch (CloudFoundryException e) {
			if (HttpStatus.NOT_FOUND.equals(e.getStatusCode())) {
				super.getLog().info("File Doesn't Exist");
			}
		}

		super.getLog().info("============== /logs/stdout.log ==============" + "\n");
		try {
			super.getLog().info(super.getClient().getFile(this.getAppname(), 0, "logs/stdout.log"));
		} catch (CloudFoundryException e) {
			if (HttpStatus.NOT_FOUND.equals(e.getStatusCode())) {
				super.getLog().info("File Doesn't Exist");
			}
		}
	}
}