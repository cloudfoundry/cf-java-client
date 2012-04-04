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
package org.cloudfoundry.maven;

import java.util.List;

import org.cloudfoundry.client.lib.CloudApplication;
import org.cloudfoundry.maven.common.UiUtils;

/**
 * Lists your applications. Displays all deployed applications, along with
 * information about health, instance count, bound services, and associated URLs.
 *
 * @author Gunnar Hillert
 * @since 1.0.0
 *
 * @goal apps
 * @phase process-sources
 */
public class Apps extends AbstractCloudFoundryMojo {

    @Override
    protected void doExecute() {

        final List<CloudApplication> applications = this.getClient().getApplications();

        super.getLog().info("\n" + UiUtils.renderCloudApplicationDataAsTable(applications));

    }

}
