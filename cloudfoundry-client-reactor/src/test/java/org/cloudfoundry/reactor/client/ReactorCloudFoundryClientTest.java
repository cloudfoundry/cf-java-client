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

package org.cloudfoundry.reactor.client;

import org.cloudfoundry.reactor.AbstractRestTest;
import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;

public final class ReactorCloudFoundryClientTest extends AbstractRestTest {

    private final ReactorCloudFoundryClient client = ReactorCloudFoundryClient.builder()
        .connectionContext(CONNECTION_CONTEXT)
        .rootV2(this.root)
        .rootV3(this.root)
        .tokenProvider(TOKEN_PROVIDER)
        .build();

    @Test
    public void applicationUsageEvents() {
        assertThat(this.client.applicationUsageEvents()).isNotNull();
    }

    @Test
    public void applicationsV2() {
        assertThat(this.client.applicationsV2()).isNotNull();
    }

    @Test
    public void applicationsV3() {
        assertThat(this.client.applicationsV3()).isNotNull();
    }

    @Test
    public void buildpacks() {
        assertThat(this.client.buildpacks()).isNotNull();
    }

    @Test
    public void builds() {
        assertThat(this.client.builds()).isNotNull();
    }

    @Test
    public void domains() {
        assertThat(this.client.domains()).isNotNull();
    }

    @Test
    public void domainsV3() {
        assertThat(this.client.domainsV3()).isNotNull();
    }

    @Test
    public void droplets() {
        assertThat(this.client.droplets()).isNotNull();
    }

    @Test
    public void environmentVariableGroups() {
        assertThat(this.client.environmentVariableGroups()).isNotNull();
    }

    @Test
    public void events() {
        assertThat(this.client.events()).isNotNull();
    }

    @Test
    public void featureFlags() {
        assertThat(this.client.featureFlags()).isNotNull();
    }

    @Test
    public void info() {
        assertThat(this.client.info()).isNotNull();
    }

    @Test
    public void isolationSegments() {
        assertThat(this.client.isolationSegments()).isNotNull();
    }

    @Test
    public void jobs() {
        assertThat(this.client.jobs()).isNotNull();
    }

    @Test
    public void jobsV3() {
        assertThat(this.client.jobsV3()).isNotNull();
    }

    @Test
    public void organizationQuotaDefinitions() {
        assertThat(this.client.organizationQuotaDefinitions()).isNotNull();
    }

    @Test
    public void organizations() {
        assertThat(this.client.organizations()).isNotNull();
    }

    @Test
    public void organizationsV3() {
        assertThat(this.client.organizationsV3()).isNotNull();
    }

    @Test
    public void packages() {
        assertThat(this.client.packages()).isNotNull();
    }

    @Test
    public void privateDomains() {
        assertThat(this.client.privateDomains()).isNotNull();
    }

    @Test
    public void processes() {
        assertThat(this.client.processes()).isNotNull();
    }

    @Test
    public void resourceMatch() {
        assertThat(this.client.resourceMatch()).isNotNull();
    }

    @Test
    public void routeMappings() {
        assertThat(this.client.routeMappings()).isNotNull();
    }

    @Test
    public void routes() {
        assertThat(this.client.routes()).isNotNull();
    }

    @Test
    public void runningSecurityGroups() {
        assertThat(this.client.securityGroups()).isNotNull();
    }

    @Test
    public void serviceBindingsV2() {
        assertThat(this.client.serviceBindingsV2()).isNotNull();
    }

    @Test
    public void serviceBindingsV3() {
        assertThat(this.client.serviceBindingsV3()).isNotNull();
    }

    @Test
    public void serviceBrokers() {
        assertThat(this.client.serviceBrokers()).isNotNull();
    }

    @Test
    public void serviceInstances() {
        assertThat(this.client.serviceInstances()).isNotNull();
    }

    @Test
    public void serviceKeys() {
        assertThat(this.client.serviceKeys()).isNotNull();
    }

    @Test
    public void servicePlanVisibilities() {
        assertThat(this.client.servicePlanVisibilities()).isNotNull();
    }

    @Test
    public void servicePlans() {
        assertThat(this.client.servicePlans()).isNotNull();
    }

    @Test
    public void serviceUsageEvents() {
        assertThat(this.client.serviceUsageEvents()).isNotNull();
    }

    @Test
    public void services() {
        assertThat(this.client.services()).isNotNull();
    }

    @Test
    public void sharedDomains() {
        assertThat(this.client.sharedDomains()).isNotNull();
    }

    @Test
    public void spaceQuotaDefinitions() {
        assertThat(this.client.spaceQuotaDefinitions()).isNotNull();
    }

    @Test
    public void spaces() {
        assertThat(this.client.spaces()).isNotNull();
    }

    @Test
    public void spacesV3() {
        assertThat(this.client.spacesV3()).isNotNull();
    }

    @Test
    public void stacks() {
        assertThat(this.client.stacks()).isNotNull();
    }

    @Test
    public void tasks() {
        assertThat(this.client.tasks()).isNotNull();
    }

    @Test
    public void userProvidedServiceInstances() {
        assertThat(this.client.userProvidedServiceInstances()).isNotNull();
    }

    @Test
    public void users() {
        assertThat(this.client.users()).isNotNull();
    }

}
