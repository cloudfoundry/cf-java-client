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

package org.cloudfoundry.operations;

import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.Singular;
import lombok.ToString;

import java.util.List;

/**
 * A Cloud Foundry Application as returned by list
 */
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public class ApplicationSummary extends AbstractApplicationSummary {

    @Builder
    protected ApplicationSummary(Integer diskQuota,
                                 String id,
                                 Integer instances,
                                 Integer memoryLimit,
                                 String name,
                                 String requestedState,
                                 Integer runningInstances,
                                 @Singular List<String> urls) {
        super(diskQuota, id, instances, memoryLimit, name, requestedState, runningInstances, urls);
    }

}
