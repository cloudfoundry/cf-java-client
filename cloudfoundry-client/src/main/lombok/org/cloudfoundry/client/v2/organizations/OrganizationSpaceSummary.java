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

package org.cloudfoundry.client.v2.organizations;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Data;

/**
 * The Space part of an Organization summary
 */
@Data
public final class OrganizationSpaceSummary {

    /**
     * The app count
     *
     * @param appCount the app count
     * @return the app count
     */
    private final Integer appCount;

    /**
     * The space id
     *
     * @param id the space id
     * @return the space id
     */
    private final String id;

    /**
     * The mem_dev_total
     *
     * @param memDevTotal the mem_dev_total
     * @return the mem_dev_total
     */
    private final Integer memDevTotal;

    /**
     * The mem_prod_total
     *
     * @param memProdTotal the mem_prod_total
     * @return the mem_prod_total
     */
    private final Integer memProdTotal;

    /**
     * The space name
     *
     * @param name the space name
     * @return the space name
     */
    private final String name;

    /**
     * The service count
     *
     * @param serviceCount the service count
     * @return the service count
     */
    private final Integer serviceCount;

    @Builder
    OrganizationSpaceSummary(@JsonProperty("app_count") Integer appCount,
                             @JsonProperty("guid") String id,
                             @JsonProperty("mem_dev_total") Integer memDevTotal,
                             @JsonProperty("mem_prod_total") Integer memProdTotal,
                             @JsonProperty("name") String name,
                             @JsonProperty("service_count") Integer serviceCount) {
        this.appCount = appCount;
        this.id = id;
        this.memDevTotal = memDevTotal;
        this.memProdTotal = memProdTotal;
        this.name = name;
        this.serviceCount = serviceCount;
    }

}
