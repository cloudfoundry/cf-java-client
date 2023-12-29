/*
 * Copyright 2013-2021 the original author or authors.
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

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.Optional;

/**
 * The payload for Group member
 */
abstract class AbstractMember {

    /**
     * Globally unique identifier of the member, either a user ID or another group ID
     */
    @JsonProperty("value")
    abstract String getMemberId();

    /**
     * The alias of the identity provider that authenticated this user. "uaa" is an internal UAA user.
     */
    @JsonProperty("origin")
    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    abstract Optional<String> getOrigin();

    /**
     * The member type
     */
    @JsonProperty("type")
    abstract Optional<MemberType> getType();
}
