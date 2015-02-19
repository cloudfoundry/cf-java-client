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

import org.apache.maven.plugin.MojoExecutionException;

import org.cloudfoundry.client.lib.domain.CloudInfo;

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

	@Override
	protected void doExecute() throws MojoExecutionException {
		final CloudInfo cloudInfo = getClient().getCloudInfo();
		getLog().info(UiUtils.renderCloudInfoFormattedAsString(cloudInfo, getTarget().toString(), getOrg(), getSpace()));
	}
}