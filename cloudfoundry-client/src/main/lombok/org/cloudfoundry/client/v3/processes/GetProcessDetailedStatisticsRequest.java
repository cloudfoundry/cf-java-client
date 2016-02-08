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

package org.cloudfoundry.client.v3.processes;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import org.cloudfoundry.client.Validatable;
import org.cloudfoundry.client.ValidationResult;
import org.cloudfoundry.client.v3.PaginatedRequest;

/**
 * The request payload for the Get Detailed Statistics for a Process operation
 */
@Data
@EqualsAndHashCode(callSuper = true)
public final class GetProcessDetailedStatisticsRequest extends PaginatedRequest implements Validatable {

    /**
     * The process id
     *
     * @param processId the process id
     * @return the process id
     */
    @Getter(onMethod = @__(@JsonIgnore))
    private final String processId;

    @Builder
    GetProcessDetailedStatisticsRequest(Integer page, Integer perPage, String processId) {
        super(page, perPage);
        this.processId = processId;
    }

    @Override
    public ValidationResult isValid() {
        ValidationResult.ValidationResultBuilder builder = ValidationResult.builder();

        if (this.processId == null) {
            builder.message("process id must be specified");
        }

        return builder.build();
    }

}
