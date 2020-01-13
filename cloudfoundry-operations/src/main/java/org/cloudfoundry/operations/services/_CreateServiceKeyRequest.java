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

package org.cloudfoundry.operations.services;

import org.cloudfoundry.AllowNulls;
import org.cloudfoundry.Nullable;
import org.immutables.value.Value;

import java.util.Map;

/**
 * The request options for the create service key operation
 */
@Value.Immutable
abstract class _CreateServiceKeyRequest {

    /**
     * The parameters of the service key
     */
    @AllowNulls
    @Nullable
    abstract Map<String, Object> getParameters();

    /**
     * The name of the service instance
     */
    abstract String getServiceInstanceName();

    /**
     * The name of the service key to create
     */
    abstract String getServiceKeyName();

}
