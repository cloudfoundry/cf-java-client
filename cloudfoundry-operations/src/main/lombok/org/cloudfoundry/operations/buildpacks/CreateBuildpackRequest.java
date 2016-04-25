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

/**
 * The request options for the create buildpack operation
 */
@Data
public class CreateBuildpackRequest implements Validatable {

    /**
     * The buildpack name
     *
     * @param buildpack the buildpack name
     * @return the buildpack name
     */
    private final String buildpack;

    /**
     * The buildpack path
     *
     * @param path the buildpack path
     * @return the buildpack path
     */
    private final String path;

    /**
     * The buildpack position
     *
     * @param position the buildpack position
     * @return the buildpack position
     */
    private final Integer position;

    /**
     * Enables the buildpack to be used for staging
     * Default value is true
     *
     * @param enable the enable option indicating whether the buildpack is used for staging
     * @return the enable option
     */
    private Boolean enable;


    @Builder
    CreateBuildpackRequest(String buildpack, String path, Integer position, Boolean enable) {
        this.buildpack = buildpack;
        this.path = path;
        this.position = position;
        this.enable = (enable != null ? enable : true);
    }

    @Override
    public ValidationResult isValid() {
        ValidationResult.ValidationResultBuilder builder = ValidationResult.builder();

        if (this.buildpack == null) {
            builder.message("buildpack must be specified");
        }

        if (this.path == null) {
            builder.message("path must be specified");
        }

        if (this.position == null) {
            builder.message("position must be specified");
        }

        return builder.build();
    }
}
