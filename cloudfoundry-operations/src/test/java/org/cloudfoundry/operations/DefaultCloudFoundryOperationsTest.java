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

import org.junit.Test;
import reactor.core.publisher.Mono;

import static org.junit.Assert.assertNotNull;

public final class DefaultCloudFoundryOperationsTest extends AbstractOperationsTest {

    private final DefaultCloudFoundryOperations operations = new DefaultCloudFoundryOperations(this.cloudFoundryClient, Mono.just(this.loggingClient), MISSING_ID, MISSING_ID,
        Mono.just(TEST_USERNAME));

    @Test
    public void applications() {
        assertNotNull(this.operations.applications());
    }

    @Test
    public void buildpacks() {
        assertNotNull(this.operations.buildpacks());
    }

    @Test
    public void domains() {
        assertNotNull(this.operations.domains());
    }

    @Test
    public void organizations() {
        assertNotNull(this.operations.organizations());
    }

    @Test
    public void quotas() {
        assertNotNull(this.operations.quotas());
    }
    
    @Test
    public void routes() {
        assertNotNull(this.operations.routes());
    }

    @Test
    public void services() {
        assertNotNull(this.operations.services());
    }

    @Test
    public void spaceQuotas() {
        assertNotNull(this.operations.spaceQuotas());
    }

    @Test
    public void spaces() {
        assertNotNull(this.operations.spaces());
    }

    @Test
    public void stacks() {
        assertNotNull(this.operations.stacks());
    }

}
