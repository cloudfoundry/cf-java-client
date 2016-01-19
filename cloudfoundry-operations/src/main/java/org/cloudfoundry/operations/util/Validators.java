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

package org.cloudfoundry.operations.util;

import org.cloudfoundry.operations.RequestValidationException;
import org.cloudfoundry.operations.Validatable;
import org.cloudfoundry.operations.ValidationResult;
import reactor.core.publisher.Mono;

/**
 * Utiltities for dealing with {@link Validatable} types
 */
public final class Validators {

    private Validators() {
    }

    /**
     * Validates a request throwing an error if the request is not valid
     *
     * @param request the request to validate
     * @param <T>     the type of the request
     * @return a {@link Mono} containing the validated request
     */
    public static <T extends Validatable> Mono<T> validate(T request) {
        ValidationResult validationResult = request.isValid();
        if (validationResult.getStatus() == ValidationResult.Status.INVALID) {
            return Mono.error(new RequestValidationException(validationResult));
        } else {
            return Mono.just(request);
        }
    }

}
