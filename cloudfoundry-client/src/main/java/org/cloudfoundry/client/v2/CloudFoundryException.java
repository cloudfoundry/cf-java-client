/*
 * Copyright 2013-2015 the original author or authors.
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

/**
 * An exception encapsulating an error returned from Cloud Foundry
 */
public final class CloudFoundryException extends RuntimeException {

    private static final long serialVersionUID = 884665902665899098L;

    private final Integer code;

    private final String description;

    private final String errorCode;

    /**
     * Creates a new instance
     *
     * @param code        the code
     * @param description the description
     * @param errorCode   the error code
     */
    public CloudFoundryException(Integer code, String description, String errorCode) {
        this(code, description, errorCode, null);
    }

    /**
     * Creates a new instance
     *
     * @param code        the code
     * @param description the description
     * @param errorCode   the error code
     * @param cause       the cause
     */
    public CloudFoundryException(Integer code, String description, String errorCode, Throwable cause) {
        super(String.format("%s(%d): %s", errorCode, code, description), cause);
        this.code = code;
        this.description = description;
        this.errorCode = errorCode;
    }

    /**
     * Returns the code
     *
     * @return the code
     */
    public Integer getCode() {
        return this.code;
    }

    /**
     * Returns the description
     *
     * @return the description
     */
    public String getDescription() {
        return this.description;
    }

    /**
     * Returns the error code
     *
     * @return the error code
     */
    public String getErrorCode() {
        return this.errorCode;
    }

}
