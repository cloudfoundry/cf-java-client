/*
 * Copyright 2013-2020 the original author or authors.
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

package org.cloudfoundry.reactor.client.v2.serviceusageevents;

import org.cloudfoundry.client.v2.serviceusageevents.GetServiceUsageEventRequest;
import org.cloudfoundry.client.v2.serviceusageevents.GetServiceUsageEventResponse;
import org.cloudfoundry.client.v2.serviceusageevents.ListServiceUsageEventsRequest;
import org.cloudfoundry.client.v2.serviceusageevents.ListServiceUsageEventsResponse;
import org.cloudfoundry.client.v2.serviceusageevents.PurgeAndReseedServiceUsageEventsRequest;
import org.cloudfoundry.client.v2.serviceusageevents.ServiceUsageEvents;
import org.cloudfoundry.reactor.ConnectionContext;
import org.cloudfoundry.reactor.TokenProvider;
import org.cloudfoundry.reactor.client.v2.AbstractClientV2Operations;
import reactor.core.publisher.Mono;

import java.util.Map;

/**
 * The Reactor-based implementation of {@link ServiceUsageEvents}
 */
public final class ReactorServiceUsageEvents extends AbstractClientV2Operations implements ServiceUsageEvents {

    /**
     * Creates an instance
     *
     * @param connectionContext the {@link ConnectionContext} to use when communicating with the server
     * @param root              the root URI of the server. Typically something like {@code https://api.run.pivotal.io}.
     * @param tokenProvider     the {@link TokenProvider} to use when communicating with the server
     * @param requestTags       map with custom http headers which will be added to web request
     */
    public ReactorServiceUsageEvents(ConnectionContext connectionContext, Mono<String> root, TokenProvider tokenProvider, Map<String, String> requestTags) {
        super(connectionContext, root, tokenProvider, requestTags);
    }

    @Override
    public Mono<GetServiceUsageEventResponse> get(GetServiceUsageEventRequest request) {
        return get(request, GetServiceUsageEventResponse.class, builder -> builder.pathSegment("service_usage_events", request.getServiceUsageEventId()))
            .checkpoint();
    }

    @Override
    public Mono<ListServiceUsageEventsResponse> list(ListServiceUsageEventsRequest request) {
        return get(request, ListServiceUsageEventsResponse.class, builder -> builder.pathSegment("service_usage_events"))
            .checkpoint();
    }

    @Override
    public Mono<Void> purgeAndReseed(PurgeAndReseedServiceUsageEventsRequest request) {
        return post(request, Void.class, builder -> builder.pathSegment("service_usage_events", "destructively_purge_all_and_reseed_existing_instances"))
            .checkpoint();
    }

}
