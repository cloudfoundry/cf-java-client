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

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.Singular;
import lombok.ToString;

import java.util.List;

/**
 * The response from the token key request
 */
@Data
public final class ListTokenKeysResponse {

    private final List<TokenKey> keys;

    @Builder
    ListTokenKeysResponse(@JsonProperty("keys") @Singular List<TokenKey> keys) {
        this.keys = keys;
    }

    /**
     * The token key
     */
    @Data
    @EqualsAndHashCode(callSuper = true)
    @ToString(callSuper = true)
    public static final class TokenKey extends AbstractTokenKey {

        @Builder
        TokenKey(@JsonProperty("alg") String algorithm,
                 @JsonProperty("e") String e,
                 @JsonProperty("kid") String id,
                 @JsonProperty("kty") KeyType keyType,
                 @JsonProperty("n") String n,
                 @JsonProperty("use") String use,
                 @JsonProperty("value") String value) {

            super(algorithm, e, id, keyType, n, use, value);
        }

    }

}
