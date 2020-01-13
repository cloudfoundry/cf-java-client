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

package org.cloudfoundry.client.v3.isolationsegments;

import reactor.core.publisher.Mono;

/**
 * Main entry point to the Cloud Foundry Isolation Zones Client API
 */
public interface IsolationSegments {

    /**
     * Makes the <a href="https://v3-apidocs.cloudfoundry.org/version/3.27.0/index.html#entitle-organizations-for-an-isolation-segment">
     * Add an Isolation Segment Organization Entitlement</a> request
     *
     * @param request the Add an Isolation Segment Organization Entitlement request
     * @return the response from the Add an Isolation Segment Organization Entitlement request
     */
    Mono<AddIsolationSegmentOrganizationEntitlementResponse> addOrganizationEntitlement(AddIsolationSegmentOrganizationEntitlementRequest request);

    /**
     * Makes the <a href="https://v3-apidocs.cloudfoundry.org/version/3.27.0/index.html#create-an-isolation-segment">Create an Isolation Segment</a> request
     *
     * @param request the Create Isolation Segment request
     * @return the response from the Create Isolation Segment request
     */
    Mono<CreateIsolationSegmentResponse> create(CreateIsolationSegmentRequest request);

    /**
     * Makes the <a href="https://v3-apidocs.cloudfoundry.org/version/3.27.0/index.html#delete-an-isolation-segment">Delete an Isolation Segment</a> request
     *
     * @param request the Delete Isolation Segment request
     * @return the response from the Delete Isolation Segment request
     */
    Mono<Void> delete(DeleteIsolationSegmentRequest request);

    /**
     * Makes the <a href="https://v3-apidocs.cloudfoundry.org/version/3.27.0/index.html#get-an-isolation-segment">Get an Isolation Segment</a> request
     *
     * @param request the Get Isolation Segment request
     * @return the response from the Get Isolation Segment request
     */
    Mono<GetIsolationSegmentResponse> get(GetIsolationSegmentRequest request);

    /**
     * Makes the <a href="https://v3-apidocs.cloudfoundry.org/version/3.27.0/index.html#list-isolation-segments">List Isolation Segments</a> request
     *
     * @param request the List Isolation Segments request
     * @return the response from the List Isolation Segments request
     */
    Mono<ListIsolationSegmentsResponse> list(ListIsolationSegmentsRequest request);

    /**
     * Makes the <a href="https://v3-apidocs.cloudfoundry.org/version/3.27.0/index.html#list-organizations-for-isolation-segment">List Organizations for Isolation Segment</a> request
     *
     * @param request the List Organizations for Isolation Segment request
     * @return the response from the List Organizations for Isolation Segment request
     */
    Mono<ListIsolationSegmentEntitledOrganizationsResponse> listEntitledOrganizations(ListIsolationSegmentEntitledOrganizationsRequest request);

    /**
     * Makes the <a href="https://v3-apidocs.cloudfoundry.org/version/3.27.0/index.html#list-organizations-relationship">List Organizations Relationship</a> request
     *
     * @param request the List Organizations Relationship request
     * @return the response from the List Organizations Relationship request
     */
    Mono<ListIsolationSegmentOrganizationsRelationshipResponse> listOrganizationsRelationship(ListIsolationSegmentOrganizationsRelationshipRequest request);

    /**
     * Makes the <a href="https://v3-apidocs.cloudfoundry.org/version/3.27.0/index.html#list-spaces-relationship">List Spaces Relationship</a> request
     *
     * @param request the List Spaces Relationship request
     * @return the response from the List Spaces Relationship request
     */
    Mono<ListIsolationSegmentSpacesRelationshipResponse> listSpacesRelationship(ListIsolationSegmentSpacesRelationshipRequest request);

    /**
     * Makes the <a href="https://v3-apidocs.cloudfoundry.org/version/3.27.0/index.html#revoke-entitlement-to-isolation-segment-for-an-organization">
     * Remove an Isolation Segment Organization Entitlement</a> request
     *
     * @param request the Remove an Isolation Segment Organization Entitlement request
     * @return the response from the Remove an Isolation Segment Organization Entitlement request
     */
    Mono<Void> removeOrganizationEntitlement(RemoveIsolationSegmentOrganizationEntitlementRequest request);

    /**
     * Makes the <a href="https://v3-apidocs.cloudfoundry.org/version/3.27.0/index.html#update-an-isolation-segment">Update an Isolation Segment</a> request
     *
     * @param request the Update Isolation Segment request
     * @return the response from the Update Isolation Segment request
     */
    Mono<UpdateIsolationSegmentResponse> update(UpdateIsolationSegmentRequest request);

}
