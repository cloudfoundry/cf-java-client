/*
 * Copyright 2013-2017 the original author or authors.
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

import org.cloudfoundry.Nullable;
import org.cloudfoundry.client.CloudFoundryClient;
import org.cloudfoundry.client.v2.organizations.ListOrganizationsRequest;
import org.cloudfoundry.client.v2.organizations.OrganizationResource;
import org.cloudfoundry.client.v2.spaces.ListSpacesRequest;
import org.cloudfoundry.client.v2.spaces.SpaceResource;
import org.cloudfoundry.doppler.DopplerClient;
import org.cloudfoundry.operations.advanced.Advanced;
import org.cloudfoundry.operations.advanced.DefaultAdvanced;
import org.cloudfoundry.operations.applications.Applications;
import org.cloudfoundry.operations.applications.DefaultApplications;
import org.cloudfoundry.operations.buildpacks.Buildpacks;
import org.cloudfoundry.operations.buildpacks.DefaultBuildpacks;
import org.cloudfoundry.operations.domains.DefaultDomains;
import org.cloudfoundry.operations.domains.Domains;
import org.cloudfoundry.operations.organizationadmin.DefaultOrganizationAdmin;
import org.cloudfoundry.operations.organizationadmin.OrganizationAdmin;
import org.cloudfoundry.operations.organizations.DefaultOrganizations;
import org.cloudfoundry.operations.organizations.Organizations;
import org.cloudfoundry.operations.routes.DefaultRoutes;
import org.cloudfoundry.operations.routes.Routes;
import org.cloudfoundry.operations.serviceadmin.DefaultServiceAdmin;
import org.cloudfoundry.operations.serviceadmin.ServiceAdmin;
import org.cloudfoundry.operations.services.DefaultServices;
import org.cloudfoundry.operations.services.Services;
import org.cloudfoundry.operations.spaceadmin.DefaultSpaceAdmin;
import org.cloudfoundry.operations.spaceadmin.SpaceAdmin;
import org.cloudfoundry.operations.spaces.DefaultSpaces;
import org.cloudfoundry.operations.spaces.Spaces;
import org.cloudfoundry.operations.stacks.DefaultStacks;
import org.cloudfoundry.operations.stacks.Stacks;
import org.cloudfoundry.routing.RoutingClient;
import org.cloudfoundry.uaa.UaaClient;
import org.cloudfoundry.util.ExceptionUtils;
import org.cloudfoundry.util.PaginationUtils;
import org.cloudfoundry.util.ResourceUtils;
import org.immutables.value.Value;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.NoSuchElementException;
import java.util.Optional;

/**
 * The default implementation of the {@link CloudFoundryOperations} interface
 */
@Value.Immutable
abstract class _DefaultCloudFoundryOperations implements CloudFoundryOperations {

    @Override
    @Value.Derived
    public Advanced advanced() {
        return new DefaultAdvanced(getUaaClientPublisher());
    }

    @Override
    @Value.Derived
    public Applications applications() {
        return new DefaultApplications(getCloudFoundryClientPublisher(), getDopplerClientPublisher(), getSpaceId());
    }

    @Override
    @Value.Derived
    public Buildpacks buildpacks() {
        return new DefaultBuildpacks(getCloudFoundryClientPublisher());
    }

    @Override
    @Value.Derived
    public Domains domains() {
        return new DefaultDomains(getCloudFoundryClientPublisher(), getRoutingClientPublisher());
    }

    @Override
    @Value.Derived
    public OrganizationAdmin organizationAdmin() {
        return new DefaultOrganizationAdmin(getCloudFoundryClientPublisher());
    }

    @Override
    @Value.Derived
    public Organizations organizations() {
        return new DefaultOrganizations(getCloudFoundryClientPublisher(), getUsername());
    }

    @Override
    @Value.Derived
    public Routes routes() {
        return new DefaultRoutes(getCloudFoundryClientPublisher(), getOrganizationId(), getSpaceId());
    }

    @Override
    @Value.Derived
    public ServiceAdmin serviceAdmin() {
        return new DefaultServiceAdmin(getCloudFoundryClientPublisher(), getSpaceId());
    }

    @Override
    @Value.Derived
    public Services services() {
        return new DefaultServices(getCloudFoundryClientPublisher(), getOrganizationId(), getSpaceId());
    }

    @Override
    @Value.Derived
    public SpaceAdmin spaceAdmin() {
        return new DefaultSpaceAdmin(getCloudFoundryClientPublisher(), getOrganizationId());
    }

    @Override
    @Value.Derived
    public Spaces spaces() {
        return new DefaultSpaces(getCloudFoundryClientPublisher(), getOrganizationId(), getUsername());
    }

    @Override
    @Value.Derived
    public Stacks stacks() {
        return new DefaultStacks(getCloudFoundryClientPublisher());
    }

