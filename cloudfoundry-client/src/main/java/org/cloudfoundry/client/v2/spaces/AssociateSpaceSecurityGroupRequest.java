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

package org.cloudfoundry.client.v2.spaces;

import com.fasterxml.jackson.annotation.JsonIgnore;
import org.cloudfoundry.client.Validatable;
import org.cloudfoundry.client.ValidationResult;

/**
 * The request payload for the Associate Security Group with the Space operation
 *
 * <p><b>This class is NOT threadsafe.</b>
 */
public final class AssociateSpaceSecurityGroupRequest implements Validatable {

    private volatile String id;

    private volatile String securityGroupId;

    /**
     * Returns the security group id
     *
     * @return the security group id
     */
    @JsonIgnore
    public String getSecurityGroupId() {
        return this.securityGroupId;
    }

    /**
     * Configure the security group id
     *
     * @param securityGroupId the security group id
     * @return {@code this}
     */
    public AssociateSpaceSecurityGroupRequest withSecurityGroupId(String securityGroupId) {
        this.securityGroupId = securityGroupId;
        return this;
    }

    /**
     * Returns the id
     *
     * @return the id
     */
    @JsonIgnore
    public String getId() {
        return this.id;
    }

    /**
     * Configure the id
     *
     * @param id the id
     * @return {@code this}
     */
    public AssociateSpaceSecurityGroupRequest withId(String id) {
        this.id = id;
        return this;
    }

    @Override
    public ValidationResult isValid() {
        ValidationResult result = new ValidationResult();

        if (this.id == null) {
            result.invalid("id must be specified");
        }

        if (this.securityGroupId == null) {
            result.invalid("security group id must be specified");
        }

        return result;
    }
}
