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

package org.cloudfoundry.client;

import java.util.stream.Collectors;

/**
 * An exception indicating that a request was invalid
 */
public final class RequestValidationException extends RuntimeException {

    private static final long serialVersionUID = -5182957592674088047L;

    /**
     * Creates a new instance
     *
     * @param validationResult the {@link ValidationResult} to read messages from
     */
    public RequestValidationException(ValidationResult validationResult) {
        super(getMessage(validationResult));
    }

    private static String getMessage(ValidationResult validationResult) {
        return "Request is invalid: " + validationResult.getMessages().stream().collect(Collectors.joining(", "));
    }

}
