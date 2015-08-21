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

package org.cloudfoundry.client.spring.v3.droplets;

import org.cloudfoundry.client.spring.AbstractRestTest;
import org.cloudfoundry.client.spring.ExpectedExceptionSubscriber;
import org.cloudfoundry.client.v3.Hash;
import org.cloudfoundry.client.v3.droplets.GetDropletRequest;
import org.junit.Test;
import org.springframework.core.io.ClassPathResource;

import java.util.Collections;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.springframework.http.HttpStatus.OK;
import static org.springframework.http.HttpStatus.UNPROCESSABLE_ENTITY;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.requestTo;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withStatus;

public final class SpringDropletsTest extends AbstractRestTest {

    private final SpringDroplets droplets = new SpringDroplets(this.restTemplate, this.root);

    @Test
    public void get() {
        this.mockServer
                .expect(requestTo("https://api.run.pivotal.io/v3/droplets/test-id"))
                .andRespond(withStatus(OK)
                        .body(new ClassPathResource("v3/droplets/GET_{id}_response.json"))
                        .contentType(APPLICATION_JSON));

        GetDropletRequest request = new GetDropletRequest()
                .withId("test-id");

        this.droplets.get(request).subscribe(response -> {
            assertEquals("http://buildpack.git.url.com", response.getBuildpack());
            assertEquals("2015-07-27T22:43:30Z", response.getCreatedAt());
            assertEquals(Collections.singletonMap("cloud", "foundry"), response.getEnvironmentVariables());
            assertEquals("example error", response.getError());

            Hash hash = response.getHash();
            assertEquals("sha1", hash.getType());
            assertNull(hash.getValue());

            assertEquals("guid-4dc396dd-9fe3-4b96-847e-d0c63768d5f9", response.getId());

            assertEquals(4, response.getLinks().size());
            assertNotNull(response.getLink("self"));
            assertNotNull(response.getLink("package"));
            assertNotNull(response.getLink("app"));
            assertNotNull(response.getLink("assign_current_droplet"));

            assertNull(response.getProcfile());
            assertEquals("STAGED", response.getState());
            assertNull(response.getUpdatedAt());
            this.mockServer.verify();
        });
    }

    @Test
    public void getError() {
        this.mockServer
                .expect(requestTo("https://api.run.pivotal.io/v3/droplets/test-id"))
                .andRespond(withStatus(UNPROCESSABLE_ENTITY)
                        .body(new ClassPathResource("v2/error_response.json"))
                        .contentType(APPLICATION_JSON));

        GetDropletRequest request = new GetDropletRequest()
                .withId("test-id");

        this.droplets.get(request).subscribe(new ExpectedExceptionSubscriber());
    }

    @Test
    public void getInvalidRequest() {
        this.droplets.get(new GetDropletRequest()).subscribe(new ExpectedExceptionSubscriber());
    }
}
