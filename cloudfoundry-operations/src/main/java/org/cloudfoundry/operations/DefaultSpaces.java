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
import org.reactivestreams.Publisher;
import reactor.rx.Streams;

import java.util.Optional;

final class DefaultSpaces extends AbstractOperations implements Spaces {

    private final CloudFoundryClient cloudFoundryClient;

    private final Optional<String> organizationId;

    DefaultSpaces(CloudFoundryClient cloudFoundryClient, Optional<String> organizationId) {
        this.cloudFoundryClient = cloudFoundryClient;
        this.organizationId = organizationId;
    }

    @Override
    public Publisher<Space> list() {
        String organizationId = this.organizationId
                .orElseThrow(() -> new IllegalStateException("No organization targeted"));

        return paginate(page -> ListSpacesRequest.builder().organizationId(organizationId).page(page).build(),
                request -> this.cloudFoundryClient.spaces().list(request))
                .flatMap(r -> Streams.from(r.getResources()))
                .map(resource -> Space.builder().id(resource.getMetadata().getId())
                        .name(resource.getEntity().getName()).build());
    }
}
