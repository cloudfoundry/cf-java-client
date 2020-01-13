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

package org.cloudfoundry.client.v2.applicationusageevents;

import reactor.core.publisher.Mono;

/**
 * Main entry point to the Cloud Foundry Application Usage Events Client API
 */
public interface ApplicationUsageEvents {

    /**
     * Makes the <a href="https://apidocs.cloudfoundry.org/latest-release/app_usage_events/retrieve_a_particular_app_usage_event.html">Get an Application Usage Event</a> request
     *
     * @param request the Get Application Usage Event request
     * @return the response from the Get all Application Usage Event request
     */
    Mono<GetApplicationUsageEventResponse> get(GetApplicationUsageEventRequest request);

    /**
     * Makes the <a href="https://apidocs.cloudfoundry.org/latest-release/app_usage_events/list_all_app_usage_events.html">List all Application Usage Events</a> request
     *
     * @param request the List all Application Usage Events request
     * @return the response from the List all Application Usage Events request
     */
    Mono<ListApplicationUsageEventsResponse> list(ListApplicationUsageEventsRequest request);

    /**
     * Makes the <a href="https://apidocs.cloudfoundry.org/latest-release/app_usage_events/purge_and_reseed_app_usage_events.html">Purge and Reseed Application Usage Events</a> request
     *
     * @param request the Purge and Reseed Application Usage Events
     * @return the response from the Purge and Reseed Application Usage Events request
     */
    Mono<Void> purgeAndReseed(PurgeAndReseedApplicationUsageEventsRequest request);

}
