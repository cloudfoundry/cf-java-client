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

package org.cloudfoundry.client.logging;

import lombok.Builder;
import lombok.Data;
import org.cloudfoundry.client.Validatable;
import org.cloudfoundry.client.ValidationResult;

/**
 * The request payload for the Recent Logs operation
 */
@Data
public final class RecentLogsRequest implements Validatable {

    /**
     * The application id
     *
     * @param applicationId the application id
     * @return the application id
     */
    private final String applicationId;

    @Builder
    RecentLogsRequest(String applicationId) {
        this.applicationId = applicationId;
    }

    @Override
    public ValidationResult isValid() {
        ValidationResult.ValidationResultBuilder builder = ValidationResult.builder();

        if (this.applicationId == null) {
            builder.message("application id must be specified");
        }

        return builder.build();
    }

}
