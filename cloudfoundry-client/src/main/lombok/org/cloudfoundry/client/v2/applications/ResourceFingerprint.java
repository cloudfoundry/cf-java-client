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

package org.cloudfoundry.client.v2.applications;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import org.cloudfoundry.client.Validatable;
import org.cloudfoundry.client.ValidationResult;

/**
 * The request payload for the resources in an Upload Application Bits operation
 */
@Data
public final class ResourceFingerprint implements Validatable {

    @Getter(onMethod = @__(@JsonProperty("sha1")))
    private final String hash;

    @Getter(onMethod = @__(@JsonProperty("fn")))
    private final String path;

    @Getter(onMethod = @__(@JsonProperty("size")))
    private final Integer size;

    @Builder
    ResourceFingerprint(String hash, String path, Integer size) {
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
