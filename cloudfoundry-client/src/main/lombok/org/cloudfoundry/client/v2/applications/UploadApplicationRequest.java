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

package org.cloudfoundry.client.v2.applications;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.Singular;
import org.cloudfoundry.client.QueryParameter;
import org.cloudfoundry.client.Validatable;
import org.cloudfoundry.client.ValidationResult;

import java.io.InputStream;
import java.util.List;

/**
 * Request payload for the Upload Application request.
 */
@Data
public final class UploadApplicationRequest implements Validatable {

    /**
     * A binary zip file containing the application bits.
     *
     * @param application the application bits file
     * @return the application bits file
     */
    @Getter(onMethod = @__(@JsonIgnore))
    private final InputStream application;

    /**
     * The application id
     *
     * @param applicationId the application id
     * @return the application id
     */
    @Getter(onMethod = @__(@JsonIgnore))
    private final String applicationId;

    /**
     * If true, a new asynchronous job is submitted to persist the bits and the job id is included in the response.
     *
     * @param async whether to persist in a separate job
     * @return whether to persist in a separate job
     */
    @Getter(onMethod = @__(@QueryParameter("async")))
    private final Boolean async;

    /**
     * Fingerprints of the application bits that have previously been pushed to Cloud Foundry.
     *
     * @param resources the fingerprints of application bits
     * @return the fingerprints of application bits
     */
    @Getter(onMethod = @__(@JsonIgnore))
    private final List<Resource> resources;

    @Builder
    UploadApplicationRequest(InputStream application,
                             String applicationId,
                             Boolean async,
                             @Singular List<Resource> resources) {
        this.application = application;
        this.applicationId = applicationId;
        this.async = async;
        this.resources = resources;
    }

    @Override
    public ValidationResult isValid() {
        ValidationResult.ValidationResultBuilder builder = ValidationResult.builder();

        if (this.application == null) {
            builder.message("application must be specified");
        }

        if (this.applicationId == null) {
            builder.message("application id must be specified");
        }

        for (Resource resource : this.resources) {
            builder.messages(resource.isValid().getMessages());
        }

        return builder.build();
    }

    /**
     * The request payload for the resources
     */
    @Data
    public static final class Resource implements Validatable {

        @Getter(onMethod = @__(@JsonProperty("sha1")))
        private final String hash;

        @Getter(onMethod = @__(@JsonProperty("fn")))
        private final String path;

        @Getter(onMethod = @__(@JsonProperty("size")))
        private final Integer size;

        @Builder
        Resource(String hash, String path, Integer size) {
            this.path = path;
            this.hash = hash;
            this.size = size;
        }

        @Override
        public ValidationResult isValid() {
            ValidationResult.ValidationResultBuilder builder = ValidationResult.builder();

            if (this.hash == null) {
                builder.message("resource hash must be specified");
            }

            if (this.path == null) {
                builder.message("resource path must be specified");
            }

            if (this.size == null) {
                builder.message("resource size must be specified");
            }

            return builder.build();
        }
    }

}
