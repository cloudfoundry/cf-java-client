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
import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import org.cloudfoundry.client.Validatable;
import org.cloudfoundry.client.ValidationResult;

import java.io.InputStream;

/**
 * The request payload for the Upload Package operation
 */
@Data
public final class UploadPackageRequest implements Validatable {

    /**
     * The application
     *
     * @param file the application
     * @return the application
     */
    @Getter(onMethod = @__(@JsonIgnore))
    private final InputStream application;

    /**
     * The package id
     *
     * @param packageId the package id
     * @return the package id
     */
    @Getter(onMethod = @__(@JsonIgnore))
    private final String packageId;

    @Builder
    UploadPackageRequest(InputStream application, String packageId) {
        this.application = application;
        this.packageId = packageId;
    }

    @Override
    public ValidationResult isValid() {
        ValidationResult.ValidationResultBuilder builder = ValidationResult.builder();

        if (this.application == null) {
            builder.message("application must be specified");
        }

        if (this.packageId == null) {
            builder.message("package id must be specified");
        }

        return builder.build();
    }

}
