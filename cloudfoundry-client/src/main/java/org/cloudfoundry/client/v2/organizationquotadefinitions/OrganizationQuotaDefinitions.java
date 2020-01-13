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

package org.cloudfoundry.client.v2.organizationquotadefinitions;

import reactor.core.publisher.Mono;

/**
 * Main entry point to the Cloud Foundry Organization Quota Definitions Client API
 */
public interface OrganizationQuotaDefinitions {

    /**
     * Makes the <a href="https://apidocs.cloudfoundry.org/latest-release/organization_quota_definitions/creating_a_organization_quota_definition.html">Creating an Organization Quota Definition</a>
     * request
     *
     * @param request the Create an Organization Quota Definition request
     * @return the response from the Create an Organization Quota Definition request
     */
    Mono<CreateOrganizationQuotaDefinitionResponse> create(CreateOrganizationQuotaDefinitionRequest request);

    /**
     * Makes the <a href="https://apidocs.cloudfoundry.org/latest-release/organization_quota_definitions/delete_a_particular_organization_quota_definition.html">Delete an Organization Quota
     * Definition</a> request
     *
     * @param request the Delete an Organization Quota Definition request
     * @return the response from the Delete an Organization Quota Definition request
     */
    Mono<DeleteOrganizationQuotaDefinitionResponse> delete(DeleteOrganizationQuotaDefinitionRequest request);

    /**
     * Makes the <a href="https://apidocs.cloudfoundry.org/latest-release/organization_quota_definitions/retrieve_a_particular_organization_quota_definition.html">Retrieve a Particular Organization
     * Quota Definition</a> request
     *
     * @param request the Retrieve a Particular Organization Quota Definition request
     * @return the response from the Retrieve a Particular Organization Quota Definition request
     */
    Mono<GetOrganizationQuotaDefinitionResponse> get(GetOrganizationQuotaDefinitionRequest request);

    /**
     * Makes the <a href="https://apidocs.cloudfoundry.org/latest-release/organization_quota_definitions/list_all_organization_quota_definitions.html">List all Organization Quota Definitions</a>
     * request
     *
     * @param request the List all Organization Quota Definitions request
     * @return the response from the List all Organization Quota Definitions request
     */
    Mono<ListOrganizationQuotaDefinitionsResponse> list(ListOrganizationQuotaDefinitionsRequest request);

    /**
     * Makes the <a href="https://apidocs.cloudfoundry.org/latest-release/organization_quota_definitions/updating_a_organization_quota_definition.html">Update an Organization Quota Definition</a>
     * request
     *
     * @param request the Update an Organization Quota Definition request
     * @return the response from the Update an Organization Quota Definition request
     */
    Mono<UpdateOrganizationQuotaDefinitionResponse> update(UpdateOrganizationQuotaDefinitionRequest request);

}
