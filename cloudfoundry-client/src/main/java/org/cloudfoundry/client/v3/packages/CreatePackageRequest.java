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
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonValue;
import org.cloudfoundry.client.Validatable;
import org.cloudfoundry.client.ValidationResult;

import static org.cloudfoundry.client.v3.packages.CreatePackageRequest.PackageType.BITS;
import static org.cloudfoundry.client.v3.packages.CreatePackageRequest.PackageType.DOCKER;

/**
 * The request payload for the Create Package operation
 *
 * <p><b>This class is NOT threadsafe.</b>
 */
public final class CreatePackageRequest implements Validatable {

    private volatile String applicationId;

    private volatile PackageType type;

    private volatile String url;

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
    public CreatePackageRequest withApplicationId(String applicationId) {
        this.applicationId = applicationId;
        return this;
    }

    /**
     * Returns the type
     *
     * @return the type
     */
    @JsonProperty("type")
    public PackageType getType() {
        return this.type;
    }

    /**
     * Configure the type
     *
     * @param type the type
     * @return {@code this}
     */
    public CreatePackageRequest withType(PackageType type) {
        this.type = type;
        return this;
    }

    /**
     * Returns the url
     *
     * @return the url
     */
    @JsonProperty("url")
    public String getUrl() {
        return this.url;
    }

    /**
     * Configure the url
     *
     * @param url the url
     * @return {@code this}
     */
    public CreatePackageRequest withUrl(String url) {
        this.url = url;
        return this;
    }

    @Override
    public ValidationResult isValid() {
        ValidationResult result = new ValidationResult();

        if (this.applicationId == null) {
            result.invalid("applicationId must be specified");
        }

        if (this.type == null) {
            result.invalid("type must be specified");
        }

        if (this.type == BITS && this.url != null) {
            result.invalid("url must only be specified if type is DOCKER");
        }

        if (this.type == DOCKER && this.url == null) {
            result.invalid("url must be specified if type is DOCKER");
        }

        return result;
    }

    /**
     * The package type of the {@link CreatePackageRequest}
     */
    public enum PackageType {

        /**
         * Indicates that package type should be bits
         */
        BITS,

        /**
         * Indicates that package type should be docker
         */
        DOCKER;

        @JsonValue
        @Override
        public String toString() {
            return name().toLowerCase();
        }

    }

}
