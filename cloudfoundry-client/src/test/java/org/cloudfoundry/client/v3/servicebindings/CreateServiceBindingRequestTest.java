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
import org.cloudfoundry.client.v3.servicebindings.CreateServiceBindingRequest.Data;
import org.cloudfoundry.client.v3.servicebindings.CreateServiceBindingRequest.Relationships;
import org.cloudfoundry.client.v3.servicebindings.CreateServiceBindingRequest.ServiceBindingType;
import org.junit.Test;

import static org.cloudfoundry.ValidationResult.Status.INVALID;
import static org.cloudfoundry.ValidationResult.Status.VALID;
import static org.junit.Assert.assertEquals;

public final class CreateServiceBindingRequestTest {

    @Test
    public void dataIsValid() {
        ValidationResult result = Data.builder()
            .build()
            .isValid();

        assertEquals(VALID, result.getStatus());
    }

    @Test
    public void relationshipsIsInvalidNoApplication() {
        ValidationResult result = Relationships.builder()
            .serviceInstance(Relationship.builder()
                .id("test-service-instance-id")
                .build())
            .build()
            .isValid();

        assertEquals(INVALID, result.getStatus());
        assertEquals("application relationship must be specified", result.getMessages().get(0));
    }

    @Test
    public void relationshipsIsInvalidNoServiceInstance() {
        ValidationResult result = Relationships.builder()
            .application(Relationship.builder()
                .id("test-application-id")
                .build())
            .build()
            .isValid();

        assertEquals(INVALID, result.getStatus());
        assertEquals("service instance relationship must be specified", result.getMessages().get(0));
    }

    @Test
    public void relationshipsIsValid() {
        ValidationResult result = Relationships.builder()
            .application(Relationship.builder()
                .id("test-application-id")
                .build())
            .serviceInstance(Relationship.builder()
                .id("test-service-instance-id")
                .build())
            .build()
            .isValid();

        assertEquals(VALID, result.getStatus());
    }

    @Test
    public void requestIsNotValidNoRelationships() {
        ValidationResult result = CreateServiceBindingRequest.builder()
            .type(ServiceBindingType.APP)
            .build()
            .isValid();

        assertEquals(INVALID, result.getStatus());
        assertEquals("relationships must be specified", result.getMessages().get(0));
    }

    @Test
    public void requestIsNotValidNoType() {
        ValidationResult result = CreateServiceBindingRequest.builder()
            .relationships(Relationships.builder()
                .application(Relationship.builder()
                    .id("test-application-id")
                    .build())
                .serviceInstance(Relationship.builder()
                    .id("test-service-instance-id")
                    .build())
                .build())
            .build()
            .isValid();

        assertEquals(INVALID, result.getStatus());
        assertEquals("type must be specified", result.getMessages().get(0));
    }

    @Test
    public void requestIsValid() {
        ValidationResult result = CreateServiceBindingRequest.builder()
            .relationships(Relationships.builder()
                .application(Relationship.builder()
                    .id("test-application-id")
                    .build())
                .serviceInstance(Relationship.builder()
                    .id("test-service-instance-id")
                    .build())
                .build())
            .type(ServiceBindingType.APP)
            .build()
            .isValid();

        assertEquals(VALID, result.getStatus());
    }

}
