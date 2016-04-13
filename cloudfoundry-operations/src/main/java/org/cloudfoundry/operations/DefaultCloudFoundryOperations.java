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
import org.cloudfoundry.logging.LoggingClient;
import org.cloudfoundry.operations.applications.Applications;
import org.cloudfoundry.operations.applications.DefaultApplications;
import org.cloudfoundry.operations.buildpacks.Buildpacks;
import org.cloudfoundry.operations.buildpacks.DefaultBuildpacks;
import org.cloudfoundry.operations.domains.DefaultDomains;
import org.cloudfoundry.operations.domains.Domains;
import org.cloudfoundry.operations.organizations.DefaultOrganizations;
import org.cloudfoundry.operations.organizations.Organizations;
import org.cloudfoundry.operations.quotas.DefaultOrganizationAdmin;
import org.cloudfoundry.operations.quotas.OrganizationAdmin;
import org.cloudfoundry.operations.routes.DefaultRoutes;
import org.cloudfoundry.operations.routes.Routes;
import org.cloudfoundry.operations.services.DefaultServices;
import org.cloudfoundry.operations.services.Services;
import org.cloudfoundry.operations.spaceadmin.DefaultSpaceAdmin;
import org.cloudfoundry.operations.spaceadmin.SpaceAdmin;
import org.cloudfoundry.operations.spaces.DefaultSpaces;
import org.cloudfoundry.operations.spaces.Spaces;
import org.cloudfoundry.operations.stacks.DefaultStacks;
import org.cloudfoundry.operations.stacks.Stacks;
import reactor.core.publisher.Mono;

final class DefaultCloudFoundryOperations implements CloudFoundryOperations {

    private final Applications applications;

    private final Buildpacks buildpacks;

    private final Domains domains;

    private final OrganizationAdmin organizationAdmin;

    private final Organizations organizations;

    private final Routes routes;

    private final Services services;

    private final SpaceAdmin spaceAdmin;

    private final Spaces spaces;

    private final Stacks stacks;

    DefaultCloudFoundryOperations(CloudFoundryClient cloudFoundryClient, Mono<LoggingClient> loggingClient, Mono<String> organizationId, Mono<String> spaceId, Mono<String> username) {
        this.applications = new DefaultApplications(cloudFoundryClient, loggingClient, spaceId);
        this.buildpacks = new DefaultBuildpacks(cloudFoundryClient);
        this.domains = new DefaultDomains(cloudFoundryClient);
        this.organizationAdmin = new DefaultOrganizationAdmin(cloudFoundryClient);
        this.organizations = new DefaultOrganizations(cloudFoundryClient, username);
        this.routes = new DefaultRoutes(cloudFoundryClient, organizationId, spaceId);
        this.services = new DefaultServices(cloudFoundryClient, spaceId);
        this.spaceAdmin = new DefaultSpaceAdmin(cloudFoundryClient, organizationId);
        this.spaces = new DefaultSpaces(cloudFoundryClient, organizationId, username);
        this.stacks = new DefaultStacks(cloudFoundryClient);
    }

    @Override
    public Applications applications() {
        return this.applications;
    }

    @Override
    public Buildpacks buildpacks() {
        return this.buildpacks;
    }

    @Override
    public Domains domains() {
        return this.domains;
    }

    @Override
    public OrganizationAdmin organizationAdmin() {
        return this.organizationAdmin;
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
    public Services services() {
        return this.services;
    }

    @Override
    public SpaceAdmin spaceAdmin() {
        return this.spaceAdmin;
    }

    @Override
    public Spaces spaces() {
        return this.spaces;
    }

    @Override
    public Stacks stacks() {
        return this.stacks;
    }

}
