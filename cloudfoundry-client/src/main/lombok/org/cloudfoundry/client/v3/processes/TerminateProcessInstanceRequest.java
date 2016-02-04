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
import lombok.Getter;
import org.cloudfoundry.client.Validatable;
import org.cloudfoundry.client.ValidationResult;

/**
 * The request payload for the Terminate Process Instance operation
 */
@Data
public final class TerminateProcessInstanceRequest implements Validatable {

    /**
     * The index
     *
     * @param index the index
     * @return the index
     */
    @Getter(onMethod = @__(@JsonIgnore))
    private final String index;

    /**
     * The process id
     *
     * @param processId the process id
     * @return the process id
     */
    @Getter(onMethod = @__(@JsonIgnore))
    private final String processId;

    @Builder
    TerminateProcessInstanceRequest(String index, String processId) {
        this.index = index;
        this.processId = processId;
    }

    @Override
    public ValidationResult isValid() {
        ValidationResult.ValidationResultBuilder builder = ValidationResult.builder();

        if (this.index == null) {
            builder.message("index must be specified");
        }

        if (this.processId == null) {
            builder.message("process id must be specified");
        }

        return builder.build();
    }

}
