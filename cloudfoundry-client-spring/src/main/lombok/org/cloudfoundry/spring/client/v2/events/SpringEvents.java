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

package org.cloudfoundry.spring.client.v2.events;

import lombok.ToString;
import org.cloudfoundry.client.v2.events.Events;
import org.cloudfoundry.client.v2.events.GetEventRequest;
import org.cloudfoundry.client.v2.events.GetEventResponse;
import org.cloudfoundry.client.v2.events.ListEventsRequest;
import org.cloudfoundry.client.v2.events.ListEventsResponse;
import org.cloudfoundry.spring.client.v2.FilterBuilder;
import org.cloudfoundry.spring.util.AbstractSpringOperations;
import org.cloudfoundry.spring.util.QueryBuilder;
import org.springframework.web.client.RestOperations;
import reactor.core.publisher.Mono;
import reactor.core.publisher.SchedulerGroup;

import java.net.URI;

/**
 * The Spring-based implementation of {@link Events}
 */
@ToString(callSuper = true)
public final class SpringEvents extends AbstractSpringOperations implements Events {

    /**
     * Creates an instance
     *
     * @param restOperations the {@link RestOperations} to use to communicate with the server
     * @param root           the root URI of the server.  Typically something like {@code https://api.run.pivotal.io}.
     * @param schedulerGroup The group to use when making requests
     */
    public SpringEvents(RestOperations restOperations, URI root, SchedulerGroup schedulerGroup) {
        super(restOperations, root, schedulerGroup);
    }

    @Override
    public Mono<GetEventResponse> get(GetEventRequest request) {
        return get(request, GetEventResponse.class, builder -> builder.pathSegment("v2", "events", request.getEventId()));
    }

    @Override
    public Mono<ListEventsResponse> list(ListEventsRequest request) {
        return get(request, ListEventsResponse.class, builder -> {
            builder.pathSegment("v2", "events");
            FilterBuilder.augment(builder, request);
            QueryBuilder.augment(builder, request);
        });
    }

}
