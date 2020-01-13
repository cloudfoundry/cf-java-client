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

import com.fasterxml.jackson.annotation.JacksonAnnotationsInside;
import com.fasterxml.jackson.annotation.JsonIgnore;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import static org.cloudfoundry.client.v2.FilterParameter.Operation.IN;
import static org.cloudfoundry.client.v2.FilterParameter.Operation.IS;

/**
 * An annotation indicating that a method represents a Cloud Foundry V2 filter parameter
 */
@JacksonAnnotationsInside
@JsonIgnore
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface FilterParameter {

    /**
     * Returns the collection operation for the filter.  Defaults to {@link Operation#IN}
     *
     * @return the collection operation for the filter
     */
    Operation collectionOperation() default IN;

    /**
     * Returns the operation for the filter.  Defaults to {@link Operation#IS}
     *
     * @return the operation for the filter
     */
    Operation operation() default IS;

    /**
     * Returns the name of the parameter
     *
     * @return the name of the parameter
     */
    String value();

    /**
     * Operations in a Cloud Foundry V2 filter
     */
    enum Operation {

        /**
         * Greater than or equal to.  Renders to {@code >}.
         */
        GREATER_THAN(">"),

        /**
         * Greater than or equal to.  Renders to {@code >=}.
         */
        GREATER_THAN_OR_EQUAL_TO(">="),

        /**
         * In.  Renders to {@code  IN }.
         */
        IN(" IN "),

        /**
         * Is.  Renders to {@code :}.
         */
        IS(":"),

        /**
         * Less than.  Renders to {@code <}.
         */
        LESS_THAN("<"),

        /**
         * Less than or equal to.  Renders to {@code <=}.
         */
        LESS_THAN_OR_EQUAL_TO("<=");

        private final String value;

        Operation(String value) {
            this.value = value;
        }

        @Override
        public String toString() {
            return this.value;
        }

    }

}
