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

package org.cloudfoundry.client.v2.servicekeys;

import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Singular;
import lombok.ToString;
import org.cloudfoundry.Validatable;
import org.cloudfoundry.ValidationResult;
import org.cloudfoundry.client.v2.InFilterParameter;
import org.cloudfoundry.client.v2.OrderDirection;
import org.cloudfoundry.client.v2.PaginatedRequest;

import java.util.List;

/**
 * The request payload for the List Service Keys operation
 */
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public final class ListServiceKeysRequest extends PaginatedRequest implements Validatable {

    /**
     * The names
     *
     * @param names the names
     * @return the names
     */
    @Getter(onMethod = @__(@InFilterParameter("name")))
    private final List<String> names;

    /**
     * The service instance ids
     *
     * @param serviceInstanceIds the service instance ids
     * @return the service instance ids
     */
    @Getter(onMethod = @__(@InFilterParameter("service_instance_guid")))
    private final List<String> serviceInstanceIds;

    @Builder
    ListServiceKeysRequest(OrderDirection orderDirection, Integer page, Integer resultsPerPage,
                           @Singular List<String> names,
                           @Singular List<String> serviceInstanceIds) {
        super(orderDirection, page, resultsPerPage);
        this.names = names;
        this.serviceInstanceIds = serviceInstanceIds;
    }

    @Override
    public ValidationResult isValid() {
        return ValidationResult.builder().build();
    }

}
