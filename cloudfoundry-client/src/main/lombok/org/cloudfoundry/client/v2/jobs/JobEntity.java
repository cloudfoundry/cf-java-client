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

package org.cloudfoundry.client.v2.jobs;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Data;

/**
 * Job entity in response payloads
 */
@Data
public final class JobEntity {

    /**
     * The error
     *
     * @param error the error
     * @return the error
     */
    private final String error;

    /**
     * The error details
     *
     * @param errorDetails the error details
     * @return the error details
     */
    private final ErrorDetails errorDetails;

    /**
     * The id
     *
     * @param id the id
     * @return the id
     */
    private final String id;

    /**
     * The status
     *
     * @param status the status
     * @return the status
     */
    private final String status;

    @Builder
    JobEntity(@JsonProperty("error") String error,
              @JsonProperty("error_details") ErrorDetails errorDetails,
              @JsonProperty("guid") String id,
              @JsonProperty("status") String status) {
        this.error = error;
        this.errorDetails = errorDetails;
        this.id = id;
        this.status = status;
    }

    /**
     * The error details in {@link JobEntity} response payloads
     */
    @Data
    public static final class ErrorDetails {

        /**
         * The code
         *
         * @param code the code
         * @return the code
         */
        private final Integer code;

        /**
         * The description
         *
         * @param description the description
         * @return the description
         */
        private final String description;

        /**
         * The error code
         *
         * @param errorCode the error code
         * @return the error code
         */
        private final String errorCode;

        @Builder
        ErrorDetails(@JsonProperty("code") Integer code,
                     @JsonProperty("description") String description,
                     @JsonProperty("error_code") String errorCode) {
            this.code = code;
            this.description = description;
            this.errorCode = errorCode;
        }

    }

}
