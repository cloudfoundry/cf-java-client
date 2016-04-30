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

package org.cloudfoundry.spring.client;

import org.cloudfoundry.spring.AbstractRestTest;
import org.cloudfoundry.spring.util.network.ConnectionContext;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertSame;

public final class SpringCloudFoundryClientTest extends AbstractRestTest {

    private final SpringCloudFoundryClient client = new SpringCloudFoundryClient(ConnectionContext.builder().build(), "test-host", null, null, this.restTemplate, this.root, PROCESSOR_GROUP,
        this.tokenProvider);

    @Test
    public void applicationUsageEvents() {
        assertNotNull(this.client.applicationUsageEvents());
    }

    @Test
    public void applicationsV2() {
        assertNotNull(this.client.applicationsV2());
    }

    @Test
    public void applicationsV3() {
        assertNotNull(this.client.applicationsV3());
    }

    @Test
    public void buildpacks() {
        assertNotNull(this.client.buildpacks());
    }

    @Test
    public void domains() {
        assertNotNull(this.client.domains());
    }

    @Test
    public void droplets() {
        assertNotNull(this.client.droplets());
    }

    @Test
    public void environmentVariableGroups() {
        assertNotNull(this.client.environmentVariableGroups());
    }

    @Test
    public void events() {
        assertNotNull(this.client.events());
    }

    @Test
    public void featureFlags() {
        assertNotNull(this.client.featureFlags());
    }

    @Test
    public void getAccessToken() {
        assertEquals(this.tokenProvider.getToken(), this.client.getAccessToken());
    }

    @Test
    public void getConnectionContext() {
        assertSame(this.client, this.client.getConnectionContext().getCloudFoundryClient());
    }

    @Test
    public void info() {
        assertNotNull(this.client.info());
    }

    @Test
    public void jobs() {
        assertNotNull(this.client.jobs());
    }

    @Test
    public void organizationQuotaDefinitions() {
        assertNotNull(this.client.organizationQuotaDefinitions());
    }

    @Test
    public void organizations() {
        assertNotNull(this.client.organizations());
    }

    @Test
    public void packages() {
        assertNotNull(this.client.packages());
    }

    @Test
    public void privateDomains() {
        assertNotNull(this.client.privateDomains());
    }

    @Test
    public void processes() {
        assertNotNull(this.client.processes());
    }

    @Test
    public void routeMappings() {
        assertNotNull(this.client.routeMappings());
    }

    @Test
    public void routes() {
        assertNotNull(this.client.routes());
    }

    @Test
    public void runningSecurityGroups() {
        assertNotNull(this.client.securityGroups());
    }

    @Test
    public void serviceBindings() {
        assertNotNull(this.client.serviceBindings());
    }

    @Test
    public void serviceBrokers() {
        assertNotNull(this.client.serviceBrokers());
    }

    @Test
    public void serviceInstances() {
        assertNotNull(this.client.serviceInstances());
    }

    @Test
    public void serviceKeys() {
        assertNotNull(this.client.serviceKeys());
    }

    @Test
    public void servicePlanVisibilities() {
        assertNotNull(this.client.servicePlanVisibilities());
    }

    @Test
    public void servicePlans() {
        assertNotNull(this.client.servicePlans());
    }

    @Test
    public void serviceUsageEvents() {
        assertNotNull(this.client.serviceUsageEvents());
    }

    @Test
    public void services() {
        assertNotNull(this.client.services());
    }

    @Test
    public void sharedDomains() {
        assertNotNull(this.client.sharedDomains());
    }

    @Test
    public void space() {
        assertNotNull(this.client.spaces());
    }

    @Test
    public void spaceQuotaDefinitions() {
        assertNotNull(this.client.spaceQuotaDefinitions());
    }

    @Test
    public void stacks() {
        assertNotNull(this.client.stacks());
    }

    @Test
    public void tasks() {
        assertNotNull(this.client.tasks());
    }

    @Test
    public void userProvidedServiceInstances() {
        assertNotNull(this.client.userProvidedServiceInstances());
    }

    @Test
    public void users() {
        assertNotNull(this.client.users());
    }

}
