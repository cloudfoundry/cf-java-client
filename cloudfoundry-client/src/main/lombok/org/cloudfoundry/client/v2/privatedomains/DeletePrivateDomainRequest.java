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

package org.cloudfoundry.client.v2.privatedomains;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import org.cloudfoundry.QueryParameter;
import org.cloudfoundry.Validatable;
import org.cloudfoundry.ValidationResult;

/**
 * The request payload for the Delete a Particular Private Domain operation
 */
@Data
public final class DeletePrivateDomainRequest implements Validatable {

    /**
     * The async
     *
     * @param async the async
     * @return the async
     */
    @Getter(onMethod = @__(@QueryParameter("async")))
    private final Boolean async;

    /**
     * The private domain id
     *
     * @param privateDomainId the private domain id
     * @return the private domain id
     */
    @Getter(onMethod = @__(@JsonIgnore))
    private final String privateDomainId;

    @Builder
    DeletePrivateDomainRequest(Boolean async, String privateDomainId) {
        this.async = async;
        this.privateDomainId = privateDomainId;
    }

    @Override
    public ValidationResult isValid() {
        ValidationResult.ValidationResultBuilder builder = ValidationResult.builder();

        if (this.privateDomainId == null) {
            builder.message("private domain id must be specified");
        }

        return builder.build();
    }

}
