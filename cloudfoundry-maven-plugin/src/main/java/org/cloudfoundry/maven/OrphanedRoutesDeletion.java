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
import org.apache.maven.plugin.MojoFailureException;
import org.cloudfoundry.client.lib.domain.CloudRoute;

import java.util.List;

/**
 * Delete routes that do not have any application which is assigned to them.
 *
 * @author Alexander Orlov
 * @goal delete-orphaned-routes
 * @since 1.0.4
 */
public class OrphanedRoutesDeletion extends AbstractApplicationAwareCloudFoundryMojo {

    @Override
    protected void doExecute() throws MojoExecutionException, MojoFailureException {
        getLog().info("Getting routes");
        List<CloudRoute> routes = getClient().deleteOrphanedRoutes();
        for (CloudRoute route : routes) {
            getLog().info(String.format("Deleted route '%s'", route.getName()));
        }
    }
}
