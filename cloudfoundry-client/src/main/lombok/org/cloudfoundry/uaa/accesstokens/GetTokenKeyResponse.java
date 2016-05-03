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

package org.cloudfoundry.uaa.accesstokens;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Data;

/**
 * The response from the token key request
 */
@Data
public final class GetTokenKeyResponse {

    /**
     * The algorithm
     *
     * @param algorithm the algorithm
     * @return the algorithm
     */
    private final String algorithm;

    /**
     * The exponent
     *
     * @param e the exponent
     * @return the exponent
     */
    private final String e;

    /**
     * The key type
     *
     * @param keyType the keyType
     * @return the key type
     */
    private final String keyType;

    /**
     * The modulus
     *
     * @param n the modulus
     * @return the modulus
     */
    private final String n;

    /**
     * The use
     *
     * @param use the use
     * @return the use
     */
    private final String use;

    /**
     * The value
     *
     * @param value the value
     * @return the value
     */
    private final String value;

    @Builder
    GetTokenKeyResponse(@JsonProperty("alg") String algorithm,
                        @JsonProperty("e") String e,
                        @JsonProperty("kty") String keyType,
                        @JsonProperty("n") String n,
                        @JsonProperty("use") String use,
                        @JsonProperty("value") String value) {
        this.algorithm = algorithm;
        this.e = e;
        this.keyType = keyType;
        this.n = n;
        this.use = use;
        this.value = value;
    }

}
