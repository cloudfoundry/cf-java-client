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

package org.cloudfoundry.client.v3.organizationquotadefinitions;

import reactor.core.publisher.Mono;

/**
 * Main entry point to the Cloud Foundry Organization Quota Definitions Client API
 */
public interface OrganizationQuotaDefinitionsV3 {

    /**
     * Makes the <a href="https://v3-apidocs.cloudfoundry.org/version/3.200.0/index.html#create-organization-quota-definition">Create Organization Quota Definition</a>
     * request
     *
     * @param request the Create Organization Quota Definition request
     * @return the response from the Create Organization Quota Definition request
     */
    Mono<CreateOrganizationQuotaDefinitionResponse> create(
            CreateOrganizationQuotaDefinitionRequest request);

    /**
     * Makes the <a href="https://v3-apidocs.cloudfoundry.org/version/3.200.0/index.html#get-organization-quota-definition">Get Organization Quota Definition</a>
     * request
     *
     * @param request the Get Organization Quota Definition request
     * @return the response from the Get Organization Quota Definition request
     */
    Mono<GetOrganizationQuotaDefinitionResponse> get(
            GetOrganizationQuotaDefinitionRequest request);

    /**
     * Makes the <a href="https://v3-apidocs.cloudfoundry.org/version/3.200.0/index.html#list-organization-quotas">List all Organization Quota Definitions</a>
     * request
     *
     * @param request the List all Organization Quota Definitions request
     * @return the response from the List all Organization Quota Definitions request
     */
    Mono<ListOrganizationQuotaDefinitionsResponse> list(
            ListOrganizationQuotaDefinitionsRequest request);

    /** Makes the <a href="https://v3-apidocs.cloudfoundry.org/version/3.200.0/index.html#update-organization-quota-definition">Update Organization Quota Definition</a>
     * request
     *
     * @param request the Update Organization Quota Definition request
     * @return the response from the Update Organization Quota Definition request
     */
    Mono<UpdateOrganizationQuotaDefinitionResponse> update(
            UpdateOrganizationQuotaDefinitionRequest request);
}
