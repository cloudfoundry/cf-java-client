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
import org.cloudfoundry.Nullable;

abstract class AbstractTokenKey {

    /**
     * The algorithm
     */
    @JsonProperty("alg")
    abstract String getAlgorithm();

    /**
     * The exponent
     */
    @JsonProperty("e")
    abstract String getE();

    /**
     * The id
     */
    @JsonProperty("kid")
    @Nullable  // TODO: Remove once all test environments are on UAA 3.3.0 or later
    abstract String getId();

    /**
     * The key type
     */
    @JsonProperty("kty")
    abstract KeyType getKeyType();

    /**
     * The modulus
     */
    @JsonProperty("n")
    abstract String getN();

    /**
     * The use
     */
    @JsonProperty("use")
    abstract String getUse();

    /**
     * The value
     */
    @JsonProperty("value")
    abstract String getValue();

}
