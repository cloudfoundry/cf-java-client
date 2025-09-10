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

package org.cloudfoundry.client.v3.spacequotadefinitions;

import reactor.core.publisher.Mono;

/**
 * Main entry point to the Cloud Foundry Space Quota Definitions Client API
 */
public interface SpaceQuotaDefinitionsV3 {

    /**
     * Makes the <a href="https://v3-apidocs.cloudfoundry.org/version/3.200.0/index.html#create-space-quota-definition">Create Space Quota Definition</a>
     * request
     *
     * @param request the Create Space Quota Definition request
     * @return the response from the Create Space Quota Definition request
     */
    Mono<CreateSpaceQuotaDefinitionResponse> create(
            CreateSpaceQuotaDefinitionRequest request);
}
