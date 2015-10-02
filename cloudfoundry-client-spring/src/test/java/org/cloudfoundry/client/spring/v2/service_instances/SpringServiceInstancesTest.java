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

package org.cloudfoundry.client.spring.v2.service_instances;

import org.cloudfoundry.client.spring.AbstractRestTest;
import org.cloudfoundry.client.v2.CloudFoundryException;
import org.cloudfoundry.client.v2.Resource;
import org.cloudfoundry.client.v2.serviceinstances.ListServiceInstancesRequest;
import org.cloudfoundry.client.v2.serviceinstances.ListServiceInstancesResponse;
import org.cloudfoundry.client.v2.serviceinstances.ListServiceInstancesResponse.ListServiceInstancesResponseEntity;
import org.junit.Test;
import org.springframework.core.io.ClassPathResource;
import reactor.rx.Streams;

import java.util.Arrays;
import java.util.Collections;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.springframework.http.HttpStatus.OK;
import static org.springframework.http.HttpStatus.UNPROCESSABLE_ENTITY;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.requestTo;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withStatus;

public final class SpringServiceInstancesTest extends AbstractRestTest {

    private final SpringServiceInstances serviceInstances = new SpringServiceInstances(this.restTemplate, this.root);

    @Test
    public void list() {
        this.mockServer
                .expect(requestTo("https://api.run.pivotal.io/v2/service_instances?q=name%20IN%20test-name&page=-1"))
                .andRespond(withStatus(OK)
                        .body(new ClassPathResource("v2/service_instances/GET_response.json"))
                        .contentType(APPLICATION_JSON));

        ListServiceInstancesRequest request = new ListServiceInstancesRequest()
                .withName("test-name")
                .withPage(-1);

        ListServiceInstancesResponse response = Streams.wrap(this.serviceInstances.list(request)).next()
                .get();

        assertNull(response.getNextUrl());
        assertNull(response.getPreviousUrl());
        assertEquals(Integer.valueOf(1), response.getTotalPages());
        assertEquals(Integer.valueOf(1), response.getTotalResults());

        assertEquals(1, response.getResources().size());
        ListServiceInstancesResponse.ListServiceInstancesResponseResource resource = response.getResources().get(0);

        Resource.Metadata metadata = resource.getMetadata();
        assertEquals("2015-07-27T22:43:08Z", metadata.getCreatedAt());
        assertEquals("24ec15f9-f6c7-434a-8893-51baab8408d8", metadata.getId());
        assertNull(metadata.getUpdatedAt());
        assertEquals("/v2/service_instances/24ec15f9-f6c7-434a-8893-51baab8408d8", metadata.getUrl());

        ListServiceInstancesResponse.ListServiceInstancesResponseEntity entity = resource.getEntity();

        assertEquals(Collections.singletonMap("creds-key-72", "creds-val-72"), entity.getCredentials());
        assertNull(entity.getDashboardUrl());

        ListServiceInstancesResponseEntity.LastOperation lastOperation = entity.getLastOperation();
        assertEquals("2015-07-27T22:43:08Z", lastOperation.getCreatedAt());
        assertEquals("service broker-provided description", lastOperation.getDescription());
        assertEquals("succeeded", lastOperation.getState());
        assertEquals("create", lastOperation.getType());
        assertEquals("2015-07-27T22:43:08Z", lastOperation.getUpdatedAt());

        assertEquals("name-133", entity.getName());
        assertEquals("/v2/service_instances/24ec15f9-f6c7-434a-8893-51baab8408d8/service_bindings",
                entity.getServiceBindingsUrl());
        assertEquals("/v2/service_instances/24ec15f9-f6c7-434a-8893-51baab8408d8/service_keys",
                entity.getServiceKeysUrl());
        assertEquals("2b53255a-8b40-4671-803d-21d3f5d4183a", entity.getServicePlanId());
        assertEquals("/v2/service_plans/2b53255a-8b40-4671-803d-21d3f5d4183a", entity.getServicePlanUrl());
        assertEquals("83b3e705-49fd-4c40-8adf-f5e34f622a19", entity.getSpaceId());
        assertEquals("/v2/spaces/83b3e705-49fd-4c40-8adf-f5e34f622a19", entity.getSpaceUrl());
        assertEquals(Arrays.asList("accounting", "mongodb"), entity.getTags());
        assertEquals("managed_service_instance", entity.getType());
        this.mockServer.verify();
    }

    @Test(expected = CloudFoundryException.class)
    public void listError() {
        this.mockServer
                .expect(requestTo("https://api.run.pivotal.io/v2/service_instances?q=name%20IN%20test-name&page=-1"))
                .andRespond(withStatus(UNPROCESSABLE_ENTITY)
                        .body(new ClassPathResource("v2/error_response.json"))
                        .contentType(APPLICATION_JSON));

        ListServiceInstancesRequest request = new ListServiceInstancesRequest()
                .withName("test-name")
                .withPage(-1);

        Streams.wrap(this.serviceInstances.list(request)).next().get();
    }

}
