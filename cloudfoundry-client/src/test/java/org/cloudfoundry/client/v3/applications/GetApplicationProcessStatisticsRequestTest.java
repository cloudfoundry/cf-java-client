/*
 * Copyright 2013-2020 the original author or authors.
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
    public void validDownResponse() {
    	ProcessStatisticsResource processStatisticsResource = ProcessStatisticsResource.builder()
    			.type("web")
    			.index(0)
    			.state(ProcessState.DOWN)
    			.uptime(new Long(0))
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
    			.memory(new Long(19177472))
    			.disk(new Integer(69705728))
    			.build();
    	ProcessStatisticsResource processStatisticsResource = ProcessStatisticsResource.builder()
    			.type("web")
    			.index(0)
    			.state(ProcessState.RUNNING)
    			.host("10.244.16.10")
    			.usage(processUsage)
    			.uptime(new Long(9042))
    			.memoryQuota(new Long(268435456))
    			.diskQuota(new Long(1073741824))
    			.fileDescriptorQuota(new Long(16384))
    			.build();
        GetApplicationProcessStatisticsResponse.builder()
        	.resource(processStatisticsResource)
            .build();
    }
}
