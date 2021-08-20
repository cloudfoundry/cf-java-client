/*
 * Copyright 2013-2021 the original author or authors.
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

package org.cloudfoundry.client.v3.serviceinstances;

import reactor.core.publisher.Mono;

/**
 * Main entry point to the Cloud Foundry Service Instances V3 Client API
 */
public interface ServiceInstancesV3 {
    
    /**
     * Makes the <a href="http://v3-apidocs.cloudfoundry.org/version/release-candidate/#get-parameters-for-a-managed-service-instance">
     * Get parameters for a managed service instance</a> request
     * 
     * @param request the Get Managed Service Parameters request
     * @return the response from Get Managed Service Parameters request
     */
    Mono<GetManagedServiceParametersResponse> getManagedServiceParameters(GetManagedServiceParametersRequest request);
    
    /**
     * Makes the <a href="http://v3-apidocs.cloudfoundry.org/version/release-candidate/#get-credentials-for-a-user-provided-service-instance">
     * Get credentials for a user-provided service instance</a> request
     * 
     * @param request the Get User Provided Credentials request
     * @return the response from Get User provided Credentials request
     */
    Mono<GetUserProvidedCredentialsResponse> getUserProvidedCredentials(GetUserProvidedCredentialsRequest request);

    /**
     * Makes the <a href="https://v3-apidocs.cloudfoundry.org/version/release-candidate/index.html#list-service-instances">List service instances</a> request
     *
     * @param request the List Service Instances request
     * @return the response from the List Service Instances request
     */
    Mono<ListServiceInstancesResponse> list(ListServiceInstancesRequest request);

    /**
     * Makes the <a href="https://v3-apidocs.cloudfoundry.org/version/release-candidate/index.html#ist-shared-spaces-relationship">
     * List shared spaces relationship</a> request
     *
     * @param request the List Shared Spaces Relationship request
     * @return the response from the List Shared Spaces Relationship request
     */
    Mono<ListSharedSpacesRelationshipResponse> listSharedSpacesRelationship(ListSharedSpacesRelationshipRequest request);

    /**
     * Makes the <a href="https://v3-apidocs.cloudfoundry.org/version/release-candidate/index.html#share-a-service-instance-to-other-spaces">
     * Share a service instance to other spaces</a> request
     *
     * @param request the Share Service Instance To Other Spaces request
     * @return the response from the Share Service Instance To Other Spaces request
     */
    Mono<ShareServiceInstanceResponse> share(ShareServiceInstanceRequest request);

    /**
     * Makes the <a href="https://v3-apidocs.cloudfoundry.org/version/release-candidate/index.html#unshare-a-service-instance-from-another-space">
     * Unshare a service instance from another space</a> request
     *
     * @param request the Unshare Service Instance From Another Space request
     * @return the response from the Unshare Service Instance From Another Space request
     */
    Mono<Void> unshare(UnshareServiceInstanceRequest request);

    /**
     * Makes the <a href="https://v3-apidocs.cloudfoundry.org/version/release-candidate/index.html#update-a-service-instance">
     * Update a service instance</a> request
     *
     * @param request the Update Service Instance request
     * @return the response from the Update Service Instance request
     */
    Mono<UpdateServiceInstanceResponse> update(UpdateServiceInstanceRequest request);
}
