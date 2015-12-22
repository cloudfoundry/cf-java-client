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
import org.cloudfoundry.client.v2.organizations.ListOrganizationsResponse;
import org.cloudfoundry.client.v2.spaces.ListSpacesRequest;
import org.cloudfoundry.client.v2.spaces.ListSpacesResponse;
import org.cloudfoundry.client.v2.spaces.SpaceResource;
import org.cloudfoundry.operations.v2.PageUtils;
import org.reactivestreams.Publisher;
import reactor.fn.Function;

import java.util.List;

/**
 * A builder API for creating the default implementation of the {@link CloudFoundryOperations}
 */
public final class CloudFoundryOperationsBuilder {

    private volatile CloudFoundryClient cloudFoundryClient;

    private volatile String organization;

    private volatile String space;

    /**
     * Builds a new instance of the default implementation of the {@link CloudFoundryOperations} using the information
     * provided.
     *
     * @return a new instance of the default implementation of the {@link CloudFoundryOperations}
     * @throws IllegalArgumentException if {@code cloudFoundryClient} has not been set
     */
    public CloudFoundryOperations build() {
        if (this.cloudFoundryClient == null) {
            throw new IllegalArgumentException("CloudFoundryClient must be set");
        }

        String organizationId = getOrganizationId(this.cloudFoundryClient);
        String spaceId = getSpaceId(this.cloudFoundryClient, organizationId);

        return new DefaultCloudFoundryOperations(this.cloudFoundryClient, organizationId, spaceId);
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

    private String getOrganizationId(CloudFoundryClient cloudFoundryClient) {
        if (this.organization == null) {
            return null;
        }

        List<String> orgIds = PageUtils.resourceStream(new Function<Integer, Publisher<ListOrganizationsResponse>>() {

            @Override
            public Publisher<ListOrganizationsResponse> apply(Integer integer) {
                ListOrganizationsRequest request = ListOrganizationsRequest.builder()
                        .name(CloudFoundryOperationsBuilder.this.organization)
                        .build();

                return CloudFoundryOperationsBuilder.this.cloudFoundryClient.organizations().list(request);
            }
        }).map(new Function<ListOrganizationsResponse.Resource, String>() {

            @Override
            public String apply(ListOrganizationsResponse.Resource resource) {

                return resource.getMetadata().getId();
            }

        }).toList().get();

        if (orgIds == null || orgIds.size() == 0) {
            throw new IllegalArgumentException(String.format("Organization '%s' does not exist",
                    CloudFoundryOperationsBuilder.this.organization));
        } else if (orgIds.size() > 1) {
            throw new UnexpectedResponseException(String.format("Organization '%s' was listed more than once: '%s'",
                    CloudFoundryOperationsBuilder.this.organization, orgIds.toString()));
        }
        return orgIds.get(0);
    }

    private String getSpaceId(final CloudFoundryClient cloudFoundryClient, final String organizationId) {
        if (organizationId == null) {
            return null;
        }

        if (this.space == null) {
            return null;
        }

        List<String> spaceIds = PageUtils.resourceStream(new Function<Integer, Publisher<ListSpacesResponse>>() {

            @Override
            public Publisher<ListSpacesResponse> apply(Integer integer) {
                ListSpacesRequest request = ListSpacesRequest.builder()
                        .organizationId(organizationId)
                        .name(CloudFoundryOperationsBuilder.this.space)
                        .build();

                return cloudFoundryClient.spaces().list(request);
            }
        }).map(new Function<SpaceResource, String>() {

            @Override
            public String apply(SpaceResource resource) {

                return resource.getMetadata().getId();
            }
        }).toList().get();

        if (spaceIds == null || spaceIds.size() == 0) {
            throw new IllegalArgumentException(String.format("Space '%s' does not exist",
                    CloudFoundryOperationsBuilder.this.space));
        } else if (spaceIds.size() > 1) {
            throw new UnexpectedResponseException(String.format("Space '%s' was listed more than once: '%s'",
                    CloudFoundryOperationsBuilder.this.space, spaceIds.toString()));
        }
        return spaceIds.get(0);
    }

}
