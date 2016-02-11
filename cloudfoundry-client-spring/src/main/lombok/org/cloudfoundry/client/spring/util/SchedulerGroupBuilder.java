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

package org.cloudfoundry.client.spring.util;

import lombok.Setter;
import lombok.ToString;
import lombok.experimental.Accessors;
import org.cloudfoundry.utils.Optional;
import reactor.core.publisher.SchedulerGroup;
import reactor.core.util.PlatformDependent;

@Accessors(chain = true, fluent = true)
@Setter
@ToString
public final class SchedulerGroupBuilder {

    private Boolean autoShutdown;

    private Integer bufferSize;

    private Integer concurrency;

    private String name;

    public SchedulerGroup build() {
        return SchedulerGroup
            .io(this.name,
                Optional.ofNullable(this.bufferSize).orElse(PlatformDependent.MEDIUM_BUFFER_SIZE),
                Optional.ofNullable(this.concurrency).orElse(SchedulerGroup.DEFAULT_POOL_SIZE),
                Optional.ofNullable(this.autoShutdown).orElse(true));
    }

}
