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

package org.cloudfoundry.operations.serviceadmin;

import lombok.Builder;
import lombok.Data;

/**
 * A Service Broker
 */
@Data
public final class ServiceBroker {

    /**
     * The name of the service broker
     *
     * @param name the name
     * @return the name
     */
    private final String name;

    /**
     * The url of the service broker
     *
     * @param url the url
     * @return the url
     */
    private final String url;

    @Builder
    ServiceBroker(String name, String url) {
        this.name = name;
        this.url = url;
    }

}
