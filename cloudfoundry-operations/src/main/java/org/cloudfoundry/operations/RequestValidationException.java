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

package org.cloudfoundry.operations;

import java.util.List;

/**
 * An exception indicating that a request was invalid
 */
public final class RequestValidationException extends IllegalArgumentException {

    private static final long serialVersionUID = 8196351272987333804L;

    /**
     * Creates a new instance
     *
     * @param validationResult the {@link ValidationResult} to read messages from
     */
    public RequestValidationException(ValidationResult validationResult) {
        super(getMessage(validationResult));
    }

    private static String getMessage(ValidationResult validationResult) {
        return "Request is invalid: " + join(validationResult.getMessages(), ", ");
    }

    private static String join(List<String> list, String conjunction) {
        if (list.isEmpty()) {
            return "";
        }

        StringBuilder sb = new StringBuilder().append(list.get(0));
        for (String item : list.subList(1, list.size())) {
            sb.append(conjunction).append(item);
        }

        return sb.toString();
    }

}
