package org.cloudfoundry.maven;

import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;

import java.io.File;

/**
 * Performs logout if client exist &
 * Deletes the user's token mvn-cf file
 *
 * @author Ali Moghadam
 * @since 1.0.0
 *
 * @goal logout
 * @requiresProject false
 */
public class Logout extends AbstractCloudFoundryMojo {

	@Override
	public void execute() throws MojoExecutionException, MojoFailureException {
		if (getClient() != null) {
			getClient().logout();
		}
		doExecute();
	}

	@Override
	protected void doExecute() throws MojoExecutionException {
		File file  = getFile();

		if (file.delete()) {
			getLog().info("Token file removed. You are logged out");
		} else {
			getLog().info("Token file didn't exist. You are logged out");
		}
	}

	protected File getFile() {
		File file  = new File(System.getProperty("user.home") + "/.mvn-cf.xml");

		return file;
	}
}