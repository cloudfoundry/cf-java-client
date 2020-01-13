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

package org.cloudfoundry.uaa.tokens;

import com.fasterxml.jackson.annotation.JsonProperty;
import org.cloudfoundry.Nullable;

/**
 * The base class for token keys
 */
public abstract class AbstractTokenKey {

    /**
     * The algorithm
     */
    @JsonProperty("alg")
    public abstract String getAlgorithm();

    /**
     * The exponent
     */
    @JsonProperty("e")
    public abstract String getE();

    /**
     * The id
     */
    @JsonProperty("kid")
    @Nullable
    public abstract String getId();

    /**
     * The key type
     */
    @JsonProperty("kty")
    public abstract KeyType getKeyType();

    /**
     * The modulus
     */
    @JsonProperty("n")
    public abstract String getN();

    /**
     * The use
     */
    @JsonProperty("use")
    public abstract String getUse();

    /**
     * The value
     */
    @JsonProperty("value")
    public abstract String getValue();

}
