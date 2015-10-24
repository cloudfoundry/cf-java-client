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

import lombok.Builder;
import lombok.Data;
import org.cloudfoundry.client.Validatable;
import org.cloudfoundry.client.ValidationResult;

import java.io.File;

/**
 * The request payload for the Upload Package operation
 */
@Data
public final class UploadPackageRequest implements Validatable {

    /**
     * The file
     *
     * @param file the file
     * @return the file
     */
    private final File file;

    /**
     * The id
     *
     * @param id the id
     * @return the id
     */
    private final String id;

    @Builder
    UploadPackageRequest(File file, String id) {
        this.file = file;
        this.id = id;
    }

    @Override
    public ValidationResult isValid() {
        ValidationResult.ValidationResultBuilder builder = ValidationResult.builder();

        if (this.file == null) {
            builder.message("file must be specified");
        }

        if (this.id == null) {
            builder.message("id must be specified");
        }

        return builder.build();
    }

}
