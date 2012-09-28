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
			getClient().getApplication(getAppname());
		} catch (CloudFoundryException e) {
			getLog().info("Application Not Found");
			return;
		}

		getLog().info("============== /logs/stderr.log ==============" + "\n");
		try {
			getLog().info(getClient().getFile(getAppname(), 0, "logs/stderr.log"));
		} catch (CloudFoundryException e) {
			if (HttpStatus.NOT_FOUND.equals(e.getStatusCode())) {
				getLog().info("File Doesn't Exist");
			}
		}

		getLog().info("============== /logs/stdout.log ==============" + "\n");
		try {
			getLog().info(getClient().getFile(getAppname(), 0, "logs/stdout.log"));
		} catch (CloudFoundryException e) {
			if (HttpStatus.NOT_FOUND.equals(e.getStatusCode())) {
				getLog().info("File Doesn't Exist");
			}
		}
	}
}