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

package org.cloudfoundry.client.v2;

import org.cloudfoundry.AbstractCloudFoundryException;

/**
 * An exception encapsulating an error returned from Cloud Foundry V2 APIs
 */
public final class ClientV2Exception extends AbstractCloudFoundryException {

    private static final long serialVersionUID = -5211312680168239905L;

    private final Integer code;

    private final String description;

    private final String errorCode;

    /**
     * Creates a new instance
     *
     * @param statusCode  the status code
     * @param code        the code
     * @param description the description
     * @param errorCode   the error code
     */
    public ClientV2Exception(Integer statusCode, Integer code, String description, String errorCode) {
        super(statusCode, String.format("%s(%d): %s", errorCode, code, description));
        this.code = code;
        this.description = description;
        this.errorCode = errorCode;
    }

    /**
     * Returns the code
     */
    public Integer getCode() {
        return this.code;
    }

    /**
     * Returns the description
     */
    public String getDescription() {
        return this.description;
    }

    /**
     * Returns the error code
     */
    public String getErrorCode() {
        return this.errorCode;
    }

}
