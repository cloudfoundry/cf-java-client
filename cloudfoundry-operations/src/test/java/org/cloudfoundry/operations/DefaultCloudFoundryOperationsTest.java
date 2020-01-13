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

package org.cloudfoundry.operations;

import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;

public final class DefaultCloudFoundryOperationsTest extends AbstractOperationsTest {

    private final DefaultCloudFoundryOperations operations = DefaultCloudFoundryOperations.builder()
        .cloudFoundryClient(this.cloudFoundryClient)
        .dopplerClient(this.dopplerClient)
        .organization(TEST_ORGANIZATION_NAME)
        .space(TEST_SPACE_NAME)
        .uaaClient(this.uaaClient)
        .build();

    @Test
    public void advanced() {
        assertThat(this.operations.advanced()).isNotNull();
    }

    @Test
    public void applications() {
        assertThat(this.operations.applications()).isNotNull();
    }

    @Test
    public void buildpacks() {
        assertThat(this.operations.buildpacks()).isNotNull();
    }

    @Test
    public void domains() {
        assertThat(this.operations.domains()).isNotNull();
    }

    @Test
    public void networkPolicies() {
        assertThat(this.operations.networkPolicies()).isNotNull();
    }

    @Test
    public void organizationAdmin() {
        assertThat(this.operations.organizationAdmin()).isNotNull();
    }

    @Test
    public void organizations() {
        assertThat(this.operations.organizations()).isNotNull();
    }

    @Test
    public void routes() {
        assertThat(this.operations.routes()).isNotNull();
    }

    @Test
    public void serviceAdmin() {
        assertThat(this.operations.serviceAdmin()).isNotNull();
    }

    @Test
    public void services() {
        assertThat(this.operations.services()).isNotNull();
    }

    @Test
    public void spaceAdmin() {
        assertThat(this.operations.spaceAdmin()).isNotNull();
    }

    @Test
    public void spaces() {
        assertThat(this.operations.spaces()).isNotNull();
    }

    @Test
    public void stacks() {
        assertThat(this.operations.stacks()).isNotNull();
    }

    @Test
    public void userAdmin() {
        assertThat(this.operations.userAdmin()).isNotNull();
    }

}
