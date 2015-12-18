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
import org.cloudfoundry.client.v2.domains.CreateSharedDomainRequest;
import org.cloudfoundry.client.v2.domains.CreateSharedDomainResponse;
import org.cloudfoundry.client.v2.domains.DeleteDomainRequest;
import org.cloudfoundry.client.v2.domains.DomainEntity;
import org.cloudfoundry.client.v2.domains.DomainResource;
import org.cloudfoundry.client.v2.domains.GetDomainRequest;
import org.cloudfoundry.client.v2.domains.GetDomainResponse;
import org.cloudfoundry.client.v2.domains.ListDomainSpacesRequest;
import org.cloudfoundry.client.v2.domains.ListDomainSpacesResponse;
import org.cloudfoundry.client.v2.domains.ListDomainsRequest;
import org.cloudfoundry.client.v2.domains.ListDomainsResponse;
import org.cloudfoundry.client.v2.spaces.SpaceEntity;
import org.cloudfoundry.client.v2.spaces.SpaceResource;
import org.junit.Test;
import reactor.rx.Streams;

import java.util.Collections;

import static org.cloudfoundry.client.v2.Resource.Metadata;
import static org.junit.Assert.assertEquals;
import static org.springframework.http.HttpMethod.DELETE;
import static org.springframework.http.HttpMethod.GET;
import static org.springframework.http.HttpMethod.POST;
import static org.springframework.http.HttpStatus.NO_CONTENT;
import static org.springframework.http.HttpStatus.OK;

public final class SpringDomainsTest extends AbstractRestTest {

    private final SpringDomains domains = new SpringDomains(this.restTemplate, this.root);

    @Test
    public void createShared() {
        mockRequest(new RequestContext()
                .method(POST).path("v2/domains")
                .requestPayload("v2/domains/POST_request.json")
                .status(OK)
                .responsePayload("v2/domains/POST_response.json"));

        CreateSharedDomainRequest request = CreateSharedDomainRequest.builder()
                .name("example.com")
                .wildcard(true)
                .build();

        CreateSharedDomainResponse expected = CreateSharedDomainResponse.builder()
                .metadata(Metadata.builder()
                        .id("cf2b6b9b-aa02-4ab8-ae20-0d5454dd7e98")
                        .url("/v2/domains/cf2b6b9b-aa02-4ab8-ae20-0d5454dd7e98")
                        .createdAt("2015-07-27T22:43:33Z")
                        .build())
                .entity(DomainEntity.builder()
                        .name("example.com")
                        .sharedOrganizations(Collections.<String>emptyList())
                        .build())
                .build();

        CreateSharedDomainResponse actual = Streams.wrap(this.domains.createShared(request)).next().get();

        assertEquals(expected, actual);
        verify();
    }

    @Test(expected = CloudFoundryException.class)
    public void createSharedError() {
        mockRequest(new RequestContext()
                .method(POST).path("v2/domains")
                .requestPayload("v2/domains/POST_request.json")
                .errorResponse());

        CreateSharedDomainRequest request = CreateSharedDomainRequest.builder()
                .name("example.com")
                .wildcard(true)
                .build();

        Streams.wrap(this.domains.createShared(request)).next().get();
    }

    @Test(expected = RequestValidationException.class)
    public void createSharedInvalidRequest() {
        CreateSharedDomainRequest request = CreateSharedDomainRequest.builder()
                .build();

        Streams.wrap(this.domains.createShared(request)).next().get();
    }

    @Test
    public void delete() {
        mockRequest(new RequestContext()
                .method(DELETE).path("v2/domains/test-id")
                .status(NO_CONTENT));

        DeleteDomainRequest request = DeleteDomainRequest.builder()
                .id("test-id")
                .build();

        Streams.wrap(this.domains.delete(request)).next().get();

        verify();
    }

    @Test(expected = CloudFoundryException.class)
    public void deleteError() {
        mockRequest(new RequestContext()
                .method(DELETE).path("v2/domains/test-id")
                .errorResponse());

        DeleteDomainRequest request = DeleteDomainRequest.builder()
                .id("test-id")
                .build();

        Streams.wrap(this.domains.delete(request)).next().get();
    }

    @Test(expected = RequestValidationException.class)
    public void deleteInvalidRequest() {
        DeleteDomainRequest request = DeleteDomainRequest.builder()
                .build();

        Streams.wrap(this.domains.delete(request)).next().get();
    }

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
    public void listDomains() {
        mockRequest(new RequestContext()
                .method(GET).path("v2/domains?page=-1")
                .status(OK)
                .responsePayload("v2/domains/GET_response.json"));

        ListDomainsRequest request = ListDomainsRequest.builder()
                .page(-1)
                .build();

        ListDomainsResponse expected = ListDomainsResponse.builder()
                .totalResults(4)
                .totalPages(1)
                .resource(DomainResource.builder()
                        .metadata(Metadata.builder()
                                .id("c8e670ff-2473-4e21-8047-afc6e0c62ce7")
                                .url("/v2/domains/c8e670ff-2473-4e21-8047-afc6e0c62ce7")
                                .createdAt("2015-07-27T22:43:31Z")
                                .build())
                        .entity(DomainEntity.builder()
                                .name("customer-app-domain1.com")
                                .build())
                        .build())
                .resource(DomainResource.builder()
                        .metadata(Metadata.builder()
                                .id("2b63d3fa-52e9-4f12-87d1-a96af5bd3cd4")
                                .url("/v2/domains/2b63d3fa-52e9-4f12-87d1-a96af5bd3cd4")
                                .createdAt("2015-07-27T22:43:31Z")
                                .build())
                        .entity(DomainEntity.builder()
                                .name("customer-app-domain2.com")
                                .build())
                        .build())
                .resource(DomainResource.builder()
                        .metadata(Metadata.builder()
                                .id("2c60a78c-0f6e-4ef8-81db-f3a6cb5e31da")
                                .url("/v2/domains/2c60a78c-0f6e-4ef8-81db-f3a6cb5e31da")
                                .createdAt("2015-07-27T22:43:31Z")
                                .build())
                        .entity(DomainEntity.builder()
                                .name("vcap.me")
                                .owningOrganizationId("f93d5a41-5d35-4e21-ac32-421dfd545d3c")
                                .owningOrganizationUrl("/v2/organizations/f93d5a41-5d35-4e21-ac32-421dfd545d3c")
                                .spacesUrl("/v2/domains/2c60a78c-0f6e-4ef8-81db-f3a6cb5e31da/spaces")
                                .build())
                        .build())
                .resource(DomainResource.builder()
                        .metadata(Metadata.builder()
                                .id("b37aab98-5882-420a-a91f-65539e36e860")
                                .url("/v2/domains/b37aab98-5882-420a-a91f-65539e36e860")
                                .createdAt("2015-07-27T22:43:33Z")
                                .build())
                        .entity(DomainEntity.builder()
                                .name("domain-62.example.com")
                                .build())
                        .build())
                .build();

        ListDomainsResponse actual = Streams.wrap(this.domains.listDomains(request)).next().get();

        assertEquals(expected, actual);
        verify();
    }

    @Test(expected = CloudFoundryException.class)
    public void listDomainsError() {
        mockRequest(new RequestContext()
                .method(GET).path("v2/domains?page=-1")
                .errorResponse());

        ListDomainsRequest request = ListDomainsRequest.builder()
                .page(-1)
                .build();

        Streams.wrap(this.domains.listDomains(request)).next().get();
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