    /**
     * The {@link CloudFoundryClient} to use for operations functionality
     */
    @Nullable
    abstract CloudFoundryClient getCloudFoundryClient();

    @Value.Derived
    Mono<CloudFoundryClient> getCloudFoundryClientPublisher() {
        return Optional.ofNullable(getCloudFoundryClient())
            .map(Mono::just)
            .orElse(Mono.error(new IllegalStateException("CloudFoundryClient must be set")));
    }

    /**
     * The {@link DopplerClient} to use for operations functionality
     */
    @Nullable
    abstract DopplerClient getDopplerClient();

    @Value.Derived
    Mono<DopplerClient> getDopplerClientPublisher() {
        return Optional.ofNullable(getDopplerClient())
            .map(Mono::just)
            .orElse(Mono.error(new IllegalStateException("DopplerClient must be set")));
    }

    /**
     * The organization to target
     */
    @Nullable
    abstract String getOrganization();

    @Value.Derived
    Mono<String> getOrganizationId() {
        String organization = getOrganization();

        if (hasText(organization)) {
            return getOrganization(getCloudFoundryClientPublisher(), organization)
                .map(ResourceUtils::getId)
                .cache();
        } else {
            return Mono.error(new IllegalStateException("No organization targeted"));
        }
    }

    /**
     * The {@link RoutingClient} to use for operations functionality
     */
    @Nullable
    abstract RoutingClient getRoutingClient();

    @Value.Derived
    Mono<RoutingClient> getRoutingClientPublisher() {
        return Optional.ofNullable(getRoutingClient())
            .map(Mono::just)
            .orElse(Mono.error(new IllegalStateException("RoutingClient must be set")));
    }

    /**
     * The space to target
     */
    @Nullable
    abstract String getSpace();

    @Value.Derived
    Mono<String> getSpaceId() {
        String space = getSpace();

        if (hasText(getSpace())) {
            return getOrganizationId()
                .flatMap(organizationId -> getSpace(getCloudFoundryClientPublisher(), organizationId, space))
                .map(ResourceUtils::getId)
                .cache();
        } else {
            return Mono.error(new IllegalStateException("No space targeted"));
        }
    }

    /**
     * The {@link UaaClient} to use for operations functionality
     */
    @Nullable
    abstract UaaClient getUaaClient();

    @Value.Derived
    Mono<UaaClient> getUaaClientPublisher() {
        return Optional.ofNullable(getUaaClient())
            .map(Mono::just)
            .orElse(Mono.error(new IllegalStateException("UaaClient must be set")));
    }

    @Value.Derived
    Mono<String> getUsername() {
        return getUaaClientPublisher()
            .flatMap(UaaClient::getUsername);
    }

    private static Mono<OrganizationResource> getOrganization(Mono<CloudFoundryClient> cloudFoundryClient, String organization) {
        return requestOrganizations(cloudFoundryClient, organization)
            .single()
            .onErrorResume(NoSuchElementException.class, t -> ExceptionUtils.illegalArgument("Organization %s does not exist", organization));
    }

    private static Mono<SpaceResource> getSpace(Mono<CloudFoundryClient> cloudFoundryClient, String organizationId, String space) {
        return requestSpaces(cloudFoundryClient, organizationId, space)
            .single()
            .onErrorResume(NoSuchElementException.class, t -> ExceptionUtils.illegalArgument("Space %s does not exist", space));
    }

    private static boolean hasLength(CharSequence str) {
        return (str != null && str.length() > 0);
    }

    private static boolean hasText(CharSequence str) {
        if (!hasLength(str)) {
            return false;
        }
        int strLen = str.length();
        for (int i = 0; i < strLen; i++) {
            if (!Character.isWhitespace(str.charAt(i))) {
                return true;
            }
        }
        return false;
    }

    private static Flux<OrganizationResource> requestOrganizations(Mono<CloudFoundryClient> cloudFoundryClientPublisher, String organization) {
        return cloudFoundryClientPublisher
            .flatMapMany(cloudFoundryClient -> PaginationUtils
                .requestClientV2Resources(page -> cloudFoundryClient.organizations()
                    .list(ListOrganizationsRequest.builder()
                        .name(organization)
                        .page(page)
                        .build())));
    }

    private static Flux<SpaceResource> requestSpaces(Mono<CloudFoundryClient> cloudFoundryClientPublisher, String organizationId, String space) {
        return cloudFoundryClientPublisher
            .flatMapMany(cloudFoundryClient -> PaginationUtils
                .requestClientV2Resources(page -> cloudFoundryClient.spaces()
                    .list(ListSpacesRequest.builder()
                        .organizationId(organizationId)
                        .name(space)
                        .page(page)
                        .build())));
    }

}
