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
import org.cloudfoundry.client.v2.organizations.ListOrganizationsRequest;
import org.cloudfoundry.client.v2.organizations.OrganizationResource;
import org.cloudfoundry.client.v2.spaces.ListSpacesRequest;
import org.cloudfoundry.client.v2.spaces.SpaceResource;
import org.cloudfoundry.logging.LoggingClient;
import org.cloudfoundry.uaa.UaaClient;
import org.cloudfoundry.util.ExceptionUtils;
import org.cloudfoundry.util.PaginationUtils;
import org.cloudfoundry.util.ResourceUtils;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.NoSuchElementException;

/**
 * A builder API for creating the default implementation of the {@link CloudFoundryOperations}
 */
public final class CloudFoundryOperationsBuilder {

    private CloudFoundryClient cloudFoundryClient;

    private LoggingClient loggingClient;

    private String organization;

    private String space;

    private UaaClient uaaClient;

    /**
     * Builds a new instance of the default implementation of the {@link CloudFoundryOperations} using the information provided.
     *
     * @return a new instance of the default implementation of the {@link CloudFoundryOperations}
     * @throws IllegalArgumentException if {@code cloudFoundryClient} has not been set
     */
    public CloudFoundryOperations build() {
        if (this.cloudFoundryClient == null) {
            throw new IllegalArgumentException("CloudFoundryClient must be set");
        }

        Mono<String> organizationId = getOrganizationId(this.cloudFoundryClient, this.organization);
        Mono<String> spaceId = getSpaceId(this.cloudFoundryClient, organizationId, this.space);
        Mono<String> username = getUsername(this.cloudFoundryClient, this.uaaClient);

        return new DefaultCloudFoundryOperations(this.cloudFoundryClient, getLoggingClient(this.loggingClient), organizationId, spaceId, username);
    }

    /**
     * Configure the {@link CloudFoundryClient} to use
     *
     * @param cloudFoundryClient the {@link CloudFoundryClient} to use
     * @return {@code this}
     */
    public CloudFoundryOperationsBuilder cloudFoundryClient(CloudFoundryClient cloudFoundryClient) {
        this.cloudFoundryClient = cloudFoundryClient;
        return this;
    }

    /**
     * Configure the {@link LoggingClient} to use
     *
     * @param loggingClient the {@link LoggingClient} to use
     * @return {@code this}
     */
    public CloudFoundryOperationsBuilder loggingClient(LoggingClient loggingClient) {
        this.loggingClient = loggingClient;
        return this;
    }

    /**
     * Configure the organization and space to target
     *
     * @param organization the organization to target
     * @param space        the space to target
     * @return {@code this}
     */
    public CloudFoundryOperationsBuilder target(String organization, String space) {
        this.organization = organization;
        this.space = space;
        return this;
    }

    /**
     * Configure the organization to target
     *
     * @param organization the organization to target
     * @return {@code this}
     */
    public CloudFoundryOperationsBuilder target(String organization) {
        this.organization = organization;
        return this;
    }

    /**
     * Configure the {@link UaaClient} to use
     *
     * @param uaaClient the {@link UaaClient} to use
     * @return {@code this}
     */
    public CloudFoundryOperationsBuilder uaaClient(UaaClient uaaClient) {
        this.uaaClient = uaaClient;
        return this;
    }

    private static Mono<LoggingClient> getLoggingClient(LoggingClient loggingClient) {
        if (loggingClient == null) {
            return Mono.error(new IllegalStateException("LoggingClient must be set"));
        }

        return Mono.just(loggingClient);
    }

    private static Mono<OrganizationResource> getOrganization(CloudFoundryClient cloudFoundryClient, String organization) {
        return requestOrganizations(cloudFoundryClient, organization)
            .single()
            .otherwise(ExceptionUtils.replace(NoSuchElementException.class, () -> ExceptionUtils.illegalArgument("Organization %s does not exist", organization)));
    }

    private static Mono<String> getOrganizationId(CloudFoundryClient cloudFoundryClient, String organization) {
        if (organization == null) {
            return Mono.error(new IllegalStateException("No organization targeted"));
        }

        Mono<String> organizationId = getOrganization(cloudFoundryClient, organization)
            .map(ResourceUtils::getId)
            .cache();

        organizationId.get();
        return organizationId;
    }

    private static Mono<SpaceResource> getSpace(CloudFoundryClient cloudFoundryClient, String organizationId, String space) {
        return requestSpaces(cloudFoundryClient, organizationId, space)
            .single()
            .otherwise(ExceptionUtils.replace(NoSuchElementException.class, () -> ExceptionUtils.illegalArgument("Space %s does not exist", space)));
    }

    private static Mono<String> getSpaceId(CloudFoundryClient cloudFoundryClient, Mono<String> organizationId, String space) {
        if (space == null) {
            return Mono.error(new IllegalStateException("No space targeted"));
        }

        Mono<String> spaceId = organizationId
            .then(organizationId1 -> getSpace(cloudFoundryClient, organizationId1, space))
            .map(ResourceUtils::getId)
            .cache();

        spaceId.get();
        return spaceId;
    }

    private static Mono<String> getUsername(CloudFoundryClient cloudFoundryClient, UaaClient uaaClient) {
        if (uaaClient == null) {
            return Mono.error(new IllegalStateException("UaaClient must be set"));
        }

        return new UsernameBuilder()
            .cloudFoundryClient(cloudFoundryClient)
            .uaaClient(uaaClient)
            .build();
    }

    private static Flux<OrganizationResource> requestOrganizations(CloudFoundryClient cloudFoundryClient, String organization) {
        return PaginationUtils
            .requestResources(page -> cloudFoundryClient.organizations()
                .list(ListOrganizationsRequest.builder()
                    .name(organization)
                    .page(page)
                    .build()));
    }

    private static Flux<SpaceResource> requestSpaces(CloudFoundryClient cloudFoundryClient, String organizationId, String space) {
        return PaginationUtils
            .requestResources(page -> cloudFoundryClient.spaces()
                .list(ListSpacesRequest.builder()
                    .organizationId(organizationId)
                    .name(space)
                    .page(page)
                    .build()));
    }

}
