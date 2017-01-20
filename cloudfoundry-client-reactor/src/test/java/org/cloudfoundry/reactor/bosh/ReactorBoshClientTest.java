/*
 * Copyright 2013-2017 the original author or authors.
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

package org.cloudfoundry.reactor.bosh;

import org.cloudfoundry.reactor.AbstractRestTest;
import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;

public final class ReactorBoshClientTest extends AbstractRestTest {

    private final ReactorBoshClient client = ReactorBoshClient.builder()
        .connectionContext(CONNECTION_CONTEXT)
        .root(this.root)
        .tokenProvider(TOKEN_PROVIDER)
        .build();

    @Test
    public void deployments() {
        assertThat(this.client.deployments()).isNotNull();
    }

    @Test
    public void info() {
        assertThat(this.client.info()).isNotNull();
    }

    @Test
    public void releases() {
        assertThat(this.client.releases()).isNotNull();
    }

    @Test
    public void stemcells() {
        assertThat(this.client.stemcells()).isNotNull();
    }

    @Test
    public void tasks() {
        assertThat(this.client.tasks()).isNotNull();
    }

}
