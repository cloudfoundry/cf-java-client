/*
 * Copyright 2013-2019 the original author or authors.
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

package org.cloudfoundry.client.v3.auditevents;

import reactor.core.publisher.Mono;

/**
 * Main entry point to the Cloud Foundry Audit Events V3 Client API
 */
public interface AuditEventsV3 {

    /**
     * Makes the <a href="http://v3-apidocs.cloudfoundry.org/version/3.84.0/index.html#get-an-audit-event">Get Audit Event</a> request
     *
     * @param request the Get Audit Event request
     * @return the response from the Get Audit Event request
     */
    Mono<GetAuditEventResponse> get(GetAuditEventRequest request);

    /**
     * Makes the <a href="https://v3-apidocs.cloudfoundry.org/version/3.84.0/index.html#list-audit-events">List Audit Events</a> request
     *
     * @param request the List Audit Events request
     * @return the response from the List Audit Events request
     */
    Mono<ListAuditEventsResponse> list(ListAuditEventsRequest request);

}
