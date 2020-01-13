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

package org.cloudfoundry.operations.serviceadmin;

import org.immutables.value.Value;

/**
 * Request options for the update service broker operation
 */
@Value.Immutable
abstract class _UpdateServiceBrokerRequest {

    /**
     * The name of the service broker
     */
    abstract String getName();

    /**
     * The password to authenticate with the broker
     */
    abstract String getPassword();

    /**
     * The url of the service broker
     */
    abstract String getUrl();

    /**
     * The username to authenticate with the broker
     */
    abstract String getUsername();
}
