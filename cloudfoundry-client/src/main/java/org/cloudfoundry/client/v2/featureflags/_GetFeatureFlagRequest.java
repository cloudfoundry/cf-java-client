/*
 * Copyright 2013-2020 the original author or authors.
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

package org.cloudfoundry.client.v2.featureflags;

import com.fasterxml.jackson.annotation.JsonIgnore;
import org.immutables.value.Value;

import java.util.regex.Pattern;

/**
 * The request payload for the Get SetUserRoles feature flag operation
 */
@Value.Immutable
abstract class _GetFeatureFlagRequest {

    private static final Pattern ALPHAS_AND_UNDERS = Pattern.compile("[a-z_]*");

    @Value.Check
    void check() {
        if (!ALPHAS_AND_UNDERS.matcher(getName()).matches()) {
            throw new IllegalStateException("name must consist only of alphabetic characters and underscores");
        }
    }

    /**
     * The name of the feature flag
     */
    @JsonIgnore
    abstract String getName();

}
