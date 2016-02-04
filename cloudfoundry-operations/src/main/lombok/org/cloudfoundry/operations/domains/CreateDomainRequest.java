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

package org.cloudfoundry.operations.domains;

import lombok.Builder;
import lombok.Data;
import org.cloudfoundry.operations.Validatable;
import org.cloudfoundry.operations.ValidationResult;

/**
 * The request options for the create domain operation
 */
@Data
public final class CreateDomainRequest implements Validatable {

    /**
     * The domain name
     *
     * @param domain the domain name
     * @return the domain name
     */
    private final String domain;

    /**
     * The organization name of the domain.
     *
     * @param host the organization name
     * @return the organization name
     */
    private final String organization;

    @Builder
    public CreateDomainRequest(String domain, String organization) {
        this.domain = domain;
        this.organization = organization;
    }

    @Override
    public ValidationResult isValid() {
        ValidationResult.ValidationResultBuilder builder = ValidationResult.builder();

        if (this.domain == null) {
            builder.message("domain must be specified");
        }

        if (this.organization == null) {
            builder.message("organization must be specified");
        }

        return builder.build();
    }

}
