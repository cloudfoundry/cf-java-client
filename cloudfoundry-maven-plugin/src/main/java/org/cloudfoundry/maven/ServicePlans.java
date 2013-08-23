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

import org.cloudfoundry.client.lib.domain.CloudServiceOffering;
import org.cloudfoundry.maven.common.UiUtils;

import java.util.List;

/**
 * Displays information about available services.
 *
 * @author Scott Frederick
 * @since 1.0.0
 *
 * @goal service-plans
 * @phase process-sources
 */

public class ServicePlans extends AbstractCloudFoundryMojo {

	@Override
	protected void doExecute() {
		final List<CloudServiceOffering> serviceOfferings = getClient().getServiceOfferings();
		getLog().info("Available Services");
		getLog().info("\n" + UiUtils.renderServiceOfferingDataAsTable(serviceOfferings));
	}
}