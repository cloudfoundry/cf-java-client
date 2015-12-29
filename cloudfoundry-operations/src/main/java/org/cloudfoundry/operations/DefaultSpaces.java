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
import org.cloudfoundry.client.v2.spaces.ListSpacesRequest;
import org.cloudfoundry.client.v2.spaces.ListSpacesResponse;
import org.cloudfoundry.client.v2.spaces.SpaceResource;
import org.cloudfoundry.operations.v2.Paginated;
import org.cloudfoundry.operations.v2.Resources;
import org.reactivestreams.Publisher;
import reactor.fn.Function;
import reactor.rx.Stream;

final class DefaultSpaces implements Spaces {

    private final CloudFoundryClient cloudFoundryClient;

    private final Stream<String> organizationId;

    DefaultSpaces(CloudFoundryClient cloudFoundryClient, Stream<String> organizationId) {
        this.cloudFoundryClient = cloudFoundryClient;
        this.organizationId = organizationId;
    }

    @Override
    public Publisher<Space> list() {
        return this.organizationId
                .flatMap(requestSpaceResources(this.cloudFoundryClient))
                .map(toSpace());
    }

    private static Function<Integer, Publisher<ListSpacesResponse>> requestPage(final CloudFoundryClient cloudFoundryClient, final String organizationId) {
        return new Function<Integer, Publisher<ListSpacesResponse>>() {

            @Override
            public Publisher<ListSpacesResponse> apply(Integer page) {
                ListSpacesRequest request = ListSpacesRequest.builder()
                        .organizationId(organizationId)
                        .page(page)
                        .build();

                return cloudFoundryClient.spaces().list(request);
            }

        };
    }

    private static Function<String, Publisher<SpaceResource>> requestSpaceResources(final CloudFoundryClient cloudFoundryClient) {
        return new Function<String, Publisher<SpaceResource>>() {

            @Override
            public Publisher<SpaceResource> apply(String organizationId) {
                return Paginated.requestResources(requestPage(cloudFoundryClient, organizationId));
            }

        };
    }

    private static Function<SpaceResource, Space> toSpace() {
        return new Function<SpaceResource, Space>() {

            @Override
            public Space apply(SpaceResource resource) {
                return Space.builder()
                        .id(Resources.getId(resource))
                        .name(Resources.getEntity(resource).getName())
                        .build();
            }

        };
    }

}
