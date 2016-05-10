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

package org.cloudfoundry.client.v3.servicebindings;


import org.cloudfoundry.ValidationResult;
import org.cloudfoundry.client.v3.Relationship;
import org.junit.Test;

import java.util.Collections;

import static org.cloudfoundry.ValidationResult.Status.INVALID;
import static org.cloudfoundry.ValidationResult.Status.VALID;
import static org.junit.Assert.assertEquals;

public final class CreateServiceBindingRequestTest {

    @Test
    public void isNotValidNoAppRelationship() {
        ValidationResult result = CreateServiceBindingRequest.builder()
            .type(CreateServiceBindingRequest.ServiceBindingType.APP)
            .relationships(CreateServiceBindingRequest.Relationships.builder()
                .serviceInstance(Relationship.builder().id("test-service-instance-id").build())
                .build())
            .build()
            .isValid();

        assertEquals(INVALID, result.getStatus());
        assertEquals("application relationship must be specified", result.getMessages().get(0));
    }

    @Test
    public void isNotValidNoServiceInstanceRelationship() {
        ValidationResult result = CreateServiceBindingRequest.builder()
            .type(CreateServiceBindingRequest.ServiceBindingType.APP)
            .relationships(CreateServiceBindingRequest.Relationships.builder()
                .application(Relationship.builder().id("test-application-id").build())
                .build())
            .build()
            .isValid();

        assertEquals(INVALID, result.getStatus());
        assertEquals("service instance relationship must be specified", result.getMessages().get(0));
    }

    @Test
    public void isNotValidNoType() {
        ValidationResult result = CreateServiceBindingRequest.builder()
            .relationships(CreateServiceBindingRequest.Relationships.builder()
                .application(Relationship.builder().id("test-application-id").build())
                .serviceInstance(Relationship.builder().id("test-service-instance-id").build())
                .build())
            .build()
            .isValid();

        assertEquals(INVALID, result.getStatus());
        assertEquals("type must be specified", result.getMessages().get(0));
    }

    @Test
    public void isNotValidEmptyData() {
        ValidationResult result = CreateServiceBindingRequest.builder()
            .type(CreateServiceBindingRequest.ServiceBindingType.APP)
            .relationships(CreateServiceBindingRequest.Relationships.builder()
                .application(Relationship.builder().id("test-application-id").build())
                .serviceInstance(Relationship.builder().id("test-service-instance-id").build())
                .build())
            .data(CreateServiceBindingRequest.Data.builder()
                .build())
            .build()
            .isValid();

        assertEquals(INVALID, result.getStatus());
        assertEquals("parameters cannot be empty if specified", result.getMessages().get(0));
    }

    @Test
    public void isNotValidNoRelationShips() {
        ValidationResult result = CreateServiceBindingRequest.builder()
            .type(CreateServiceBindingRequest.ServiceBindingType.APP)
            .build()
            .isValid();

        assertEquals(INVALID, result.getStatus());
        assertEquals("relationships must be specified", result.getMessages().get(0));
    }


    @Test
    public void isValid() {
        ValidationResult result = CreateServiceBindingRequest.builder()
            .type(CreateServiceBindingRequest.ServiceBindingType.APP)
            .relationships(CreateServiceBindingRequest.Relationships.builder()
                .application(Relationship.builder().id("test-application-id").build())
                .serviceInstance(Relationship.builder().id("test-service-instance-id").build())
                .build())
            .data(CreateServiceBindingRequest.Data.builder()
                .parameters(Collections.singletonMap("test-key", "test-value"))
                .build())
            .build()
            .isValid();

        assertEquals(VALID, result.getStatus());
    }

}
