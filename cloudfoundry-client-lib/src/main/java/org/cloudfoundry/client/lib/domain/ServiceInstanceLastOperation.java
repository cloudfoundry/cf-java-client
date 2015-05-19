/*
 * Copyright 2009-2012 the original author or authors.
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

package org.cloudfoundry.client.lib.domain;


/**
 * Created by sebastien bortolussi.
 */
public class ServiceInstanceLastOperation {

    /**
     * The type of operation that was last performed or currently being performed on the service instance.
     */
    private String type;

    /**
     * The status of the last operation or current operation being performed on the service instance.
     */
    private OperationState state;

    /**
     * The service broker-provided description of the operation.
     */
    private String description;

    public ServiceInstanceLastOperation(String type, OperationState state, String description) {
        this.type = type;
        this.state = state;
        this.description = description;
    }

    public String getType() {
        return type;
    }

    public OperationState getState() {
        return state;
    }
}
