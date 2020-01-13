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

package org.cloudfoundry.uaa.identityzones;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import org.cloudfoundry.Nullable;
import org.immutables.value.Value;

/**
 * The payload for the key
 */
@JsonDeserialize
@Value.Immutable
abstract class _Key {

    /**
     * The certificate
     */
    @JsonProperty("certificate")
    @Nullable
    abstract String getCertificate();

    /**
     * The SAML provider's private key
     */
    @JsonProperty("key")
    @Nullable
    abstract String getKey();

    /**
     * The SAML provider's private key password
     */
    @JsonProperty("passphrase")
    @Nullable
    abstract String getPassphrase();

}
