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

package org.cloudfoundry.reactor.uaa;

import org.cloudfoundry.reactor.AbstractRestTest;
import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;

public final class ReactorUaaClientTest extends AbstractRestTest {

    private final ReactorUaaClient client = ReactorUaaClient.builder()
        .connectionContext(CONNECTION_CONTEXT)
        .root(this.root)
        .tokenProvider(TOKEN_PROVIDER)
        .build();

    @Test
    public void authorizations() {
        assertThat(this.client.authorizations()).isNotNull();
    }

    @Test
    public void clients() {
        assertThat(this.client.clients()).isNotNull();
    }

    @Test
    public void groups() {
        assertThat(this.client.groups()).isNotNull();
    }

    @Test
    public void identityProviders() {
        assertThat(this.client.identityProviders()).isNotNull();
    }

    @Test
    public void identityZones() {
        assertThat(this.client.identityZones()).isNotNull();
    }

    @Test
    public void tokens() {
        assertThat(this.client.tokens()).isNotNull();
    }

    @Test
    public void users() {
        assertThat(this.client.users()).isNotNull();
    }

}
