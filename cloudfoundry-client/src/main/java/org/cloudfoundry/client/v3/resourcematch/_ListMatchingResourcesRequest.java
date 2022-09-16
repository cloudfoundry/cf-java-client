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

package org.cloudfoundry.client.v3.resourcematch;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import org.immutables.value.Value;

import java.util.List;

/**
 * The request payload for the List Matching Resources operation
 */
@JsonSerialize
@Value.Immutable
abstract class _ListMatchingResourcesRequest {
    @Value.Check
    void check() {
        if (getResources().isEmpty()) {
            throw new IllegalStateException("Resources must have at least 1 resource");
        }
    }

    /**
     * The resources
     */
    abstract List<MatchedResource> getResources();
}
