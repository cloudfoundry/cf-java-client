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

package org.cloudfoundry.spring.client.v3.droplets;

import lombok.ToString;
import org.cloudfoundry.client.v3.droplets.DeleteDropletRequest;
import org.cloudfoundry.client.v3.droplets.Droplets;
import org.cloudfoundry.client.v3.droplets.GetDropletRequest;
import org.cloudfoundry.client.v3.droplets.GetDropletResponse;
import org.cloudfoundry.client.v3.droplets.ListDropletsRequest;
import org.cloudfoundry.client.v3.droplets.ListDropletsResponse;
import org.cloudfoundry.spring.util.AbstractSpringOperations;
import org.springframework.web.client.RestOperations;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Scheduler;

import java.net.URI;

/**
 * The Spring-based implementation of {@link Droplets}
 */
@ToString(callSuper = true)
public final class SpringDroplets extends AbstractSpringOperations implements Droplets {

    /**
     * Creates an instance
     *
     * @param restOperations the {@link RestOperations} to use to communicate with the server
     * @param root           the root URI of the server.  Typically something like {@code https://api.run.pivotal.io}.
     * @param schedulerGroup The group to use when making requests
     */
    public SpringDroplets(RestOperations restOperations, URI root, Scheduler schedulerGroup) {
        super(restOperations, root, schedulerGroup);
    }

    @Override
    public Mono<Void> delete(DeleteDropletRequest request) {
        return delete(request, Void.class, builder -> builder.pathSegment("v3", "droplets", request.getDropletId()));
    }

    @Override
    public Mono<GetDropletResponse> get(GetDropletRequest request) {
        return get(request, GetDropletResponse.class, builder -> builder.pathSegment("v3", "droplets", request.getDropletId()));
    }

    @Override
    public Mono<ListDropletsResponse> list(ListDropletsRequest request) {
        return get(request, ListDropletsResponse.class, builder -> builder.pathSegment("v3", "droplets"));
    }

}
