/*
 * Copyright 2013-2015 the original author or authors.
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
import org.cloudfoundry.client.v2.spaces.ListSpacesRequest;
import reactor.rx.Streams;

import java.util.Optional;

/**
 * A builder API for creating the default implementation of the {@link CloudFoundryOperations}
 */
public final class CloudFoundryOperationsBuilder {

    private volatile Optional<CloudFoundryClient> cloudFoundryClient = Optional.empty();

    private volatile Optional<String> organization = Optional.empty();

    private volatile Optional<String> space = Optional.empty();

    /**
     * Configure the {@link CloudFoundryClient} to use
     *
     * @param cloudFoundryClient the {@link CloudFoundryClient} to use
     * @return {@code this}
     */
    public CloudFoundryOperationsBuilder withCloudFoundryClient(CloudFoundryClient cloudFoundryClient) {
        this.cloudFoundryClient = Optional.of(cloudFoundryClient);
        return this;
    }

    /**
     * Configure the organization to target
     *
     * @param organization the organization to target
     * @return {@code this}
     */
    public CloudFoundryOperationsBuilder withTarget(String organization) {
        this.organization = Optional.of(organization);
        return this;
    }

    /**
     * Configure the organization and space to target
     *
     * @param organization the organization to target
     * @param space        the space to target
     * @return {@code this}
     */
    public CloudFoundryOperationsBuilder withTarget(String organization, String space) {
        this.organization = Optional.of(organization);
        this.space = Optional.of(space);
        return this;
    }

    /**
     * Builds a new instance of the default implementation of the {@link CloudFoundryOperations} using the information
     * provided.
     *
     * @return a new instance of the default implementation of the {@link CloudFoundryOperations}
     * @throws IllegalArgumentException if {@code cloudFoundryClient} has not been set
     */
    public CloudFoundryOperations build() {
        CloudFoundryClient cloudFoundryClient = this.cloudFoundryClient
                .orElseThrow(() -> new IllegalArgumentException("CloudFoundryClient must be set"));

        Optional<String> organizationId = getOrganizationId(cloudFoundryClient);
        Optional<String> spaceId = getSpaceId(cloudFoundryClient, organizationId);

        return new DefaultCloudFoundryOperations(cloudFoundryClient, organizationId, spaceId);
    }

    private Optional<String> getOrganizationId(CloudFoundryClient cloudFoundryClient) {
        return this.organization.map(name -> {
            ListOrganizationsRequest request = new ListOrganizationsRequest()
                    .withName(name);

            return Streams.wrap(cloudFoundryClient.organizations().list(request))
                    .map(response -> response.getResources().stream())
                    .map(stream -> stream.findFirst().map(resource -> resource.getMetadata().getId()))
                    .observe(id -> id.orElseThrow(() -> new IllegalArgumentException(
                                    String.format("Organization '%s' does not exist", name)))
                    )
                    .next().poll();
        }).orElse(Optional.empty());
    }

    private Optional<String> getSpaceId(CloudFoundryClient cloudFoundryClient, Optional<String> organizationId) {
        return organizationId.map(orgId -> this.space.map(name -> {
            ListSpacesRequest request = new ListSpacesRequest()
                    .withOrganizationId(orgId)
                    .withName(name);

            return Streams.wrap(cloudFoundryClient.spaces().list(request))
                    .map(response -> response.getResources().stream())
                    .map(stream -> stream.findFirst().map(resource -> resource.getMetadata().getId()))
                    .observe(id -> id.orElseThrow(() -> new IllegalArgumentException(
                                    String.format("Space '%s' does not exist", name)))
                    )
                    .next().poll();
        }).orElse(Optional.empty())).orElse(Optional.empty());
    }

}
