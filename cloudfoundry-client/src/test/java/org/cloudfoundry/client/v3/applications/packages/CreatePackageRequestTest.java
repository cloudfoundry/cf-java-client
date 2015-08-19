/*
 * Copyright 2013-2015 the original author or authors.
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

package org.cloudfoundry.client.v3.applications.packages;

import org.cloudfoundry.client.ValidationResult;
import org.cloudfoundry.client.v3.Link;
import org.cloudfoundry.client.v3.LinkBased;
import org.cloudfoundry.client.v3.StubLinkBased;
import org.cloudfoundry.client.v3.applications.StartApplicationRequest;
import org.junit.Test;

import static org.cloudfoundry.client.ValidationResult.Status.INVALID;
import static org.cloudfoundry.client.ValidationResult.Status.VALID;
import static org.cloudfoundry.client.v3.applications.packages.CreatePackageRequest.PackageType.BITS;
import static org.cloudfoundry.client.v3.applications.packages.CreatePackageRequest.PackageType.DOCKER;
import static org.junit.Assert.assertEquals;

public final class CreatePackageRequestTest {

    private final Link link = new Link();

    private final LinkBased linkBased = new StubLinkBased("packages", this.link);

    @Test
    public void test() {
        CreatePackageRequest request = new CreatePackageRequest()
                .withApplicationId("test-application-id")
                .withLink(this.linkBased)
                .withType(DOCKER)
                .withUrl("test-url");

        assertEquals("test-application-id", request.getApplicationId());
        assertEquals(this.link, request.getLink());
        assertEquals(DOCKER, request.getType());
        assertEquals("test-url", request.getUrl());
    }

    @Test
    public void isValid() {
        ValidationResult result = new CreatePackageRequest()
                .withApplicationId("test-application-id")
                .withType(BITS)
                .isValid();

        assertEquals(VALID, result.getStatus());
    }

    @Test
    public void isValidNoType() {
        ValidationResult result = new CreatePackageRequest()
                .withApplicationId("test-application-id")
                .isValid();

        assertEquals(INVALID, result.getStatus());
        assertEquals("type must be specified", result.getMessages().get(0));
    }

    @Test
    public void isValidBitsAndUrl() {
        ValidationResult result = new CreatePackageRequest()
                .withApplicationId("test-application-id")
                .withType(BITS)
                .withUrl("test-url")
                .isValid();

        assertEquals(INVALID, result.getStatus());
        assertEquals("url must only be specified if type is DOCKER", result.getMessages().get(0));
    }

    @Test
    public void isValidDockerNoUrl() {
        ValidationResult result = new CreatePackageRequest()
                .withApplicationId("test-application-id")
                .withType(DOCKER)
                .isValid();

        assertEquals(INVALID, result.getStatus());
        assertEquals("url must be specified if type is DOCKER", result.getMessages().get(0));
    }

    @Test
    public void isValidLink() {
        ValidationResult result = new CreatePackageRequest()
                .withLink(this.linkBased)
                .withType(BITS)
                .isValid();

        assertEquals(VALID, result.getStatus());
    }

    @Test
    public void isValidApplicationIdAndLink() {
        ValidationResult result = new CreatePackageRequest()
                .withApplicationId("test-application-id")
                .withLink(this.linkBased)
                .withType(BITS)
                .isValid();

        assertEquals(INVALID, result.getStatus());
        assertEquals("exactly one of applicationId or link must be specified", result.getMessages().get(0));
    }

    @Test
    public void isValidNoIdNoLink() {
        ValidationResult result = new CreatePackageRequest()
                .withType(BITS)
                .isValid();

        assertEquals(INVALID, result.getStatus());
        assertEquals("exactly one of applicationId or link must be specified", result.getMessages().get(0));
    }

}
