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

package org.cloudfoundry.operations.domains;

import lombok.Builder;
import lombok.Data;

/**
 * Domain object representation
 */
@Data
public final class Domain {

    /**
     * The GUID of the domain
     *
     * @param domainId the GUID of the domain return the GUID of the domain
     */
    private final String domainId;

    /**
     * The name of the domain
     *
     * @param name the name return the name of the domain
     */
    private final String domainName;

    /**
     * The GUID of the owner organization
     *
     * @param owningOrganizationId the GUID of the owner organization return the GUID of the owner organization
     */
    private final String owningOrganizationId;


    @Builder
    Domain(String domainId, String domainName, String owningOrganizationId) {
        this.domainId = domainId;
        this.domainName = domainName;
        this.owningOrganizationId = owningOrganizationId;
    }

}
