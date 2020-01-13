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
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import org.cloudfoundry.Nullable;
import org.immutables.value.Value;

/**
 * The response from the refresh token operation
 */
@JsonDeserialize
@Value.Immutable
abstract class _RefreshTokenResponse extends AbstractToken {

    /**
     * The OpenId token
     */
    @JsonProperty("id_token")
    @Nullable
    abstract String getOpenIdToken();

    /**
     * The refresh token
     */
    @JsonProperty("refresh_token")
    abstract String getRefreshToken();

}
