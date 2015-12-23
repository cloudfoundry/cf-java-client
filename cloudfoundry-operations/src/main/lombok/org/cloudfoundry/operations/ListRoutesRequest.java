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

package org.cloudfoundry.operations;

import lombok.Builder;
import lombok.Data;

/**
 * The request options for the list routes operation
 */
@Data
public final class ListRoutesRequest implements Validatable {

    /**
     * A level to indicate which routes to list
     *
     * @param level the level to list
     * @return the level to list
     */
    private final Level level;

    @Builder
    ListRoutesRequest(Level level) {
        this.level = level;
    }

    @Override
    public ValidationResult isValid() {
        return ValidationResult.builder().build();
    }

    public enum Level {
        /**
         * List routes for all the spaces in the current organisation
         */
        Organization,

        /**
         * List routes for the current space in the current organisation
         */
        Space
    }

}
