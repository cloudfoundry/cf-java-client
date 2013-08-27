/*
 * Copyright 2009-2013 the original author or authors.
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
package org.cloudfoundry.maven;

import java.net.MalformedURLException;
import java.net.URI;

import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;

import org.cloudfoundry.client.lib.CloudFoundryClient;

import org.cloudfoundry.client.lib.domain.CloudInfo;

import org.cloudfoundry.maven.common.Assert;
import org.cloudfoundry.maven.common.SystemProperties;
import org.cloudfoundry.maven.common.UiUtils;

/**
 * Provide general usage information about the used Cloud Foundry environment.
 *
 * @author Gunnar Hillert
 * @author Stephan Oudmaijer
 * @author Ali Moghadam
 * @author Scott Frederick
 *
 * @since 1.0.0
 *
 * @goal target
 * @requiresProject false
 */
public class Target extends AbstractCloudFoundryMojo {

	/**
	 * 	@FIXME Not sure whether one should be able to overwrite execute()
	 *
	 */
	@Override
	public void execute() throws MojoExecutionException, MojoFailureException {
		final URI target = getTarget();
		Assert.configurationNotNull(target, "target", SystemProperties.TARGET);

		try {
			client = new CloudFoundryClient(getTarget().toURL());
			doExecute();
		} catch (MalformedURLException e) {
			throw new MojoExecutionException(
					String.format("Incorrect Cloud Foundry target url, are you sure '%s' is correct? Make sure the url contains a scheme, e.g. http://... ", target), e);
		}
	}

	@Override
	protected void doExecute() throws MojoExecutionException {

		CloudFoundryClient newClient;

		if (getUsername() != null && getPassword() != null) {
			newClient = createCloudFoundryClient(getUsername(), getPassword(), getTarget(), getOrg(), getSpace());
		} else {
			newClient = createCloudFoundryClient(retrieveToken(), getTarget(), getOrg(), getSpace());
		}

		final CloudInfo cloudInfo = newClient.getCloudInfo();

		getLog().info(UiUtils.renderCloudInfoFormattedAsString(cloudInfo, getTarget().toString(), getOrg(), getSpace()));
	}
}