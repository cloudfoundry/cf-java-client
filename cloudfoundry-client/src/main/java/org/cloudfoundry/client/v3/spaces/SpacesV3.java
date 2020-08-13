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

package org.cloudfoundry.client.v3.spaces;

import reactor.core.publisher.Mono;

/**
 * Main entry point to the Cloud Foundry Spaces V3 Client API
 */
public interface SpacesV3 {

    /**
     * Makes the <a href="https://v3-apidocs.cloudfoundry.org/version/3.27.0/index.html#assign-an-isolation-segment">Assign an Isolation Segment</a> request
     *
     * @param request the Assign an Isolation Segment request
     * @return the response from the Assign an Isolation Segment request
     */
    Mono<AssignSpaceIsolationSegmentResponse> assignIsolationSegment(AssignSpaceIsolationSegmentRequest request);

    /**
     * Makes the <a href="https://v3-apidocs.cloudfoundry.org/version/3.34.0/index.html#create-a-space">Get Space</a> request
     *
     * @param request the Create Space request
     * @return the response from the Create Space request
     */
    Mono<CreateSpaceResponse> create(CreateSpaceRequest request);

    /**
     * Makes the <a href="https://v3-apidocs.cloudfoundry.org/version/3.87.0/index.html#delete-a-space">Delete Space</a> request
     *
     * @param request the Delete Space request
     * @return the response from the Delete Space request
     */
    Mono<String> delete(DeleteSpaceRequest request);

    /**
     * Makes the <a href="https://v3-apidocs.cloudfoundry.org/version/3.77.0/index.html#delete-unmapped-routes-for-a-space">Delete Unmapped Routes</a> request
     *
     * @param request the Delete Unmapped Routes in Space request
     * @return the response from the Delete Unmapped Routes in Space request
     */
    Mono<String> deleteUnmappedRoutes(DeleteUnmappedRoutesRequest request);

    /**
     * Makes the <a href="https://v3-apidocs.cloudfoundry.org/version/3.27.0/index.html#get-a-space">Get Space</a> request
     *
     * @param request the Get Space request
     * @return the response from the Get Space request
     */
    Mono<GetSpaceResponse> get(GetSpaceRequest request);

    /**
     * Makes the <a href="https://v3-apidocs.cloudfoundry.org/version/release-candidate/index.html#get-assigned-isolation-segment">Get Assigned Isolation Segment</a> request
     *
     * @param request the Get Assigned Isolation Segment request
     * @return the response from the Get Assigned Isolation Segment request
     */
    Mono<GetSpaceIsolationSegmentResponse> getIsolationSegment(GetSpaceIsolationSegmentRequest request);

    /**
     * Makes the <a href="https://v3-apidocs.cloudfoundry.org/version/3.27.0/index.html#list-spaces">List Spaces</a> request
     *
     * @param request the List Spaces request
     * @return the response from the List Spaces request
     */
    Mono<ListSpacesResponse> list(ListSpacesRequest request);

    /**
     * Makes the <a href="https://v3-apidocs.cloudfoundry.org/version/3.82.0/#update-a-space">Update a Space</a> request
     *
     * @param request the Update Space request
     * @return the response from the Update Space request
     */
    Mono<UpdateSpaceResponse> update(UpdateSpaceRequest request);

}
