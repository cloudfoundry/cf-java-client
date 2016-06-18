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

package org.cloudfoundry.reactor.uaa;

import org.cloudfoundry.reactor.AbstractRestTest;
import org.junit.Test;

import static org.junit.Assert.assertNotNull;

public final class ReactorUaaClientTest extends AbstractRestTest {

    private final ReactorUaaClient client = new ReactorUaaClient(AUTHORIZATION_PROVIDER, HTTP_CLIENT, OBJECT_MAPPER, this.root);

    @Test
    public void authorizations() {
        assertNotNull(this.client.authorizations());
    }

    @Test
    public void groups() {
        assertNotNull(this.client.groups());
    }

    @Test
    public void identityProviders() {
        assertNotNull(this.client.identityProviders());
    }

    @Test
    public void identityZones() {
        assertNotNull(this.client.identityZones());
    }

    @Test
    public void tokens() {
        assertNotNull(this.client.tokens());
    }

    @Test
    public void users() {
        assertNotNull(this.client.users());
    }

}
