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

/**
 * The entity response payload for the buildpack resource
 */
@Data
public final class BuildpackEntity {

    private final Boolean enabled;

    private final String filename;

    private final Boolean locked;

    private final String name;

    private final Integer position;

    @Builder
    BuildpackEntity(@JsonProperty("enabled") Boolean enabled,
                    @JsonProperty("filename") String filename,
                    @JsonProperty("locked") Boolean locked,
                    @JsonProperty("name") String name,
                    @JsonProperty("position") Integer position) {
        this.enabled = enabled;
        this.filename = filename;
        this.locked = locked;
        this.name = name;
        this.position = position;
    }

}
