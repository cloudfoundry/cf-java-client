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

package org.cloudfoundry.client.v2.spacequotadefinitions;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import org.cloudfoundry.Validatable;
import org.cloudfoundry.ValidationResult;

/**
 * The request payload for the Remove Space from the Space Quota Definition operation
 */
@Data
public final class RemoveSpaceQuotaDefinitionRequest implements Validatable {

    /**
     * The space id
     *
     * @param spaceId the space id
     * @return the space id
     */
    @Getter(onMethod = @__(@JsonIgnore))
    private final String spaceId;

    /**
     * The space quota definition id
     *
     * @param spaceQuotaDefinitionId the space quota definition id
     * @return the space quota definition id
     */
    @Getter(onMethod = @__(@JsonIgnore))
    private final String spaceQuotaDefinitionId;

    @Builder
    RemoveSpaceQuotaDefinitionRequest(String spaceId, String spaceQuotaDefinitionId) {
        this.spaceId = spaceId;
        this.spaceQuotaDefinitionId = spaceQuotaDefinitionId;
    }

    @Override
    public ValidationResult isValid() {
        ValidationResult.ValidationResultBuilder builder = ValidationResult.builder();

        if (this.spaceId == null) {
            builder.message("space id must be specified");
        }

        if (this.spaceQuotaDefinitionId == null) {
            builder.message("space quota definition id must be specified");
        }

        return builder.build();
    }

}
