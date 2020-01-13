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

package org.cloudfoundry.operations.useradmin;

import org.cloudfoundry.Nullable;
import org.immutables.value.Value;

/**
 * The request options for the create user operation
 */
@Value.Immutable
abstract class _CreateUserRequest {

    @Value.Check
    void check() {
        if (getOrigin() == null && getPassword() == null) {
            throw new IllegalStateException("Cannot build CreateUserRequest, one of password or origin must be set");
        }

        if (getOrigin() != null && getPassword() != null) {
            throw new IllegalStateException("Cannot build CreateUserRequest, only one of password or origin can be set");
        }
    }

    /**
     * The identity provider that authenticated the new user
     */
    @Nullable
    abstract String getOrigin();

    /**
     * The password of the new user
     */
    @Nullable
    abstract String getPassword();

    /**
     * The username of the new user
     */
    abstract String getUsername();

}
