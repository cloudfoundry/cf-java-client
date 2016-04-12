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

/**
 * A Cloud Foundry Buildpack
 */
@Data
public final class Buildpack {

    /**
     * The enabled flag
     *
     * @param enabled whether or not the buildpack will be used for staging
     * @return the enabled flag
     */
    private final Boolean enabled;

    /**
     * The filename
     *
     * @param filename the name of the uploaded buildpack file
     * @return the filename
     */
    private final String filename;

    /**
     * The id
     *
     * @param id the id
     * @return the id
     */
    private final String id;

    /**
     * The locked flag
     *
     * @param locked whether or not the buildpack is locked to prevent updates
     * @return the locked flag
     */
    private final Boolean locked;

    /**
     * The name
     *
     * @param name the name
     * @return the name
     */
    private final String name;

    /**
     * The position
     *
     * @param position the order in which the buildpacks are checked during buildpack auto-detection.
     * @return the position
     */
    private final Integer position;


    @Builder
    Buildpack(Boolean enabled,
              String filename,
              String id,
              Boolean locked,
              String name,
              Integer position) {
        this.enabled = enabled;
        this.filename = filename;
        this.id = id;
        this.locked = locked;
        this.name = name;
        this.position = position;
    }

}
