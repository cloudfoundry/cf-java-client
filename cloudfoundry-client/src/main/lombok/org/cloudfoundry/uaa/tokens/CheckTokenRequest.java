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

package org.cloudfoundry.uaa.tokens;

import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.Singular;
import org.cloudfoundry.QueryParameter;
import org.cloudfoundry.Validatable;
import org.cloudfoundry.ValidationResult;

import java.util.List;

/**
 * The request payload for the token key operation
 */
@Data
public final class CheckTokenRequest implements Validatable {

    /**
     * The scopes authorized by the user for this client
     *
     * @param scopes the scopes authorized by the user for this client
     * @return the scopes authorized by the user for this client
     */
    @Getter(onMethod = @__(@QueryParameter("scopes")))
    private final String scopes;

    /**
     * The token
     *
     * @param token the token
     * @return the token
     */
    @Getter(onMethod = @__(@QueryParameter("token")))
    private final String token;

    @Builder
    CheckTokenRequest(@Singular List<String> scopes, String token) {
        this.scopes = scopes.isEmpty() ? null : String.join(",", scopes);
        this.token = token;
    }

    @Override
    public ValidationResult isValid() {
        ValidationResult.ValidationResultBuilder builder = ValidationResult.builder();

        if (this.token == null) {
            builder.message("token must be specified");
        }

        return builder.build();
    }

}
