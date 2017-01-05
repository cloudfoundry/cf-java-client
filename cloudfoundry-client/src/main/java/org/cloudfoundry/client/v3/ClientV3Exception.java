/*
 * Copyright 2013-2017 the original author or authors.
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

package org.cloudfoundry.client.v3;

import org.cloudfoundry.AbstractCloudFoundryException;

import java.util.List;
import java.util.stream.Collectors;

/**
 * An exception encapsulating an error returned from Cloud Foundry V3 APIs
 */
public final class ClientV3Exception extends AbstractCloudFoundryException {

    private static final long serialVersionUID = 3422415564722151878L;

    private final List<Error> errors;

    /**
     * Creates a new instance
     *
     * @param statusCode the status code
     * @param errors     the errors
     */
    public ClientV3Exception(Integer statusCode, List<Error> errors) {
        super(statusCode, errors.stream().map(Error::toString).collect(Collectors.joining(", ")));
        this.errors = errors;
    }

    /**
     * Returns the errors
     */
    public List<Error> getErrors() {
        return this.errors;
    }

    /**
     * An error object encapsulating details about an error
     */
    public static final class Error {

        private final Integer code;

        private final String detail;

        private final String title;

        /**
         * Creates a new instance
         *
         * @param code   the code
         * @param detail the detail
         * @param title  the title
         */
        public Error(Integer code, String detail, String title) {
            this.code = code;
            this.detail = detail;
            this.title = title;
        }

        /**
         * Returns the code
         */
        public Integer getCode() {
            return this.code;
        }

        /**
         * Returns the detail
         */
        public String getDetail() {
            return this.detail;
        }

        /**
         * Returns the title
         */
        public String getTitle() {
            return this.title;
        }

        @Override
        public String toString() {
            return String.format("%s(%d): %s", this.title, this.code, this.detail);
        }

    }

}
