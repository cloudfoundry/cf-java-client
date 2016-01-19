/*
 * Copyright 2013-2016 the original author or authors.
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

package org.cloudfoundry.operations;

import org.cloudfoundry.client.CloudFoundryClient;
import org.cloudfoundry.operations.applications.Applications;
import org.cloudfoundry.operations.applications.DefaultApplications;
import org.cloudfoundry.operations.organizations.DefaultOrganizations;
import org.cloudfoundry.operations.organizations.Organizations;
import org.cloudfoundry.operations.routes.DefaultRoutes;
import org.cloudfoundry.operations.routes.Routes;
import org.cloudfoundry.operations.spacequotas.DefaultSpaceQuotas;
import org.cloudfoundry.operations.spacequotas.SpaceQuotas;
import org.cloudfoundry.operations.spaces.DefaultSpaces;
import org.cloudfoundry.operations.spaces.Spaces;
import reactor.core.publisher.Mono;

final class DefaultCloudFoundryOperations implements CloudFoundryOperations {

    private final Applications applications;

    private final Organizations organizations;

    private final Routes routes;

    private final SpaceQuotas spaceQuotas;

    private final Spaces spaces;

    DefaultCloudFoundryOperations(CloudFoundryClient cloudFoundryClient, Mono<String> organizationId, Mono<String> spaceId) {
        this.applications = new DefaultApplications(cloudFoundryClient, spaceId);
        this.organizations = new DefaultOrganizations(cloudFoundryClient);
        this.routes = new DefaultRoutes(cloudFoundryClient, organizationId, spaceId);
        this.spaceQuotas = new DefaultSpaceQuotas(cloudFoundryClient, organizationId);
        this.spaces = new DefaultSpaces(cloudFoundryClient, organizationId);
    }

    @Override
    public Applications applications() {
        return this.applications;
    }

    @Override
    public Organizations organizations() {
        return this.organizations;
    }

    @Override
    public Routes routes() {
        return this.routes;
    }

    @Override
    public SpaceQuotas spaceQuotas() {
        return this.spaceQuotas;
    }

    @Override
    public Spaces spaces() {
        return this.spaces;
    }

}
