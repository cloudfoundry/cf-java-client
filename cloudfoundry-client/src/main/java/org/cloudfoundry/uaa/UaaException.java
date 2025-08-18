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

package org.cloudfoundry.uaa;

import org.cloudfoundry.AbstractCloudFoundryException;

/**
 * An exception encapsulating an error returned from the UAA APIs
 */
public final class UaaException extends AbstractCloudFoundryException {

    private static final long serialVersionUID = -474523104186506972L;

    private final String error;

    private final String errorDescription;

    private final String payload;

    /**
     * Creates a new instance
     *
     * @param statusCode       the status code
     * @param error            the error
     * @param errorDescription the error description
     */
    public UaaException(Integer statusCode, String error, String errorDescription, String payload) {
        super(statusCode, String.format("%s: %s", error, errorDescription));
        this.error = error;
        this.errorDescription = errorDescription;
        this.payload = payload;
    }

    /**
     * Returns the error
     */
    public String getError() {
        return this.error;
    }

    /**
     * Returns the error message
     */
    public String getErrorDescription() {
        return this.errorDescription;
    }

    /**
     * Returns the full error payload
     */
    public String getPayload() {
        return payload;
    }
}
