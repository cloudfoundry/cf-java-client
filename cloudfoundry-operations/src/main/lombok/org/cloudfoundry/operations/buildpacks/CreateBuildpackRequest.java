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

package org.cloudfoundry.operations.buildpacks;

import lombok.Builder;
import lombok.Data;
import org.cloudfoundry.Validatable;
import org.cloudfoundry.ValidationResult;

import java.io.InputStream;

/**
 * The request options for the create buildpack operation
 */
@Data
public final class CreateBuildpackRequest implements Validatable {

    /**
     * The buildpack file stream
     *
     * @param buildpack the buildpack file stream
     * @return the buildpack file stream
     */
    private final InputStream buildpack;

    /**
     * Enables the buildpack to be used for staging
     *
     * @param enable the enable option indicating whether the buildpack is used for staging
     * @return the enable option
     */
    private final Boolean enable;

    /**
     * The buildpack file name
     *
     * @param fileName the buildpack file name
     * @return the buildpack file name
     */
    private final String fileName;

    /**
     * The buildpack name
     *
     * @param name the buildpack name
     * @return the buildpack name
     */
    private final String name;

    /**
     * The buildpack position
     *
     * @param position the buildpack position
     * @return the buildpack position
     */
    private final Integer position;

    @Builder
    CreateBuildpackRequest(InputStream buildpack, Boolean enable, String fileName, String name, Integer position) {
        this.buildpack = buildpack;
        this.enable = enable;
        this.fileName = fileName;
        this.name = name;
        this.position = position;
    }

    @Override
    public ValidationResult isValid() {
        ValidationResult.ValidationResultBuilder builder = ValidationResult.builder();

        if (this.buildpack == null) {
            builder.message("buildpack must be specified");
        }

        if (this.fileName == null) {
            builder.message("file name must be specified");
        }

        if (this.name == null) {
            builder.message("name must be specified");
        }

        if (this.position == null) {
            builder.message("position must be specified");
        }

        return builder.build();
    }

}
