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

package org.cloudfoundry.uaa.groups;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import org.cloudfoundry.Nullable;
import org.immutables.value.Value;

/**
 * The payload for Group member
 */
abstract class AbstractMember {

    /**
     * The alias of the identity provider that authenticated this user. "uaa" is an internal UAA user.
     */
    @JsonProperty("origin")
    @Nullable
    abstract String getIdentityProviderOriginKey();

    /**
     * The member type
     */
    @JsonProperty("type")
    @Nullable
    abstract MemberType getType();

    /**
     * Globally unique identifier of the member, either a user ID or another group ID
     */
    @JsonProperty("value")
    abstract String getMemberId();

}
