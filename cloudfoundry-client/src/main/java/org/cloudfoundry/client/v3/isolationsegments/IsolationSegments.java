/*
 * Copyright 2013-2017 the original author or authors.
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
     * Makes the <a href="http://v3-apidocs.cloudfoundry.org/version/release-candidate/index.html#create-an-isolation-segment">Create an Isolation Segment</a> request
     *
     * @param request the Create Isolation Segment request
     * @return the response from the Create Isolation Segment request
     */
    Mono<CreateIsolationSegmentResponse> create(CreateIsolationSegmentRequest request);

    /**
     * Makes the <a href="http://v3-apidocs.cloudfoundry.org/version/release-candidate/index.html#delete-an-isolation-segment">Delete an Isolation Segment</a> request
     *
     * @param request the Delete Isolation Segment request
     * @return the response from the Delete Isolation Segment request
     */
    Mono<Void> delete(DeleteIsolationSegmentRequest request);

    /**
     * Makes the <a href="http://v3-apidocs.cloudfoundry.org/version/release-candidate/index.html#get-an-isolation-segment">Get an Isolation Segment</a> request
     *
     * @param request the Get Isolation Segment request
     * @return the response from the Get Isolation Segment request
     */
    Mono<GetIsolationSegmentResponse> get(GetIsolationSegmentRequest request);

    /**
     * Makes the <a href="http://v3-apidocs.cloudfoundry.org/version/release-candidate/index.html#list-isolation-segments">List Isolation Segments</a> request
     *
     * @param request the List Isolation Segments request
     * @return the response from the List Isolation Segments request
     */
    Mono<ListIsolationSegmentsResponse> list(ListIsolationSegmentsRequest request);

    /**
     * Makes the <a href="http://v3-apidocs.cloudfoundry.org/version/release-candidate/index.html#update-an-isolation-segment">Update an Isolation Segment</a> request
     *
     * @param request the Update Isolation Segment request
     * @return the response from the Update Isolation Segment request
     */
    Mono<UpdateIsolationSegmentResponse> update(UpdateIsolationSegmentRequest request);

}
