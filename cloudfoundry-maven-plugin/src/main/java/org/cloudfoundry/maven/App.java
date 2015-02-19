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

import org.cloudfoundry.client.lib.CloudFoundryException;
import org.cloudfoundry.client.lib.domain.ApplicationStats;
import org.cloudfoundry.client.lib.domain.CloudApplication;
import org.cloudfoundry.maven.common.UiUtils;

/**
 * Application information. Displays the info of deployed application via name, along with
 * information about health, instance count, bound services, and associated URLs.
 *
 * @author Ali Moghadam
 * @since 1.0.0
 *
 * @goal app
 * @phase process-sources
 */
public class App extends AbstractApplicationAwareCloudFoundryMojo {

	@Override
	protected void doExecute() {
		try {
			final CloudApplication application = getClient().getApplication(getAppname());
			final ApplicationStats stats = getClient().getApplicationStats(getAppname());
			getLog().info("\n" + UiUtils.renderCloudApplicationDataAsTable(application, stats));
		} catch (CloudFoundryException e) {
			getLog().info(String.format("Application '%s' doesn't exist", getAppname()));
		}
	}
}