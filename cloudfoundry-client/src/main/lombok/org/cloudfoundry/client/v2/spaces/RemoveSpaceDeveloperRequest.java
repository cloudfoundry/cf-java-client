/*
 * Copyright 2015 the original author or authors.
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

package org.cloudfoundry.client.v2.spaces;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Singular;
import lombok.ToString;
import org.cloudfoundry.client.Validatable;
import org.cloudfoundry.client.ValidationResult;

import java.util.List;

/**
 * The request payload for the Remove Developer from the Space operation
 */
@Data
public final class RemoveSpaceDeveloperRequest implements Validatable {

    /**
     * The developer id
     *
     * @return the developer id
     */
    @Getter(onMethod = @__(@JsonIgnore))
    private volatile String developerId;

    /**
     * The id
     *
     * @return the id
     */
    @Getter(onMethod = @__(@JsonIgnore))
    private volatile String id;

    @Builder
    RemoveSpaceDeveloperRequest(String developerId, String id) {
        this.developerId = developerId;
        this.id = id;
    }

    @Override
    public ValidationResult isValid() {
        ValidationResult.ValidationResultBuilder builder = ValidationResult.builder();

        if (this.developerId == null) {
            builder.message("developer id must be specified");
        }

        if (this.id == null) {
            builder.message("id must be specified");
        }

        return builder.build();
    }

}
