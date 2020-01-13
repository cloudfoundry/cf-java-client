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
import org.immutables.value.Value;

/**
 * The payload for the identity zone client secret policy
 */
@JsonDeserialize
@Value.Immutable
abstract class _ClientSecretPolicy {

    /**
     * Maximum number of characters required for secret to be considered valid
     */
    @JsonProperty("maxLength")
    abstract Integer getMaximumLength();

    /**
     * Minimum number of characters required for secret to be considered valid
     */
    @JsonProperty("minLength")
    abstract Integer getMinimumLength();

    /**
     * Minimum number of digits required for secret to be considered valid
     */
    @JsonProperty("requireDigit")
    abstract Integer getRequireDigit();

    /**
     * Minimum number of lowercase characters required for secret to be considered valid
     */
    @JsonProperty("requireLowerCaseCharacter")
    abstract Integer getRequireLowerCaseCharacter();

    /**
     * Minimum number of special characters required for secret to be considered valid
     */
    @JsonProperty("requireSpecialCharacter")
    abstract Integer getRequireSpecialCharacter();

    /**
     * Minimum number of uppercase characters required for secret to be considered valid
     */
    @JsonProperty("requireUpperCaseCharacter")
    abstract Integer getRequireUpperCaseCharacter();

}
