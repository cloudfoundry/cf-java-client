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
 * The payload for the identity zone client lockout policy
 */
@JsonDeserialize
@Value.Immutable
abstract class _ClientLockoutPolicy {

    /**
     * Number of seconds in which {@code lockoutAfterFailures} failures must occur in order for account to be locked
     */
    @JsonProperty("countFailuresWithin")
    abstract Integer getCountFailuresWithin();

    /**
     * Number of allowed failures before account is locked
     */
    @JsonProperty("lockoutAfterFailures")
    abstract Integer getLockoutAfterFailures();

    /**
     * Number of seconds to lock out an account when lockoutAfterFailures failures is exceeded
     */
    @JsonProperty("lockoutPeriodSeconds")
    abstract Integer getLockoutPeriodSeconds();

}
