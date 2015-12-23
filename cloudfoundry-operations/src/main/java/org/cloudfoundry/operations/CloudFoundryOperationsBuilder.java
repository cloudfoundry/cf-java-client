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
import org.cloudfoundry.client.v2.Resource;
import org.cloudfoundry.client.v2.organizations.ListOrganizationsRequest;
import org.cloudfoundry.client.v2.organizations.ListOrganizationsResponse;
import org.cloudfoundry.client.v2.spaces.ListSpacesRequest;
import org.cloudfoundry.client.v2.spaces.ListSpacesResponse;
import org.cloudfoundry.client.v2.spaces.SpaceResource;
import org.cloudfoundry.operations.v2.Paginated;
import org.reactivestreams.Publisher;
import reactor.fn.BiFunction;
import reactor.fn.Function;
import reactor.rx.Stream;
import reactor.rx.Streams;

/**
 * A builder API for creating the default implementation of the {@link CloudFoundryOperations}
 */
public final class CloudFoundryOperationsBuilder {

    private volatile CloudFoundryClient cloudFoundryClient;

    private volatile String organization;

    private volatile String space;

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

        Stream<String> organizationId = getOrganizationId(this.cloudFoundryClient, this.organization);
        Stream<String> spaceId = getSpaceId(this.cloudFoundryClient, organizationId, this.space);

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

    private Function<Resource<?>, String> extractId() {
        return new Function<Resource<?>, String>() {

            @Override
            public String apply(Resource<?> resource) {

                return resource.getMetadata().getId();
            }

        };
    }

    private <T extends Resource<?>> BiFunction<T, T, T> failIfMoreThanOne(final String message) {
        return new BiFunction<T, T, T>() {

            @Override
            public T apply(T resource1, T resource2) {
                throw new UnexpectedResponseException(message);
            }

        };
    }

    private Stream<String> getOrganizationId(CloudFoundryClient cloudFoundryClient, String organization) {
        if (organization == null) {
            return Streams.fail(new IllegalStateException("No organization targeted"));
        }

        Stream<String> organizationId = Paginated.requestResources(requestOrganizationPage(cloudFoundryClient))
                .reduce(this.<ListOrganizationsResponse.Resource>failIfMoreThanOne(String.format("Organization %s was listed more than once", organization)))
// TODO: Some sort of supplier
// .defaultIfEmpty(Streams.<ListOrganizationsResponse.Resource>fail(
// new IllegalArgumentException(String.format("Organization '%s' does not exist", CloudFoundryOperationsBuilder.this.organization))))
                .map(extractId())
                .cache(1);

        organizationId.toBlockingQueue().poll();
        return organizationId;
    }

    private Stream<String> getSpaceId(final CloudFoundryClient cloudFoundryClient, Stream<String> organizationId, String space) {
        if (space == null) {
            return Streams.fail(new IllegalStateException("No space targeted"));
        }

        Stream<String> spaceId = organizationId
                .flatMap(requestResources(cloudFoundryClient))
                .reduce(this.<SpaceResource>failIfMoreThanOne(String.format("Space %s was listed more than once", space)))
// TODO: Some sort of supplier
// .defaultIfEmpty(Streams.<ListOrganizationsResponse.Resource>fail(
// new IllegalArgumentException(String.format("Organization '%s' does not exist", CloudFoundryOperationsBuilder.this.organization))))
                .map(extractId())
                .cache(1);

        spaceId.toBlockingQueue().poll();
        return spaceId;
    }

    private Function<Integer, Publisher<ListOrganizationsResponse>> requestOrganizationPage(final CloudFoundryClient cloudFoundryClient) {
        return new Function<Integer, Publisher<ListOrganizationsResponse>>() {

            @Override
            public Publisher<ListOrganizationsResponse> apply(Integer page) {
                ListOrganizationsRequest request = ListOrganizationsRequest.builder()
                        .name(CloudFoundryOperationsBuilder.this.organization)
                        .page(page)
                        .build();

                return cloudFoundryClient.organizations().list(request);
            }
        };
    }

    private Function<String, Publisher<SpaceResource>> requestResources(final CloudFoundryClient cloudFoundryClient) {
        return new Function<String, Publisher<SpaceResource>>() {

            @Override
            public Publisher<SpaceResource> apply(String organizationId) {
                return Paginated.requestResources(requestSpacePage(organizationId, cloudFoundryClient));
            }

        };
    }

    private Function<Integer, Publisher<ListSpacesResponse>> requestSpacePage(final String organizationId, final CloudFoundryClient cloudFoundryClient) {
        return new Function<Integer, Publisher<ListSpacesResponse>>() {

            @Override
            public Publisher<ListSpacesResponse> apply(Integer page) {
                ListSpacesRequest request = ListSpacesRequest.builder()
                        .organizationId(organizationId)
                        .name(CloudFoundryOperationsBuilder.this.space)
                        .page(page)
                        .build();

                return cloudFoundryClient.spaces().list(request);
            }

        };
    }

}
