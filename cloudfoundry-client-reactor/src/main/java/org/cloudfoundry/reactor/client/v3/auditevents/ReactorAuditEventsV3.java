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

package org.cloudfoundry.reactor.client.v3.auditevents;

import org.cloudfoundry.client.v3.auditevents.AuditEventsV3;
import org.cloudfoundry.client.v3.auditevents.GetAuditEventRequest;
import org.cloudfoundry.client.v3.auditevents.GetAuditEventResponse;
import org.cloudfoundry.client.v3.auditevents.ListAuditEventsRequest;
import org.cloudfoundry.client.v3.auditevents.ListAuditEventsResponse;
import org.cloudfoundry.reactor.ConnectionContext;
import org.cloudfoundry.reactor.TokenProvider;
import org.cloudfoundry.reactor.client.v3.AbstractClientV3Operations;
import reactor.core.publisher.Mono;

public class ReactorAuditEventsV3 extends AbstractClientV3Operations implements AuditEventsV3 {

    public ReactorAuditEventsV3(ConnectionContext connectionContext, Mono<String> root, TokenProvider tokenProvider) {
        super(connectionContext, root, tokenProvider);
    }

    @Override
    public Mono<GetAuditEventResponse> get(GetAuditEventRequest request) {
        return get(request, GetAuditEventResponse.class, uriComponentsBuilder -> uriComponentsBuilder.pathSegment("audit_events", request.getEventId()));
    }

    @Override
    public Mono<ListAuditEventsResponse> list(ListAuditEventsRequest request) {
        return get(request, ListAuditEventsResponse.class, uriComponentsBuilder -> uriComponentsBuilder.pathSegment("audit_events"))
            .checkpoint();
    }
}
