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

import static org.junit.Assert.assertNotNull;

public final class DefaultCloudFoundryOperationsTest extends AbstractOperationsTest {

    private final DefaultCloudFoundryOperations operations = new DefaultCloudFoundryOperations(this.cloudFoundryClient, MISSING_ID, MISSING_ID);

    @Test
    public void applications() {
        assertNotNull(this.operations.applications());
    }

    @Test
    public void organizations() {
        assertNotNull(this.operations.organizations());
    }

    @Test
    public void routes() {
        assertNotNull(this.operations.routes());
    }

    @Test
    public void spaces() {
        assertNotNull(this.operations.spaces());
    }

}
