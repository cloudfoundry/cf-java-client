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

package org.cloudfoundry.client.v2.serviceusageevents;


import reactor.core.publisher.Mono;

/**
 * Main entry point to the Cloud Foundry Service Usage Events V2 Client API
 */
public interface ServiceUsageEvents {

    /**
     * Makes the <a href="https://apidocs.cloudfoundry.org/latest-release/service_usage_events/retrieve_a_particular_service_usage_event.html">Retrieve a Particular Service Usage Events</a> request
     *
     * @param request the Get Service Usage Events
     * @return the response from the Get Service Usage Events request
     */
    Mono<GetServiceUsageEventResponse> get(GetServiceUsageEventRequest request);

    /**
     * Makes the <a href="https://apidocs.cloudfoundry.org/latest-release/service_usage_events/list_service_usage_events.html">List Service Usage Events</a> request
     *
     * @param request the List Service Usage Events request
     * @return the response from the List Service Usage Events request
     */
    Mono<ListServiceUsageEventsResponse> list(ListServiceUsageEventsRequest request);

    /**
     * Makes the <a href="https://apidocs.cloudfoundry.org/latest-release/service_usage_events/purge_and_reseed_service_usage_events.html">Purge and Reseed Service Usage Events</a> request
     *
     * @param request the Purge and Reseed Service Usage Events
     * @return the response from the Purge and Reseed Service Usage Events request
     */
    Mono<Void> purgeAndReseed(PurgeAndReseedServiceUsageEventsRequest request);

}
