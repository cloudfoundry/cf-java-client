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

package org.cloudfoundry.spring.client.v2.serviceusageevents;

import lombok.ToString;
import org.cloudfoundry.client.v2.serviceusageevents.GetServiceUsageEventsRequest;
import org.cloudfoundry.client.v2.serviceusageevents.GetServiceUsageEventsResponse;
import org.cloudfoundry.client.v2.serviceusageevents.ListServiceUsageEventsRequest;
import org.cloudfoundry.client.v2.serviceusageevents.ListServiceUsageEventsResponse;
import org.cloudfoundry.client.v2.serviceusageevents.PurgeAndReseedServiceUsageEventsRequest;
import org.cloudfoundry.client.v2.serviceusageevents.ServiceUsageEvents;
import org.cloudfoundry.spring.client.v2.FilterBuilder;
import org.cloudfoundry.spring.util.AbstractSpringOperations;
import org.cloudfoundry.spring.util.QueryBuilder;
import org.springframework.web.client.RestOperations;
import reactor.core.publisher.Mono;
import reactor.core.publisher.SchedulerGroup;

import java.net.URI;

/**
 * The Spring-based implementation of {@link ServiceUsageEvents}
 */
@ToString(callSuper = true)
public final class SpringServiceUsageEvents extends AbstractSpringOperations implements ServiceUsageEvents {

    /**
     * Creates an instance
     *
     * @param restOperations the {@link RestOperations} to use to communicate with the server
     * @param root           the root URI of the server.  Typically something like {@code https://api.run.pivotal.io}.
     * @param schedulerGroup The group to use when making requests
     */
    public SpringServiceUsageEvents(RestOperations restOperations, URI root, SchedulerGroup schedulerGroup) {
        super(restOperations, root, schedulerGroup);
    }

    @Override
    public Mono<GetServiceUsageEventsResponse> get(GetServiceUsageEventsRequest request) {
        return get(request, GetServiceUsageEventsResponse.class, builder -> builder.pathSegment("v2", "service_usage_events", request.getServiceUsageEventId()));
    }

    @Override
    public Mono<ListServiceUsageEventsResponse> list(ListServiceUsageEventsRequest request) {
        return get(request, ListServiceUsageEventsResponse.class, builder -> {
            builder.pathSegment("v2", "service_usage_events");
            FilterBuilder.augment(builder, request);
            QueryBuilder.augment(builder, request);
        });
    }

    @Override
    public Mono<Void> purgeAndReseed(PurgeAndReseedServiceUsageEventsRequest request) {
        return post(request, Void.class, builder -> builder.pathSegment("v2", "service_usage_events", "destructively_purge_all_and_reseed_existing_instances"));
    }

}
