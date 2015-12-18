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

package org.cloudfoundry.client.spring.v2.domains;

import org.cloudfoundry.client.RequestValidationException;
import org.cloudfoundry.client.spring.AbstractRestTest;
import org.cloudfoundry.client.v2.CloudFoundryException;
import org.cloudfoundry.client.v2.domains.DomainEntity;
import org.cloudfoundry.client.v2.domains.GetDomainRequest;
import org.cloudfoundry.client.v2.domains.GetDomainResponse;
import org.cloudfoundry.client.v2.domains.ListDomainSpacesRequest;
import org.cloudfoundry.client.v2.domains.ListDomainSpacesResponse;
import org.cloudfoundry.client.v2.spaces.SpaceEntity;
import org.cloudfoundry.client.v2.spaces.SpaceResource;
import org.junit.Test;
import reactor.rx.Streams;

import static org.cloudfoundry.client.v2.Resource.Metadata;
import static org.junit.Assert.assertEquals;
import static org.springframework.http.HttpMethod.GET;
import static org.springframework.http.HttpStatus.OK;

public final class SpringDomainsTest extends AbstractRestTest {

    private final SpringDomains domains = new SpringDomains(this.restTemplate, this.root);

    @Test
    public void get() {
        mockRequest(new AbstractRestTest.RequestContext()
                .method(GET).path("/v2/domains/test-id")
                .status(OK)
                .responsePayload("v2/domains/GET_{id}_response.json"));

        GetDomainRequest request = GetDomainRequest.builder()
                .id("test-id")
                .build();

        GetDomainResponse expected = GetDomainResponse.builder()
                .metadata(Metadata.builder()
                        .id("7cd249aa-197c-425c-8831-57cbc24e8e26")
                        .url("/v2/domains/7cd249aa-197c-425c-8831-57cbc24e8e26")
                        .createdAt("2015-07-27T22:43:33Z")
                        .build())
                .entity(DomainEntity.builder()
                        .name("domain-63.example.com")
                        .build())
                .build();

        GetDomainResponse actual = Streams.wrap(this.domains.get(request)).next().get();

        assertEquals(expected, actual);
        verify();
    }

    @Test(expected = CloudFoundryException.class)
    public void getError() {
        mockRequest(new AbstractRestTest.RequestContext()
                .method(GET).path("/v2/domains/test-id")
                .errorResponse());

        GetDomainRequest request = GetDomainRequest.builder()
                .id("test-id")
                .build();

        Streams.wrap(this.domains.get(request)).next().get();
    }

    @Test(expected = RequestValidationException.class)
    public void getInvalidRequest() {
        GetDomainRequest request = GetDomainRequest.builder()
                .build();

        Streams.wrap(this.domains.get(request)).next().get();
    }

    @Test
    public void listSpaces() {
        mockRequest(new RequestContext()
                .method(GET).path("v2/domains/test-id/spaces?page=-1")
                .status(OK)
                .responsePayload("v2/domains/GET_{id}_spaces_response.json"));

        ListDomainSpacesRequest request = ListDomainSpacesRequest.builder()
                .id("test-id")
                .page(-1)
                .build();

        ListDomainSpacesResponse expected = ListDomainSpacesResponse.builder()
                .totalResults(1)
                .totalPages(1)
                .resource(SpaceResource.builder()
                        .metadata(Metadata.builder()
                                .id("d1686ef7-59dc-4ada-8900-85e89d749046")
                                .url("/v2/spaces/d1686ef7-59dc-4ada-8900-85e89d749046")
                                .createdAt("2015-07-27T22:43:33Z")
                                .build())
                        .entity(SpaceEntity.builder()
                                .name("name-2311")
                                .organizationId("836b112a-30bc-4d55-b8e4-7323849759d1")
                                .allowSsh(true)
                                .organizationUrl("/v2/organizations/836b112a-30bc-4d55-b8e4-7323849759d1")
                                .developersUrl("/v2/spaces/d1686ef7-59dc-4ada-8900-85e89d749046/developers")
                                .managersUrl("/v2/spaces/d1686ef7-59dc-4ada-8900-85e89d749046/managers")
                                .auditorsUrl("/v2/spaces/d1686ef7-59dc-4ada-8900-85e89d749046/auditors")
                                .applicationsUrl("/v2/spaces/d1686ef7-59dc-4ada-8900-85e89d749046/apps")
                                .routesUrl("/v2/spaces/d1686ef7-59dc-4ada-8900-85e89d749046/routes")
                                .domainsUrl("/v2/spaces/d1686ef7-59dc-4ada-8900-85e89d749046/domains")
                                .serviceInstancesUrl
                                        ("/v2/spaces/d1686ef7-59dc-4ada-8900-85e89d749046/service_instances")
                                .applicationEventsUrl("/v2/spaces/d1686ef7-59dc-4ada-8900-85e89d749046/app_events")
                                .eventsUrl("/v2/spaces/d1686ef7-59dc-4ada-8900-85e89d749046/events")
                                .securityGroupsUrl("/v2/spaces/d1686ef7-59dc-4ada-8900-85e89d749046/security_groups")
                                .build())
                        .build())
                .build();


        ListDomainSpacesResponse actual = Streams.wrap(this.domains.listSpaces(request)).next().get();

        assertEquals(expected, actual);
        verify();
    }

    @Test(expected = CloudFoundryException.class)
    public void listSpacesError() {
        mockRequest(new RequestContext()
                .method(GET).path("v2/domains/test-id/spaces?page=-1")
                .errorResponse());

        ListDomainSpacesRequest request = ListDomainSpacesRequest.builder()
                .id("test-id")
                .page(-1)
                .build();

        Streams.wrap(this.domains.listSpaces(request)).next().get();
    }

    @Test(expected = RequestValidationException.class)
    public void listSpacesInvalidRequest() {
        ListDomainSpacesRequest request = ListDomainSpacesRequest.builder()
                .build();

        Streams.wrap(this.domains.listSpaces(request)).next().get();
    }

}
