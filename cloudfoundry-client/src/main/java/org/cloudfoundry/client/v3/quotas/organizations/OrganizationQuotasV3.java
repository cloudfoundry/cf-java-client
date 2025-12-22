/*
 * Copyright 2013-2025 the original author or authors.
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

package org.cloudfoundry.client.v3.quotas.organizations;

import reactor.core.publisher.Mono;

/**
 * Main entry point to the Cloud Foundry Organization Quota Client API
 */
public interface OrganizationQuotasV3 {

    /**
     * Makes the <a href="https://v3-apidocs.cloudfoundry.org/version/3.200.0/index.html#create-an-organization-quota">Create Organization Quota </a>
     * request
     *
     * @param request the Create Organization Quota request
     * @return the response from the Create Organization Quota request
     */
    Mono<CreateOrganizationQuotaResponse> create(CreateOrganizationQuotaRequest request);

    /**
     * Makes the <a href="https://v3-apidocs.cloudfoundry.org/version/3.200.0/index.html#get-an-organization-quota">Get Organization Quota </a>
     * request
     *
     * @param request the Get Organization Quota request
     * @return the response from the Get Organization Quota request
     */
    Mono<GetOrganizationQuotaResponse> get(GetOrganizationQuotaRequest request);

    /**
     * Makes the <a href="https://v3-apidocs.cloudfoundry.org/version/3.200.0/index.html#list-organization-quotas">List all Organization Quota s</a>
     * request
     *
     * @param request the List all Organization Quotas request
     * @return the response from the List all Organization Quotas request
     */
    Mono<ListOrganizationQuotasResponse> list(ListOrganizationQuotasRequest request);

    /** Makes the <a href="https://v3-apidocs.cloudfoundry.org/version/3.200.0/index.html#update-an-organization-quota">Update Organization Quota </a>
     * request
     *
     * @param request the Update Organization Quota request
     * @return the response from the Update Organization Quota request
     */
    Mono<UpdateOrganizationQuotaResponse> update(UpdateOrganizationQuotaRequest request);

    /**
     * Makes the <a href="https://v3-apidocs.cloudfoundry.org/version/3.200.0/index.html#delete-an-organization-quota">Delete Organization Quota </a>
     * request
     *
     * @param request the Delete Organization Quota  request
     * @return the response from the Delete Organization Quota  request
     */
    Mono<String> delete(DeleteOrganizationQuotaRequest request);

    /**
     * Makes the <a href="https://v3-apidocs.cloudfoundry.org/version/3.200.0/index.html#apply-an-organization-quota-to-an-organization">Apply an Organization Quota to an Organization </a>
     * request
     *
     * @param request the Apply an Organization Quota to an Organization request
     * @return the response from the Apply an Organization Quota to an Organization request
     */
    Mono<ApplyOrganizationQuotaResponse> apply(ApplyOrganizationQuotaRequest request);
}
