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

package org.cloudfoundry.uaa.identityproviders;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.Test;

import java.io.IOException;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.*;

public final class ExternalGroupsDeserializerTest {

    @Test
    public void validArray() throws IOException {
        ObjectMapper objectMapper = new ObjectMapper();
        AttributeMappings attributeMappings = objectMapper.readValue("{\"external_groups\":     [  \"I am not that lonely\"   ,   \"yes I am here\" ] }".getBytes(), AttributeMappings.class);
        assertNotNull(attributeMappings);
        assertNotNull(attributeMappings.getExternalGroups());
        assertThat(attributeMappings.getExternalGroups().size(), is(equalTo(2)));
    }

    @Test
    public void validString() throws IOException {
        ObjectMapper objectMapper = new ObjectMapper();
        AttributeMappings attributeMappings = objectMapper.readValue("{\"external_groups\":       \"I am a lonesome string\"}".getBytes(), AttributeMappings.class);
        assertNotNull(attributeMappings);
        assertNotNull(attributeMappings.getExternalGroups());
        assertThat(attributeMappings.getExternalGroups().size(), is(equalTo(1)));
    }

    @Test(expected = JsonMappingException.class)
    public void noValidObject() throws IOException {
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.readValue("{\"external_groups\":     {} }".getBytes(), AttributeMappings.class);
    }



}