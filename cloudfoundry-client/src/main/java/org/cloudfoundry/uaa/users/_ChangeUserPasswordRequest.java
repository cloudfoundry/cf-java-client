/*
 * Copyright 2013-2017 the original author or authors.
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

package org.cloudfoundry.uaa.users;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.cloudfoundry.Nullable;
import org.cloudfoundry.uaa.IdentityZoned;
import org.immutables.value.Value;

/**
 * The request payload for the change user password operation
 */
@Value.Immutable
abstract class _ChangeUserPasswordRequest implements IdentityZoned {

    /**
     * The user's existing password
     */
    @JsonProperty("oldPassword")
    @Nullable
    abstract String getOldPassword();

    /**
     * The user's desired password
     */
    @JsonProperty("password")
    abstract String getPassword();

    /**
     * The user id
     */
    @JsonIgnore
    abstract String getUserId();

}
