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
 * @goal logs
 * @phase process-sources
 */

public class Logs extends AbstractApplicationAwareCloudFoundryMojo {

	@Override
	protected void doExecute() throws MojoExecutionException {

		try {
			super.getClient().getApplication(this.getAppname());
		} catch (CloudFoundryException e) {
			super.getLog().info("Application Not Found");
			return;
		}

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