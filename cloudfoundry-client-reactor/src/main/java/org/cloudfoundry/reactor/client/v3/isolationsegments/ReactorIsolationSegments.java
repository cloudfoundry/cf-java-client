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

package org.cloudfoundry.reactor.client.v3.isolationsegments;

import org.cloudfoundry.client.v3.isolationsegments.AddIsolationSegmentOrganizationEntitlementRequest;
import org.cloudfoundry.client.v3.isolationsegments.AddIsolationSegmentOrganizationEntitlementResponse;
import org.cloudfoundry.client.v3.isolationsegments.CreateIsolationSegmentRequest;
import org.cloudfoundry.client.v3.isolationsegments.CreateIsolationSegmentResponse;
import org.cloudfoundry.client.v3.isolationsegments.DeleteIsolationSegmentRequest;
import org.cloudfoundry.client.v3.isolationsegments.GetIsolationSegmentRequest;
import org.cloudfoundry.client.v3.isolationsegments.GetIsolationSegmentResponse;
import org.cloudfoundry.client.v3.isolationsegments.IsolationSegments;
import org.cloudfoundry.client.v3.isolationsegments.ListIsolationSegmentEntitledOrganizationsRequest;
import org.cloudfoundry.client.v3.isolationsegments.ListIsolationSegmentEntitledOrganizationsResponse;
import org.cloudfoundry.client.v3.isolationsegments.ListIsolationSegmentOrganizationsRelationshipRequest;
import org.cloudfoundry.client.v3.isolationsegments.ListIsolationSegmentOrganizationsRelationshipResponse;
import org.cloudfoundry.client.v3.isolationsegments.ListIsolationSegmentSpacesRelationshipRequest;
import org.cloudfoundry.client.v3.isolationsegments.ListIsolationSegmentSpacesRelationshipResponse;
import org.cloudfoundry.client.v3.isolationsegments.ListIsolationSegmentsRequest;
import org.cloudfoundry.client.v3.isolationsegments.ListIsolationSegmentsResponse;
import org.cloudfoundry.client.v3.isolationsegments.RemoveIsolationSegmentOrganizationEntitlementRequest;
import org.cloudfoundry.client.v3.isolationsegments.UpdateIsolationSegmentRequest;
import org.cloudfoundry.client.v3.isolationsegments.UpdateIsolationSegmentResponse;
import org.cloudfoundry.reactor.ConnectionContext;
import org.cloudfoundry.reactor.TokenProvider;
import org.cloudfoundry.reactor.client.v3.AbstractClientV3Operations;
import reactor.core.publisher.Mono;

import java.util.Map;

/**
 * The Reactor-based implementation of {@link IsolationSegments}
 */
public final class ReactorIsolationSegments extends AbstractClientV3Operations implements IsolationSegments {

    /**
     * Creates an instance
     *
     * @param connectionContext the {@link ConnectionContext} to use when communicating with the server
     * @param root              the root URI of the server. Typically something like {@code https://api.run.pivotal.io}.
     * @param tokenProvider     the {@link TokenProvider} to use when communicating with the server
     * @param requestTags       map with custom http headers which will be added to web request
     */
    public ReactorIsolationSegments(ConnectionContext connectionContext, Mono<String> root, TokenProvider tokenProvider, Map<String, String> requestTags) {
        super(connectionContext, root, tokenProvider, requestTags);
    }

    @Override
    public Mono<AddIsolationSegmentOrganizationEntitlementResponse> addOrganizationEntitlement(AddIsolationSegmentOrganizationEntitlementRequest request) {
        return post(request, AddIsolationSegmentOrganizationEntitlementResponse.class, builder ->
            builder.pathSegment("isolation_segments", request.getIsolationSegmentId(), "relationships", "organizations"))
            .checkpoint();
    }

    @Override
    public Mono<CreateIsolationSegmentResponse> create(CreateIsolationSegmentRequest request) {
        return post(request, CreateIsolationSegmentResponse.class, builder -> builder.pathSegment("isolation_segments"))
            .checkpoint();
    }

    @Override
    public Mono<Void> delete(DeleteIsolationSegmentRequest request) {
        return delete(request, Void.class, builder -> builder.pathSegment("isolation_segments", request.getIsolationSegmentId()))
            .checkpoint();
    }

    @Override
    public Mono<GetIsolationSegmentResponse> get(GetIsolationSegmentRequest request) {
        return get(request, GetIsolationSegmentResponse.class, builder -> builder.pathSegment("isolation_segments", request.getIsolationSegmentId()))
            .checkpoint();
    }

    @Override
    public Mono<ListIsolationSegmentsResponse> list(ListIsolationSegmentsRequest request) {
        return get(request, ListIsolationSegmentsResponse.class, builder -> builder.pathSegment("isolation_segments"))
            .checkpoint();
    }

    @Override
    public Mono<ListIsolationSegmentEntitledOrganizationsResponse> listEntitledOrganizations(ListIsolationSegmentEntitledOrganizationsRequest request) {
        return get(request, ListIsolationSegmentEntitledOrganizationsResponse.class, builder -> builder.pathSegment("isolation_segments", request.getIsolationSegmentId(), "organizations"))
            .checkpoint();
    }

    @Override
    public Mono<ListIsolationSegmentOrganizationsRelationshipResponse> listOrganizationsRelationship(ListIsolationSegmentOrganizationsRelationshipRequest request) {
        return get(request, ListIsolationSegmentOrganizationsRelationshipResponse.class, builder ->
            builder.pathSegment("isolation_segments", request.getIsolationSegmentId(), "relationships", "organizations"))
            .checkpoint();
    }

    @Override
    public Mono<ListIsolationSegmentSpacesRelationshipResponse> listSpacesRelationship(ListIsolationSegmentSpacesRelationshipRequest request) {
        return get(request, ListIsolationSegmentSpacesRelationshipResponse.class, builder ->
            builder.pathSegment("isolation_segments", request.getIsolationSegmentId(), "relationships", "spaces"))
            .checkpoint();
    }

    @Override
    public Mono<Void> removeOrganizationEntitlement(RemoveIsolationSegmentOrganizationEntitlementRequest request) {
        return delete(request, Void.class, builder -> builder.pathSegment("isolation_segments", request.getIsolationSegmentId(), "relationships", "organizations", request.getOrganizationId()))
            .checkpoint();
    }

    @Override
    public Mono<UpdateIsolationSegmentResponse> update(UpdateIsolationSegmentRequest request) {
        return patch(request, UpdateIsolationSegmentResponse.class, builder -> builder.pathSegment("isolation_segments", request.getIsolationSegmentId()))
            .checkpoint();
    }

}
