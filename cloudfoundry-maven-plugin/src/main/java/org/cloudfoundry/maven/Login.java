package org.cloudfoundry.maven;

import java.io.BufferedWriter;
import java.io.FileWriter;

/*
 * Copyright 2009-2012 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
import java.io.IOException;
import java.net.MalformedURLException;

import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.cloudfoundry.client.lib.CloudCredentials;
import org.cloudfoundry.client.lib.CloudFoundryClient;
import org.cloudfoundry.maven.common.Assert;
import org.cloudfoundry.maven.common.SystemProperties;

/**
 * Writes the user's token into mvn-cf file
 * in user's home directory - This will be used
 * instead of username/password
 *
 * @author Ali Moghadam
 * @since 1.0.0
 *
 * @goal login
 * @requiresProject false
 */
public class Login extends AbstractCloudFoundryMojo {

	@Override
	public void execute() throws MojoExecutionException, MojoFailureException {
		Assert.configurationNotNull(getUsername(), "username", SystemProperties.USERNAME);
		Assert.configurationNotNull(getPassword(), "password", SystemProperties.PASSWORD);
		Assert.configurationNotNull(getTarget(), "target", SystemProperties.TARGET);

		try {
			client = new CloudFoundryClient(new CloudCredentials(getUsername(), getPassword()), getTarget().toURL());
		} catch (MalformedURLException e) {
			throw new MojoExecutionException(
					String.format("Incorrect Cloud Foundry target url, are you sure '%s' is correct? Make sure the url contains a scheme, e.g. http://... ", getTarget()), e);
		}

		doExecute();
	}

	@Override
	protected void doExecute() throws MojoExecutionException {
		try {
			FileWriter fileWriter = new FileWriter(System.getProperty("user.home") + "/.mvn-cf.xml");
			BufferedWriter writer = new BufferedWriter(fileWriter);

			writer.write(client.login());
			writer.close();

			getLog().info("You are now logged in");
		} catch (IOException e) {
			throw new MojoExecutionException("Error creating token file: mvn-cf.xml", e);
		}
	}
}