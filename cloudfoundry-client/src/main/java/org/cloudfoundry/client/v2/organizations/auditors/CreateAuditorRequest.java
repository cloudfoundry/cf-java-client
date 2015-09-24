/*
 * Copyright 2013-2015 the original author or authors.
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

package org.cloudfoundry.client.v2.organizations.auditors;

import com.fasterxml.jackson.annotation.JsonIgnore;
import org.cloudfoundry.client.Validatable;
import org.cloudfoundry.client.ValidationResult;

/**
 * The request payload for the Create Auditor operation
 *
 * <p><b>This class is NOT threadsafe.</b>
 */
public final class CreateAuditorRequest implements Validatable {

    private volatile String auditorId;

    private volatile String organizationId;

    /**
     * Returns the auditor id
     *
     * @return the auditor id
     */
    @JsonIgnore
    public String getAuditorId() {
        return this.auditorId;
    }

    /**
     * Configure the auditor id
     *
     * @param auditorId the auditor id
     * @return {@code this}
     */
    public CreateAuditorRequest withAuditorId(String auditorId) {
        this.auditorId = auditorId;
        return this;
    }

    /**
     * Returns the organization id
     *
     * @return the organization id
     */
    @JsonIgnore
    public String getOrganizationId() {
        return this.organizationId;
    }

    /**
     * Configure the organization id
     *
     * @param organizationId the organization id
     * @return {@code this}
     */
    public CreateAuditorRequest withOrganizationId(String organizationId) {
        this.organizationId = organizationId;
        return this;
    }

    @Override
    public ValidationResult isValid() {
        ValidationResult result = new ValidationResult();

        if (this.auditorId == null) {
            result.invalid("auditor id must be specified");
        }

        if (this.organizationId == null) {
            result.invalid("organization id must be specified");
        }

        return result;
    }
}
