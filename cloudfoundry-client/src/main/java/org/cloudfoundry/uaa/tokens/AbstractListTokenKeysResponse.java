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
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import org.immutables.value.Value;

import java.util.List;

/**
 * The response from the token key request
 */
@JsonDeserialize(as = ListTokenKeysResponse.class)
@Value.Immutable
abstract class AbstractListTokenKeysResponse {

    /**
     * The token keys
     */
    @JsonProperty("keys")
    abstract List<TokenKey> getKeys();

    /**
     * The token key
     */
    @Value.Immutable
    static abstract class AbstractTokenKey extends org.cloudfoundry.uaa.tokens.AbstractTokenKey {

    }

}
