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

package org.cloudfoundry.client.v2.featureflags;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import org.cloudfoundry.client.Validatable;
import org.cloudfoundry.client.ValidationResult;

import java.util.regex.Pattern;

/**
 * The request payload for the Get SetUserRoles feature flag operation
 */
@Data
public final class GetFeatureFlagRequest implements Validatable {

    private static final Pattern ALPHAS_AND_UNDERS = Pattern.compile("[a-z_]*");

    /**
     * The name of the feature flag
     *
     * @param name the name of the feature flag
     * @return the name of the feature flag
     */
    @Getter(onMethod = @__(@JsonIgnore))
    private final String name;

    @Builder
    GetFeatureFlagRequest(String name) {
        this.name = name;
    }

    @Override
    public ValidationResult isValid() {
        ValidationResult.ValidationResultBuilder builder = ValidationResult.builder();

        if (this.name == null) {
            builder.message("name must be specified");
        } else if (!validName(this.name)) {
            builder.message("name must consist only of alphabetic characters and underscores");
        }

        return builder.build();
    }

    private static boolean validName(String name) {
        return ALPHAS_AND_UNDERS.matcher(name).matches();
    }
}
