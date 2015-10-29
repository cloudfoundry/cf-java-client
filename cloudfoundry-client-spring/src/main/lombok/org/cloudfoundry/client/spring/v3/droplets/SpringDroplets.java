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

package org.cloudfoundry.client.spring.v3.droplets;

import lombok.ToString;
import org.cloudfoundry.client.spring.util.AbstractSpringOperations;
import org.cloudfoundry.client.v3.droplets.DeleteDropletRequest;
import org.cloudfoundry.client.v3.droplets.Droplets;
import org.cloudfoundry.client.v3.droplets.GetDropletRequest;
import org.cloudfoundry.client.v3.droplets.GetDropletResponse;
import org.cloudfoundry.client.v3.droplets.ListDropletsRequest;
import org.cloudfoundry.client.v3.droplets.ListDropletsResponse;
import org.reactivestreams.Publisher;
import org.springframework.web.client.RestOperations;

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
     */
    public SpringDroplets(RestOperations restOperations, URI root) {
        super(restOperations, root);
    }

    @Override
    public Publisher<Void> delete(DeleteDropletRequest request) {
        return delete(request, builder -> builder.pathSegment("v3", "droplets", request.getId()));
    }

    @Override
    public Publisher<GetDropletResponse> get(GetDropletRequest request) {
        return get(request, GetDropletResponse.class,
                builder -> builder.pathSegment("v3", "droplets", request.getId()));
    }

    @Override
    public Publisher<ListDropletsResponse> list(ListDropletsRequest request) {
        return get(request, ListDropletsResponse.class, builder -> builder.pathSegment("v3", "droplets"));
    }

}
