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


import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import org.cloudfoundry.Validatable;
import org.cloudfoundry.ValidationResult;

/**
 * The request payload for the Create Buildpack
 */
@Data
public final class CreateBuildpackRequest implements Validatable {

    /**
     * The enabled flag
     *
     * @param enabled whether or not the buildpack will be used for staging
     * @return the enabled flag
     */
    @Getter(onMethod = @__(@JsonProperty("enabled")))
    private final Boolean enabled;

    /**
     * The filename
     *
     * @param filename the name of the uploaded buildpack file
     * @return the filename
     */
    @Getter(onMethod = @__(@JsonProperty("filename")))
    private final String filename;

    /**
     * The locked flag
     *
     * @param locked whether or not the buildpack is locked to prevent updates
     * @return the locked flag
     */
    @Getter(onMethod = @__(@JsonProperty("locked")))
    private final Boolean locked;

    /**
     * The name
     *
     * @param name the name
     * @return the name
     */
    @Getter(onMethod = @__(@JsonProperty("name")))
    private final String name;

    /**
     * The position
     *
     * @param position the order in which the buildpacks are checked during buildpack auto-detection.
     * @return the position
     */
    @Getter(onMethod = @__(@JsonProperty("position")))
    private final Integer position;

    @Builder
    CreateBuildpackRequest(Boolean enabled,
                           String filename,
                           Boolean locked,
                           String name,
                           Integer position) {
        this.enabled = enabled;
        this.filename = filename;
        this.locked = locked;
        this.name = name;
        this.position = position;
    }

    @Override
    public ValidationResult isValid() {
        ValidationResult.ValidationResultBuilder builder = ValidationResult.builder();

        if (this.name == null) {
            builder.message("buildpack name must be specified");
        }

        return builder.build();
    }

}
