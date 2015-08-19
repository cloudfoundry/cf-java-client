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

package org.cloudfoundry.client.v3.applications;

import org.cloudfoundry.client.ValidationResult;
import org.cloudfoundry.client.v3.Link;
import org.cloudfoundry.client.v3.LinkBased;
import org.cloudfoundry.client.v3.StubLinkBased;
import org.junit.Test;

import static org.cloudfoundry.client.ValidationResult.Status.INVALID;
import static org.cloudfoundry.client.ValidationResult.Status.VALID;
import static org.junit.Assert.assertEquals;

public final class StartApplicationRequestTest {

    private final Link link = new Link();

    private final LinkBased linkBased = new StubLinkBased("start", this.link);

    @Test
    public void test() {
        StartApplicationRequest request = new StartApplicationRequest()
                .withId("test-id")
                .withLink(this.linkBased);

        assertEquals("test-id", request.getId());
        assertEquals(this.link, request.getLink());
    }

    @Test
    public void isValidId() {
        ValidationResult result = new StartApplicationRequest()
                .withId("test-id")
                .isValid();

        assertEquals(VALID, result.getStatus());
    }

    @Test
    public void isValidLink() {
        ValidationResult result = new StartApplicationRequest()
                .withLink(this.linkBased)
                .isValid();

        assertEquals(VALID, result.getStatus());
    }

    @Test
    public void isValidIdAndLink() {
        ValidationResult result = new StartApplicationRequest()
                .withId("test-id")
                .withLink(this.linkBased)
                .isValid();

        assertEquals(INVALID, result.getStatus());
        assertEquals("exactly one of id or link must be specified", result.getMessages().get(0));
    }

    @Test
    public void isValidNoIdNoLink() {
        ValidationResult result = new StartApplicationRequest()
                .isValid();

        assertEquals(INVALID, result.getStatus());
        assertEquals("exactly one of id or link must be specified", result.getMessages().get(0));
    }

}
