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

package org.cloudfoundry.uaa.identityproviders;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import org.cloudfoundry.Nullable;
import org.immutables.value.Value;

/**
 * The payload for password policy in internal identity provider management {@link InternalConfiguration}
 */
@JsonDeserialize
@Value.Immutable
abstract class _PasswordPolicy {

    /**
     * Maximum number of characters required for password to be considered valid (defaults to 255).
     */
    @JsonProperty("maxLength")
    abstract Integer getMaximumLength();

    /**
     * Minimum number of digits required for password to be considered valid (defaults to 0).
     */
    @JsonProperty("requireDigit")
    abstract Integer getMinimumDigit();

    /**
     * Minimum number of characters required for password to be considered valid (defaults to 0).
     */
    @JsonProperty("minLength")
    abstract Integer getMinimumLength();

    /**
     * Minimum number of lower characters required for password to be considered valid (defaults to 0).
     */
    @JsonProperty("requireLowerCaseCharacter")
    abstract Integer getMinimumLowerCaseCharacter();

    /**
     * Minimum number of special characters required for password to be considered valid (defaults to 0).
     */
    @JsonProperty("requireSpecialCharacter")
    abstract Integer getMinimumSpecialCharacter();

    /**
     * Minimum number of uppercase characters required for password to be considered valid (defaults to 0).
     */
    @JsonProperty("requireUpperCaseCharacter")
    abstract Integer getMinimumUpperCaseCharacter();

    /**
     * Number of months after which current password expires (defaults to 0).
     */
    @JsonProperty("expirePasswordInMonths")
    abstract Integer getPasswordExpirationInMonth();

    /**
     * This timestamp value can be used to force change password for every user. If the user’s passwordLastModified is older than this value, the password is expired (defaults to null).
     */
    @JsonProperty("passwordNewerThan")
    @Nullable
    abstract Integer getPasswordNewerThan();

}
