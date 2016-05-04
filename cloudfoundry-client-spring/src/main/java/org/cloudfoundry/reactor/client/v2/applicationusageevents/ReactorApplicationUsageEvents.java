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

package org.cloudfoundry.reactor.client.v2.applicationusageevents;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.cloudfoundry.client.v2.applicationusageevents.ApplicationUsageEvents;
import org.cloudfoundry.client.v2.applicationusageevents.GetApplicationUsageEventRequest;
import org.cloudfoundry.client.v2.applicationusageevents.GetApplicationUsageEventResponse;
import org.cloudfoundry.client.v2.applicationusageevents.ListApplicationUsageEventsRequest;
import org.cloudfoundry.client.v2.applicationusageevents.ListApplicationUsageEventsResponse;
import org.cloudfoundry.client.v2.applicationusageevents.PurgeAndReseedApplicationUsageEventsRequest;
import org.cloudfoundry.reactor.client.v2.AbstractClientV2Operations;
import org.cloudfoundry.reactor.util.AuthorizationProvider;
import reactor.core.publisher.Mono;
import reactor.io.netty.http.HttpClient;

import static org.cloudfoundry.util.tuple.TupleUtils.function;

/**
 * The Reactor-based implementation of {@link ApplicationUsageEvents}
 */
public final class ReactorApplicationUsageEvents extends AbstractClientV2Operations implements ApplicationUsageEvents {

    /**
     * Creates an instance
     *
     * @param authorizationProvider the {@link AuthorizationProvider} to use when communicating with the server
     * @param httpClient            the {@link HttpClient} to use when communicating with the server
     * @param objectMapper          the {@link ObjectMapper} to use when communicating with the server
     * @param root                  the root URI of the server.  Typically something like {@code https://uaa.run.pivotal.io}.
     */
    public ReactorApplicationUsageEvents(AuthorizationProvider authorizationProvider, HttpClient httpClient, ObjectMapper objectMapper, Mono<String> root) {
        super(authorizationProvider, httpClient, objectMapper, root);
    }

    @Override
    public Mono<GetApplicationUsageEventResponse> get(GetApplicationUsageEventRequest request) {
        return get(request, GetApplicationUsageEventResponse.class, function((builder, validRequest) -> builder.pathSegment("v2", "app_usage_events", validRequest.getApplicationUsageEventId())));
    }

    @Override
    public Mono<ListApplicationUsageEventsResponse> list(ListApplicationUsageEventsRequest request) {
        return get(request, ListApplicationUsageEventsResponse.class, function((builder, validRequest) -> builder.pathSegment("v2", "app_usage_events")));
    }

    @Override
    public Mono<Void> purgeAndReseed(PurgeAndReseedApplicationUsageEventsRequest request) {
        return post(request, Void.class, function((builder, validRequest) -> builder.pathSegment("v2", "app_usage_events", "destructively_purge_all_and_reseed_started_apps")));
    }

}
