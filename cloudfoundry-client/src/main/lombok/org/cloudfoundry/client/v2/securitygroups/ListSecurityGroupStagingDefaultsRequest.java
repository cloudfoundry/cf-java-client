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

package org.cloudfoundry.client.v2.securitygroups;

import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import org.cloudfoundry.Validatable;
import org.cloudfoundry.ValidationResult;
import org.cloudfoundry.client.v2.PaginatedRequest;

/**
 * The request payload for the List Staging Security Groups operation
 */
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public final class ListSecurityGroupStagingDefaultsRequest extends PaginatedRequest implements Validatable {

    @Builder
    ListSecurityGroupStagingDefaultsRequest(OrderDirection orderDirection, Integer page, Integer resultsPerPage) {
        super(orderDirection, page, resultsPerPage);
    }

    @Override
    public ValidationResult isValid() {
        return ValidationResult.builder().build();
    }

}
