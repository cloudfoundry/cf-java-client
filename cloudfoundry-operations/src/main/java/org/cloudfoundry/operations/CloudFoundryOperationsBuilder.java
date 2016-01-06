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
import org.cloudfoundry.client.v2.organizations.ListOrganizationsResponse;
import org.cloudfoundry.client.v2.spaces.ListSpacesRequest;
import org.cloudfoundry.client.v2.spaces.ListSpacesResponse;
import org.cloudfoundry.client.v2.spaces.SpaceResource;
import org.cloudfoundry.operations.v2.Paginated;
import org.cloudfoundry.operations.v2.Resources;
import org.reactivestreams.Publisher;
import reactor.Mono;
import reactor.fn.Function;
import reactor.rx.Promise;
import reactor.rx.Streams;

import java.util.NoSuchElementException;

/**
 * A builder API for creating the default implementation of the {@link CloudFoundryOperations}
 */
public final class CloudFoundryOperationsBuilder {

    private CloudFoundryClient cloudFoundryClient;

    private String organization;

    private String space;

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

    private static <T> Function<Throwable, Mono<T>> convertException(final String message) {
        return new Function<Throwable, Mono<T>>() {

            @Override
            public Mono<T> apply(Throwable throwable) {
                if (throwable instanceof NoSuchElementException) {
                    return Mono.error(new IllegalArgumentException(message, throwable));
                } else {
                    return Mono.error(throwable);
                }
            }

        };
    }

    private static Mono<String> getOrganizationId(CloudFoundryClient cloudFoundryClient, String organization) {
        if (organization == null) {
            return Mono.error(new IllegalStateException("No organization targeted"));
        }

        Mono<String> organizationId = Paginated
                .requestResources(requestOrganizationPage(cloudFoundryClient, organization))
                .single()
                .map(Resources.extractId())
                .otherwise(CloudFoundryOperationsBuilder.<String>convertException(String.format("Organization %s does not exist", organization)))
                .to(Promise.<String>prepare());

        organizationId.get();
        return organizationId;
    }

    private static Mono<String> getSpaceId(final CloudFoundryClient cloudFoundryClient, Publisher<String> organizationId, String space) {
        if (space == null) {
            return Mono.error(new IllegalStateException("No space targeted"));
        }

        Mono<String> spaceId = Streams
                .from(organizationId)
                .flatMap(requestResources(cloudFoundryClient, space))
                .single()
                .map(Resources.extractId())
                .otherwise(CloudFoundryOperationsBuilder.<String>convertException(String.format("Space %s does not exist", space)))
                .to(Promise.<String>prepare());

        spaceId.get();
        return spaceId;
    }

    private static Function<Integer, Publisher<ListOrganizationsResponse>> requestOrganizationPage(final CloudFoundryClient cloudFoundryClient, final String organization) {
        return new Function<Integer, Publisher<ListOrganizationsResponse>>() {

            @Override
            public Publisher<ListOrganizationsResponse> apply(Integer page) {
                ListOrganizationsRequest request = ListOrganizationsRequest.builder()
                        .name(organization)
                        .page(page)
                        .build();

                return cloudFoundryClient.organizations().list(request);
            }
        };
    }

    private static Function<String, Publisher<SpaceResource>> requestResources(final CloudFoundryClient cloudFoundryClient, final String space) {
        return new Function<String, Publisher<SpaceResource>>() {

            @Override
            public Publisher<SpaceResource> apply(String organizationId) {
                return Paginated.requestResources(requestSpacePage(cloudFoundryClient, organizationId, space));
            }

        };
    }

    private static Function<Integer, Publisher<ListSpacesResponse>> requestSpacePage(final CloudFoundryClient cloudFoundryClient, final String organizationId, final String space) {
        return new Function<Integer, Publisher<ListSpacesResponse>>() {

            @Override
            public Publisher<ListSpacesResponse> apply(Integer page) {
                ListSpacesRequest request = ListSpacesRequest.builder()
                        .organizationId(organizationId)
                        .name(space)
                        .page(page)
                        .build();

                return cloudFoundryClient.spaces().list(request);
            }

        };
    }

}
