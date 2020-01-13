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

package org.cloudfoundry.reactor.client.v2.applicationusageevents;

import org.cloudfoundry.client.v2.applicationusageevents.ApplicationUsageEvents;
import org.cloudfoundry.client.v2.applicationusageevents.GetApplicationUsageEventRequest;
import org.cloudfoundry.client.v2.applicationusageevents.GetApplicationUsageEventResponse;
import org.cloudfoundry.client.v2.applicationusageevents.ListApplicationUsageEventsRequest;
import org.cloudfoundry.client.v2.applicationusageevents.ListApplicationUsageEventsResponse;
import org.cloudfoundry.client.v2.applicationusageevents.PurgeAndReseedApplicationUsageEventsRequest;
import org.cloudfoundry.reactor.ConnectionContext;
import org.cloudfoundry.reactor.TokenProvider;
import org.cloudfoundry.reactor.client.v2.AbstractClientV2Operations;
import reactor.core.publisher.Mono;

import java.util.Map;

/**
 * The Reactor-based implementation of {@link ApplicationUsageEvents}
 */
public final class ReactorApplicationUsageEvents extends AbstractClientV2Operations implements ApplicationUsageEvents {

    /**
     * Creates an instance
     *
     * @param connectionContext the {@link ConnectionContext} to use when communicating with the server
     * @param root              the root URI of the server. Typically something like {@code https://api.run.pivotal.io}.
     * @param tokenProvider     the {@link TokenProvider} to use when communicating with the server
     * @param requestTags       map with custom http headers which will be added to web request
     */
    public ReactorApplicationUsageEvents(ConnectionContext connectionContext, Mono<String> root, TokenProvider tokenProvider, Map<String, String> requestTags) {
        super(connectionContext, root, tokenProvider, requestTags);
    }

    @Override
    public Mono<GetApplicationUsageEventResponse> get(GetApplicationUsageEventRequest request) {
        return get(request, GetApplicationUsageEventResponse.class, builder -> builder.pathSegment("app_usage_events", request.getApplicationUsageEventId()))
            .checkpoint();
    }

    @Override
    public Mono<ListApplicationUsageEventsResponse> list(ListApplicationUsageEventsRequest request) {
        return get(request, ListApplicationUsageEventsResponse.class, builder -> builder.pathSegment("app_usage_events"))
            .checkpoint();
    }

    @Override
    public Mono<Void> purgeAndReseed(PurgeAndReseedApplicationUsageEventsRequest request) {
        return post(request, Void.class, builder -> builder.pathSegment("app_usage_events", "destructively_purge_all_and_reseed_started_apps"))
            .checkpoint();
    }

}
