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

package org.cloudfoundry.client.v2.events;

import reactor.core.publisher.Mono;

/**
 * Main entry point to the Cloud Foundry Events Client API
 */
public interface Events {

    /**
     * Makes the <a href="https://apidocs.cloudfoundry.org/latest-release/events/retrieve_a_particular_event.html">Get Event</a> request
     *
     * @param request the Get Event request
     * @return the response from the Get Event request
     */
    Mono<GetEventResponse> get(GetEventRequest request);

    /**
     * Makes the <a href="https://apidocs.cloudfoundry.org/latest-release/events/list_all_events.html">List Events</a> request
     *
     * @param request the List Events request
     * @return the response from the List Events request
     */
    Mono<ListEventsResponse> list(ListEventsRequest request);

}
