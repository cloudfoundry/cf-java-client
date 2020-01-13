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

package org.cloudfoundry.client.v2.spacequotadefinitions;

import reactor.core.publisher.Mono;

/**
 * Main entry point to the Cloud Foundry Space Quota Definitions Client API
 */
public interface SpaceQuotaDefinitions {

    /**
     * Makes the <a href="https://apidocs.cloudfoundry.org/latest-release/space_quota_definitions/associate_space_with_the_space_quota_definition.html">Associate a Space with a Space Quota
     * Definition</a> request
     *
     * @param request the Associate a Space with a Space Quota Definition request
     * @return the response from the Associate a Space with a Space Quota Definition request
     */
    Mono<AssociateSpaceQuotaDefinitionResponse> associateSpace(AssociateSpaceQuotaDefinitionRequest request);

    /**
     * Makes the <a href="https://apidocs.cloudfoundry.org/latest-release/space_quota_definitions/creating_a_space_quota_definition.html">Creating a Space Quota Definition</a> request
     *
     * @param request the Create a Space Quota Definition request
     * @return the response from the Create a Space Quota Definition request
     */
    Mono<CreateSpaceQuotaDefinitionResponse> create(CreateSpaceQuotaDefinitionRequest request);

    /**
     * Makes the <a href="https://apidocs.cloudfoundry.org/latest-release/space_quota_definitions/delete_a_particular_space_quota_definition.html">Delete a Particular Space Quota Definition</a> request
     *
     * @param request the Delete a Particular Space Quota Definition request
     * @return the response from the Delete a Particular Space Quota Definition request
     */
    Mono<DeleteSpaceQuotaDefinitionResponse> delete(DeleteSpaceQuotaDefinitionRequest request);

    /**
     * Makes the <a href="https://apidocs.cloudfoundry.org/latest-release/space_quota_definitions/retrieve_a_particular_space_quota_definition.html">Retrieve a Particular Space Quota Definition</a>
     * request
     *
     * @param request the Retrieve a Particular Space Quota Definition request
     * @return the response from the Retrieve a Particular Space Quota Definition request
     */
    Mono<GetSpaceQuotaDefinitionResponse> get(GetSpaceQuotaDefinitionRequest request);

    /**
     * Makes the <a href="https://apidocs.cloudfoundry.org/latest-release/space_quota_definitions/list_all_space_quota_definitions.html">List all Space Quota Definitions</a> request
     *
     * @param request the List Space Quota Definitions request
     * @return the response from the List Space Quota Definitions request
     */
    Mono<ListSpaceQuotaDefinitionsResponse> list(ListSpaceQuotaDefinitionsRequest request);

    /**
     * Makes the <a href="https://apidocs.cloudfoundry.org/latest-release/space_quota_definitions/list_all_spaces_for_the_space_quota_definition.html">List all Spaces for the Space Quota Definition</a>
     * request
     *
     * @param request the List all Spaces for the Space Quota Definition request
     * @return the response from the List all Spaces for the Space Quota Definition request
     */
    Mono<ListSpaceQuotaDefinitionSpacesResponse> listSpaces(ListSpaceQuotaDefinitionSpacesRequest request);

    /**
     * Makes the <a href="https://apidocs.cloudfoundry.org/latest-release/space_quota_definitions/remove_space_from_the_space_quota_definition.html">Remove a Space from a Space Quota Definition</a>
     * request
     *
     * @param request the Remove a Space from a Space Quota Definition request
     * @return the response from the Remove a Space from a Space Quota Definition request
     */
    Mono<Void> removeSpace(RemoveSpaceQuotaDefinitionRequest request);

    /**
     * Makes the <a href="https://apidocs.cloudfoundry.org/latest-release/space_quota_definitions/updating_a_space_quota_definition.html">Updating a Space Quota Definition</a> request
     *
     * @param request the Update Space Quota Definitions request
     * @return the response from the Update Space Quota Definitions request
     */
    Mono<UpdateSpaceQuotaDefinitionResponse> update(UpdateSpaceQuotaDefinitionRequest request);

}
