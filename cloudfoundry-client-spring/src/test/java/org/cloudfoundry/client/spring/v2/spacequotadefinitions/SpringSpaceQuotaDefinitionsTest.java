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

package org.cloudfoundry.client.spring.v2.spacequotadefinitions;

import org.cloudfoundry.client.spring.AbstractRestTest;
import org.cloudfoundry.client.v2.CloudFoundryException;
import org.cloudfoundry.client.v2.spacequotadefinitions.ListSpaceQuotaDefinitionsRequest;
import org.cloudfoundry.client.v2.spacequotadefinitions.ListSpaceQuotaDefinitionsResponse;
import org.cloudfoundry.client.v2.spacequotadefinitions.SpaceQuotaDefinitionEntity;
import org.cloudfoundry.client.v2.spacequotadefinitions.SpaceQuotaDefinitionResource;
import org.junit.Test;
import reactor.rx.Streams;

import static org.cloudfoundry.client.v2.Resource.Metadata;
import static org.junit.Assert.assertEquals;
import static org.springframework.http.HttpMethod.GET;
import static org.springframework.http.HttpStatus.OK;

public final class SpringSpaceQuotaDefinitionsTest extends AbstractRestTest {

    private final SpringSpaceQuotaDefinitions spaceQuotaDefinitions = new SpringSpaceQuotaDefinitions(this.restTemplate, this.root);

    @Test
    public void list() {
        mockRequest(new RequestContext()
                .method(GET).path("/v2/space_quota_definitions?page=-1")
                .status(OK)
                .responsePayload("v2/space_quota_definitions/GET_response.json"));

        ListSpaceQuotaDefinitionsRequest request = ListSpaceQuotaDefinitionsRequest.builder()
                .page(-1)
                .build();

        ListSpaceQuotaDefinitionsResponse expected = ListSpaceQuotaDefinitionsResponse.builder()
                .totalResults(1)
                .totalPages(1)
                .resource(SpaceQuotaDefinitionResource.builder()
                        .metadata(Metadata.builder()
                                .id("be2d5c01-3413-43db-bea2-49b0b60ec74d")
                                .url("/v2/space_quota_definitions/be2d5c01-3413-43db-bea2-49b0b60ec74d")
                                .createdAt("2015-07-27T22:43:32Z")
                                .build())
                        .entity(SpaceQuotaDefinitionEntity.builder()
                                .name("name-2236")
                                .organizationId("a81d5218-b473-474e-9afb-3223a8b2ae9f")
                                .nonBasicServicesAllowed(true)
                                .totalServices(60)
                                .totalRoutes(1000)
                                .memoryLimit(20480)
                                .instanceMemoryLimit(-1)
                                .organizationUrl("/v2/organizations/a81d5218-b473-474e-9afb-3223a8b2ae9f")
                                .spacesUrl("/v2/space_quota_definitions/be2d5c01-3413-43db-bea2-49b0b60ec74d/spaces")
                                .build())
                        .build())
                .build();

        ListSpaceQuotaDefinitionsResponse actual = Streams.wrap(this.spaceQuotaDefinitions.list(request)).next().get();

        assertEquals(expected, actual);
        verify();
    }

    @Test(expected = CloudFoundryException.class)
    public void listError() {
        mockRequest(new RequestContext()
                .method(GET).path("/v2/space_quota_definitions?page=-1")
                .errorResponse());

        ListSpaceQuotaDefinitionsRequest request = ListSpaceQuotaDefinitionsRequest.builder()
                .page(-1)
                .build();

        Streams.wrap(this.spaceQuotaDefinitions.list(request)).next().get();
    }

}
