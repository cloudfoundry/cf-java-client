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
import org.immutables.value.Value;

/**
 * The payload for lockout policy in internal identity provider management {@link InternalConfiguration}
 */
@JsonDeserialize
@Value.Immutable
abstract class _LockoutPolicy {

    /**
     * Number of seconds to lock out an account when lockoutAfterFailures failures is exceeded (defaults to 300).
     */
    @JsonProperty("countFailuresWithin")
    abstract Integer getLockAccountPeriodInSecond();

    /**
     * Number of seconds in which lockoutAfterFailures failures must occur in order for account to be locked (defaults to 3600).
     */
    @JsonProperty("lockoutPeriodSeconds")
    abstract Integer getLockoutPeriodInSecond();

    /**
     * Number of allowed failures before account is locked (defaults to 5).
     */
    @JsonProperty("lockoutAfterFailures")
    abstract Integer getNumberOfAllowedFailures();

}
