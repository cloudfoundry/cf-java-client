/*
 * Copyright 2013-2021 the original author or authors.
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

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import org.immutables.value.Value;

import java.util.ArrayList;
import java.util.List;

@JsonDeserialize
@Value.Immutable
abstract class _ProcessStatisticsResource extends ProcessStatistics {

    @Value.Check
    protected void check() {
        if (getState() == ProcessState.STARTING || getState() == ProcessState.RUNNING || getState() == ProcessState.CRASHED) {
            List<String> missing = new ArrayList<>();

            if (getFileDescriptorQuota() == null) {
                missing.add("fileDescriptorQuota");
            }
            if (getHost() == null) {
                missing.add("host");
            }

            if (!missing.isEmpty()) {
                throw new IllegalStateException("Cannot build ProcessStatisticsResource, some of required attributes are not set " + missing);
            }
        }
    }

}
