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

package org.cloudfoundry.client.v3.organizations;

import reactor.core.publisher.Mono;

/**
 * Main entry point to the Cloud Foundry Organizations V3 Client API
 */
public interface OrganizationsV3 {

    /**
     * Makes the <a href="https://v3-apidocs.cloudfoundry.org/version/3.27.0/index.html#assign-default-isolation-segment">Assign Default Isolation Segment</a> request
     *
     * @param request the Assign Default Isolation Segment request
     * @return the response from the Assign Default Isolation Segment request
     */
    Mono<AssignOrganizationDefaultIsolationSegmentResponse> assignDefaultIsolationSegment(
            AssignOrganizationDefaultIsolationSegmentRequest request);

    /**
     * Makes the <a href="https://v3-apidocs.cloudfoundry.org/version/3.34.0/index.html#create-an-organization">Create Organization</a> request
     *
     * @param request the Create Organization request
     * @return the response from the Create Organization request
     */
    Mono<CreateOrganizationResponse> create(CreateOrganizationRequest request);

    /**
     * Makes the <a href="https://v3-apidocs.cloudfoundry.org/version/3.87.0/index.html#delete-an-organization">Delete Organization</a> request
     *
     * @param request the Delete Organization request
     * @return the response from the Delete Organization request
     */
    Mono<String> delete(DeleteOrganizationRequest request);

    /**
     * Makes the <a href="https://v3-apidocs.cloudfoundry.org/version/3.34.0/index.html#create-an-organization">Get Organization</a> request
     *
     * @param request the Get Organization request
     * @return the response from the Get Organization request
     */
    Mono<GetOrganizationResponse> get(GetOrganizationRequest request);

    /**
     * Makes the <a href="https://v3-apidocs.cloudfoundry.org/version/3.87.0/index.html#get-default-domain">Get Default Domain for an Organization</a> request
     *
     * @param request the Get Default Domain for an Organization request
     * @return the response from the Get Default Domain for an Organization request
     */
    Mono<GetOrganizationDefaultDomainResponse> getDefaultDomain(
            GetOrganizationDefaultDomainRequest request);

    /**
     * Makes the <a href="https://v3-apidocs.cloudfoundry.org/version/3.27.0/index.html#get-default-isolation-segment">Get Default Isolation Segment</a> request
     *
     * @param request the Get Default Isolation Segment request
     * @return the response from the Get Default Isolation Segment request
     */
    Mono<GetOrganizationDefaultIsolationSegmentResponse> getDefaultIsolationSegment(
            GetOrganizationDefaultIsolationSegmentRequest request);

    /**
     * Makes the <a href="https://v3-apidocs.cloudfoundry.org/version/3.87.0/index.html#get-usage-summary">Get Usage Summary</a> request
     *
     * @param request the Get Usage Summary request
     * @return the response from the Get Usage Summary request
     */
    Mono<GetOrganizationUsageSummaryResponse> getUsageSummary(
            GetOrganizationUsageSummaryRequest request);

    /**
     * Makes the <a href="https://v3-apidocs.cloudfoundry.org/version/3.27.0/index.html#list-organizations">List Organizations</a> request
     *
     * @param request the List Organizations request
     * @return the response from the List Organizations request
     */
    Mono<ListOrganizationsResponse> list(ListOrganizationsRequest request);

    /**
     * Makes <a href="http://v3-apidocs.cloudfoundry.org/version/3.77.0/index.html#list-domains-for-an-organization">List Organization Domains</a> request
     *
     * @param request the List Organization Domains request
     * @return the response from the List Organization Domains request
     */
    Mono<ListOrganizationDomainsResponse> listDomains(ListOrganizationDomainsRequest request);

    /**
     * Makes the <a href="https://v3-apidocs.cloudfoundry.org/version/3.82.0/#update-an-organization">Update an Organization</a> request
     *
     * @param request the Update Organization request
     * @return the response from the Update Organization request
     */
    Mono<UpdateOrganizationResponse> update(UpdateOrganizationRequest request);
}
