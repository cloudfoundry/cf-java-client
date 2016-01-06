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

package org.cloudfoundry.client.v3.packages;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonValue;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import org.cloudfoundry.client.Validatable;
import org.cloudfoundry.client.ValidationResult;

import static org.cloudfoundry.client.v3.packages.CreatePackageRequest.PackageType.BITS;
import static org.cloudfoundry.client.v3.packages.CreatePackageRequest.PackageType.DOCKER;

/**
 * The request payload for the Create Package operation
 */
@Data
public final class CreatePackageRequest implements Validatable {

    /**
     * The application id
     *
     * @param applicationId the application id
     * @return the application id
     */
    @Getter(onMethod = @__(@JsonIgnore))
    private final String applicationId;

    /**
     * The type
     *
     * @param type the type
     * @return the type
     */
    @Getter(onMethod = @__(@JsonProperty("type")))
    private final PackageType type;

    /**
     * The url
     *
     * @param url the url
     * @return the url
     */
    @Getter(onMethod = @__(@JsonProperty("url")))
    private final String url;

    @Builder
    CreatePackageRequest(String applicationId, PackageType type, String url) {
        this.applicationId = applicationId;
        this.type = type;
        this.url = url;
    }

    @Override
    public ValidationResult isValid() {
        ValidationResult.ValidationResultBuilder builder = ValidationResult.builder();

        if (this.applicationId == null) {
            builder.message("application id must be specified");
        }

        if (this.type == null) {
            builder.message("type must be specified");
        }

        if (this.type == BITS && this.url != null) {
            builder.message("url must only be specified if type is DOCKER");
        }

        if (this.type == DOCKER && this.url == null) {
            builder.message("url must be specified if type is DOCKER");
        }

        return builder.build();
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
