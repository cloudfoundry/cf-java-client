/*
 * Copyright 2013-2016 the original author or authors.
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

import reactor.Mono;

/**
 * Main entry point to the Cloud Foundry Space Quota Definitions Client API
 */
public interface SpaceQuotaDefinitions {

    /**
     * Makes the
     * <a href="http://apidocs.cloudfoundry.org/226/space_quota_definitions/retrieve_a_particular_space_quota_definition.html">Retrieve
     * a Particular Space Quota Definition</a> request
     *
     * @param request the Retrieve a Particular Space Quota Definition request
     * @return the response from the Retrieve a Particular Space Quota Definition request
     */
    Mono<GetSpaceQuotaDefinitionResponse> get(GetSpaceQuotaDefinitionRequest request);

    /**
     * Makes the
     * <a href="http://apidocs.cloudfoundry.org/214/space_quota_definitions/list_all_space_quota_definitions.html">List
     * all Space Quota Definitions</a> request
     *
     * @param request the List Space Quota Definitions request
     * @return the response from the List Space Quota Definitions request
     */
    Mono<ListSpaceQuotaDefinitionsResponse> list(ListSpaceQuotaDefinitionsRequest request);


}
