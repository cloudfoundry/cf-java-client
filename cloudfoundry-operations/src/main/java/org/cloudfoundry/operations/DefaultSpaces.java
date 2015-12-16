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
import org.cloudfoundry.operations.v2.PageUtils;
import org.reactivestreams.Publisher;
import reactor.fn.Function;

final class DefaultSpaces extends AbstractOperations implements Spaces {

    private final CloudFoundryClient cloudFoundryClient;

    DefaultSpaces(CloudFoundryClient cloudFoundryClient, String organizationId) {
        super(organizationId, null);
        this.cloudFoundryClient = cloudFoundryClient;
    }

    @Override
    public Publisher<Space> list() {
        return PageUtils.resourceStream(new Function<Integer, Publisher<ListSpacesResponse>>() {

            @Override
            public Publisher<ListSpacesResponse> apply(Integer page) {
                ListSpacesRequest request = ListSpacesRequest.builder()
                        .organizationId(DefaultSpaces.this.getTargetedOrganization())
                        .page(page)
                        .build();

                return DefaultSpaces.this.cloudFoundryClient.spaces().list(request);
            }

        }).map(new Function<SpaceResource, Space>() {

            @Override
            public Space apply(SpaceResource resource) {
                return Space.builder()
                        .id(resource.getMetadata().getId())
                        .name(resource.getEntity().getName())
                        .build();
            }

        });
    }

}
