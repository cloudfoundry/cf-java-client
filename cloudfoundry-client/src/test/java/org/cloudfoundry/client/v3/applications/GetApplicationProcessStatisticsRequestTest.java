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

package org.cloudfoundry.client.v3.applications;

import org.cloudfoundry.client.v3.processes.ProcessState;
import org.cloudfoundry.client.v3.processes.ProcessStatisticsResource;
import org.cloudfoundry.client.v3.processes.ProcessUsage;
import org.junit.Test;

public final class GetApplicationProcessStatisticsRequestTest {

    @Test(expected = IllegalStateException.class)
    public void noApplicationId() {
        GetApplicationProcessStatisticsRequest.builder()
            .type("test-type")
            .build();
    }

    @Test(expected = IllegalStateException.class)
    public void noType() {
        GetApplicationProcessStatisticsRequest.builder()
            .applicationId("test-id")
            .build();
    }

    @Test
    public void valid() {
        GetApplicationProcessStatisticsRequest.builder()
            .applicationId("test-id")
            .type("test-type")
            .build();
    }

    @Test
    public void validDownResource() {
        ProcessStatisticsResource processStatisticsResource = ProcessStatisticsResource.builder()
            .type("web")
            .index(0)
            .state(ProcessState.DOWN)
            .uptime(0L)
            .build();
        GetApplicationProcessStatisticsResponse.builder()
            .resource(processStatisticsResource)
            .build();
    }

    @Test(expected = IllegalStateException.class)
    public void invalidRunningResource() {
        ProcessStatisticsResource processStatisticsResource = ProcessStatisticsResource.builder()
            .type("web")
            .index(0)
            .state(ProcessState.RUNNING)
            .uptime(0L)
            .build();
        GetApplicationProcessStatisticsResponse.builder()
            .resource(processStatisticsResource)
            .build();
    }

    @Test
    public void validRunningResponse() {
        ProcessUsage processUsage = ProcessUsage.builder()
            .time("")
            .cpu(new Double("0.00038711029163348665"))
            .memory(19177472L)
            .disk(69705728L)
            .build();
        ProcessStatisticsResource processStatisticsResource = ProcessStatisticsResource.builder()
            .type("web")
            .index(0)
            .state(ProcessState.RUNNING)
            .host("10.244.16.10")
            .usage(processUsage)
            .uptime(9042L)
            .memoryQuota(268435456L)
            .diskQuota(1073741824L)
            .fileDescriptorQuota(16384L)
            .build();
        GetApplicationProcessStatisticsResponse.builder()
            .resource(processStatisticsResource)
            .build();
    }
}
