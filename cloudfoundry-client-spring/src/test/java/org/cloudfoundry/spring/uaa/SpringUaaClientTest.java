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

package org.cloudfoundry.spring.uaa;

import org.cloudfoundry.spring.AbstractRestTest;
import org.junit.Test;

import static org.junit.Assert.assertNotNull;

public final class SpringUaaClientTest extends AbstractRestTest {

    private final SpringUaaClient client = new SpringUaaClient(this.restTemplate, this.root, PROCESSOR_GROUP);

    @Test
    public void accessTokenAdministration() {
        assertNotNull(this.client.accessTokenAdministration());
    }

    @Test
    public void identityZones() {
        assertNotNull(this.client.identityZones());
    }

}
