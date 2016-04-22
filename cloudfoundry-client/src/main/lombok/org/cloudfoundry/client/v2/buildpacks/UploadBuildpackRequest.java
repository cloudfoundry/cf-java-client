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

package org.cloudfoundry.client.v2.buildpacks;


import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import org.cloudfoundry.Validatable;
import org.cloudfoundry.ValidationResult;

import java.io.InputStream;

/**
 * The request payload to Upload a Buildpack
 */
@Data
public final class UploadBuildpackRequest implements Validatable {

    /**
     * A binary zip file containing the buildpack bits.
     *
     * @param buildpack the buildpack bits file
     * @return the buildpack bits file
     */
    @Getter(onMethod = @__(@JsonIgnore))
    private final InputStream buildpack;

    /**
     * The buildpack id
     *
     * @param buildpackId the buildpack id
     * @return the buildpack id
     */
    @Getter(onMethod = @__(@JsonIgnore))
    private final String buildpackId;

    /**
     * The filename
     *
     * @param filename the name of the buildpack file
     * @return the filename
     */
    @Getter(onMethod = @__(@JsonIgnore))
    private final String filename;

    @Builder
    UploadBuildpackRequest(InputStream buildpack, String buildpackId, String filename) {
        this.buildpack = buildpack;
        this.buildpackId = buildpackId;
        this.filename = filename;
    }

    @Override
    public ValidationResult isValid() {
        ValidationResult.ValidationResultBuilder builder = ValidationResult.builder();

        if (this.buildpack == null) {
            builder.message("buildpack must be specified");
        }

        if (this.buildpackId == null) {
            builder.message("buildpack id must be specified");
        }

        if (this.filename == null) {
            builder.message("filename must be specified");
        }

        return builder.build();
    }

}
