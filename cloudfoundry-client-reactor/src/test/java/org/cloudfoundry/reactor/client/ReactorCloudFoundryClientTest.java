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

package org.cloudfoundry.reactor.client;

import static org.assertj.core.api.Assertions.assertThat;

import org.cloudfoundry.reactor.AbstractRestTest;
import org.junit.jupiter.api.Test;


final class ReactorCloudFoundryClientTest extends AbstractRestTest {

    private final ReactorCloudFoundryClient client =
            ReactorCloudFoundryClient.builder()
                    .connectionContext(CONNECTION_CONTEXT)
                    .rootV2(this.root)
                    .rootV3(this.root)
                    .tokenProvider(TOKEN_PROVIDER)
                    .build();

    @Test
    void applicationUsageEvents() {
        assertThat(this.client.applicationUsageEvents()).isNotNull();
    }

    @Test
    void applicationsV2() {
        assertThat(this.client.applicationsV2()).isNotNull();
    }

    @Test
    void applicationsV3() {
        assertThat(this.client.applicationsV3()).isNotNull();
    }

    @Test
    void buildpacks() {
        assertThat(this.client.buildpacks()).isNotNull();
    }

    @Test
    void builds() {
        assertThat(this.client.builds()).isNotNull();
    }

    @Test
    void domains() {
        assertThat(this.client.domains()).isNotNull();
    }

    @Test
    void domainsV3() {
        assertThat(this.client.domainsV3()).isNotNull();
    }

    @Test
    void droplets() {
        assertThat(this.client.droplets()).isNotNull();
    }

    @Test
    void environmentVariableGroups() {
        assertThat(this.client.environmentVariableGroups()).isNotNull();
    }

    @Test
    void events() {
        assertThat(this.client.events()).isNotNull();
    }

    @Test
    void featureFlags() {
        assertThat(this.client.featureFlags()).isNotNull();
    }

    @Test
    void info() {
        assertThat(this.client.info()).isNotNull();
    }

    @Test
    void isolationSegments() {
        assertThat(this.client.isolationSegments()).isNotNull();
    }

    @Test
    void jobs() {
        assertThat(this.client.jobs()).isNotNull();
    }

    @Test
    void jobsV3() {
        assertThat(this.client.jobsV3()).isNotNull();
    }

    @Test
    void organizationQuotaDefinitions() {
        assertThat(this.client.organizationQuotaDefinitions()).isNotNull();
    }

    @Test
    void organizations() {
        assertThat(this.client.organizations()).isNotNull();
    }

    @Test
    void organizationsV3() {
        assertThat(this.client.organizationsV3()).isNotNull();
    }

    @Test
    void packages() {
        assertThat(this.client.packages()).isNotNull();
    }

    @Test
    void privateDomains() {
        assertThat(this.client.privateDomains()).isNotNull();
    }

    @Test
    void processes() {
        assertThat(this.client.processes()).isNotNull();
    }

    @Test
    void resourceMatch() {
        assertThat(this.client.resourceMatch()).isNotNull();
    }

    @Test
    void rolesV3() {
        assertThat(this.client.rolesV3()).isNotNull();
    }

    @Test
    void routeMappings() {
        assertThat(this.client.routeMappings()).isNotNull();
    }

    @Test
    void routes() {
        assertThat(this.client.routes()).isNotNull();
    }

    @Test
    void runningSecurityGroups() {
        assertThat(this.client.securityGroups()).isNotNull();
    }

    @Test
    void serviceBindingsV2() {
        assertThat(this.client.serviceBindingsV2()).isNotNull();
    }

    @Test
    void serviceBindingsV3() {
        assertThat(this.client.serviceBindingsV3()).isNotNull();
    }

    @Test
    void serviceBrokers() {
        assertThat(this.client.serviceBrokers()).isNotNull();
    }

    @Test
    void serviceInstances() {
        assertThat(this.client.serviceInstances()).isNotNull();
    }

    @Test
    void serviceKeys() {
        assertThat(this.client.serviceKeys()).isNotNull();
    }

    @Test
    void servicePlanVisibilities() {
        assertThat(this.client.servicePlanVisibilities()).isNotNull();
    }

    @Test
    void servicePlans() {
        assertThat(this.client.servicePlans()).isNotNull();
    }

    @Test
    void serviceUsageEvents() {
        assertThat(this.client.serviceUsageEvents()).isNotNull();
    }

    @Test
    void services() {
        assertThat(this.client.services()).isNotNull();
    }

    @Test
    void sharedDomains() {
        assertThat(this.client.sharedDomains()).isNotNull();
    }

    @Test
    void spaceQuotaDefinitions() {
        assertThat(this.client.spaceQuotaDefinitions()).isNotNull();
    }

    @Test
    void spaces() {
        assertThat(this.client.spaces()).isNotNull();
    }

    @Test
    void spacesV3() {
        assertThat(this.client.spacesV3()).isNotNull();
    }

    @Test
    void stacks() {
        assertThat(this.client.stacks()).isNotNull();
    }

    @Test
    void tasks() {
        assertThat(this.client.tasks()).isNotNull();
    }

    @Test
    void userProvidedServiceInstances() {
        assertThat(this.client.userProvidedServiceInstances()).isNotNull();
    }

    @Test
    void users() {
        assertThat(this.client.users()).isNotNull();
    }
}
