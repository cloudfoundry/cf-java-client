/*
 * Copyright 2013-2021 the original author or authors.
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
import org.cloudfoundry.Nullable;
import org.cloudfoundry.client.v3.resourcematch.MatchedResource;
import org.immutables.value.Value;

import java.nio.file.Path;
import java.util.List;

/**
 * The request payload for the Upload Package operation
 */
@Value.Immutable
abstract class _UploadPackageRequest {
    @Value.Check
    void check() {
        if (getBits() == null && (getResources() == null || getResources().isEmpty())) {
            throw new IllegalStateException("At least one of resources or bits are required");
        }
    }

    /**
     * The resources
     */
    @JsonIgnore
    @Nullable
    abstract List<MatchedResource> getResources();

    /**
     * The bits
     */
    @JsonIgnore
    @Nullable
    abstract Path getBits();

    /**
     * The package id
     */
    @JsonIgnore
    abstract String getPackageId();

}
