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

package org.cloudfoundry.gradle;

import org.junit.Test;

import static org.junit.Assert.*;

public class CfPropertiesTest {

    @Test
    public void testMandatoryFields() {
        CfProperties.builder()
            .ccHost("cchost")
            .ccUser("ccuser")
            .ccPassword("ccpassword")
            .org("org")
            .space("space")
            .name("name")
            .build();
    }

    @Test(expected = IllegalStateException.class)
    public void testNotSettingMandatoryFields() {
        CfProperties.builder()
            .ccHost("ccHost")
            .build();
    }

    @Test
    public void testChangeName() {
        CfProperties cfAppProperties = CfProperties.builder()
            .ccHost("cchost")
            .ccUser("ccuser")
            .ccPassword("ccpassword")
            .org("org")
            .space("space")
            .name("name")
            .build();

        CfProperties updatedProps = cfAppProperties.withName("newName");
        assertEquals(updatedProps.name(), "newName");

    }
}
