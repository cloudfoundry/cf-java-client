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

package org.cloudfoundry.client.v3.packages;

import com.fasterxml.jackson.annotation.JsonIgnore;
import org.cloudfoundry.client.Validatable;
import org.cloudfoundry.client.ValidationResult;

/**
 * The request payload for the Copy Package operation
 *
 * <p><b>This class is NOT threadsafe.</b>
 */
public final class CopyPackageRequest implements Validatable {

    private volatile String applicationId;

    private volatile String sourcePackageId;

    /**
     * Returns the application id
     *
     * @return the application id
     */
    @JsonIgnore
    public String getApplicationId() {
        return this.applicationId;
    }

    /**
     * Configure the application id
     *
     * @param applicationId the application id
     * @return {@code this}
     */
    public CopyPackageRequest withApplicationId(String applicationId) {
        this.applicationId = applicationId;
        return this;
    }

    /**
     * Returns the source package id
     *
     * @return the source package id
     */
    @JsonIgnore
    public String getSourcePackageId() {
        return this.sourcePackageId;
    }

    /**
     * Configure the source package id
     *
     * @param sourcePackageId the source package id
     * @return {@code this}
     */
    public CopyPackageRequest withSourcePackageId(String sourcePackageId) {
        this.sourcePackageId = sourcePackageId;
        return this;
    }

    @Override
    public ValidationResult isValid() {
        ValidationResult result = new ValidationResult();

        if (this.applicationId == null) {
            result.invalid("application id must be specified");
        }

        if (this.sourcePackageId == null) {
            result.invalid("source package id must be specified");
        }

        return result;
    }

}
