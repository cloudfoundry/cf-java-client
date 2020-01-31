/*
 * Copyright 2013-2020 the original author or authors.
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

package org.cloudfoundry.reactor.client.v2.privatedomains;

import org.cloudfoundry.client.v2.Metadata;
import org.cloudfoundry.client.v2.jobs.JobEntity;
import org.cloudfoundry.client.v2.organizations.OrganizationEntity;
import org.cloudfoundry.client.v2.organizations.OrganizationResource;
import org.cloudfoundry.client.v2.privatedomains.CreatePrivateDomainRequest;
import org.cloudfoundry.client.v2.privatedomains.CreatePrivateDomainResponse;
import org.cloudfoundry.client.v2.privatedomains.DeletePrivateDomainRequest;
import org.cloudfoundry.client.v2.privatedomains.DeletePrivateDomainResponse;
import org.cloudfoundry.client.v2.privatedomains.GetPrivateDomainRequest;
import org.cloudfoundry.client.v2.privatedomains.GetPrivateDomainResponse;
import org.cloudfoundry.client.v2.privatedomains.ListPrivateDomainSharedOrganizationsRequest;
import org.cloudfoundry.client.v2.privatedomains.ListPrivateDomainSharedOrganizationsResponse;
import org.cloudfoundry.client.v2.privatedomains.ListPrivateDomainsRequest;
import org.cloudfoundry.client.v2.privatedomains.ListPrivateDomainsResponse;
import org.cloudfoundry.client.v2.privatedomains.PrivateDomainEntity;
import org.cloudfoundry.client.v2.privatedomains.PrivateDomainResource;
import org.cloudfoundry.reactor.InteractionContext;
import org.cloudfoundry.reactor.TestRequest;
import org.cloudfoundry.reactor.TestResponse;
import org.cloudfoundry.reactor.client.AbstractClientApiTest;
import org.junit.Test;
import reactor.test.StepVerifier;

import java.time.Duration;
import java.util.Collections;

import static io.netty.handler.codec.http.HttpMethod.DELETE;
import static io.netty.handler.codec.http.HttpMethod.GET;
import static io.netty.handler.codec.http.HttpMethod.POST;
import static io.netty.handler.codec.http.HttpResponseStatus.ACCEPTED;
import static io.netty.handler.codec.http.HttpResponseStatus.NO_CONTENT;
import static io.netty.handler.codec.http.HttpResponseStatus.OK;

public final class ReactorPrivateDomainsTest extends AbstractClientApiTest {

    private final ReactorPrivateDomains privateDomains = new ReactorPrivateDomains(CONNECTION_CONTEXT, this.root, TOKEN_PROVIDER, Collections.emptyMap());

    @Test
    public void create() {
        mockRequest(InteractionContext.builder()
            .request(TestRequest.builder()
                .method(POST).path("/private_domains")
                .payload("fixtures/client/v2/private_domains/POST_request.json")
                .build())
            .response(TestResponse.builder()
                .status(OK)
                .payload("fixtures/client/v2/private_domains/POST_response.json")
                .build())
            .build());

        this.privateDomains
            .create(CreatePrivateDomainRequest.builder()
                .name("exmaple.com")
                .owningOrganizationId("22bb8ae1-6324-40eb-b077-bd1bfad773f8")
                .build())
            .as(StepVerifier::create)
            .expectNext(CreatePrivateDomainResponse.builder()
                .metadata(Metadata.builder()
                    .id("4af3234e-813d-453f-b3ae-fcdecfd87a47")
                    .url("/v2/private_domains/4af3234e-813d-453f-b3ae-fcdecfd87a47")
                    .createdAt("2016-01-19T19:41:12Z")
                    .build())
                .entity(PrivateDomainEntity.builder()
                    .name("exmaple.com")
                    .owningOrganizationId("22bb8ae1-6324-40eb-b077-bd1bfad773f8")
                    .owningOrganizationUrl("/v2/organizations/22bb8ae1-6324-40eb-b077-bd1bfad773f8")
                    .sharedOrganizationsUrl("/v2/private_domains/4af3234e-813d-453f-b3ae-fcdecfd87a47/shared_organizations")
                    .build())
                .build())
            .expectComplete()
            .verify(Duration.ofSeconds(5));
    }

    @Test
    public void delete() {
        mockRequest(InteractionContext.builder()
            .request(TestRequest.builder()
                .method(DELETE).path("/private_domains/test-private-domain-id")
                .build())
            .response(TestResponse.builder()
                .status(NO_CONTENT)
                .build())
            .build());

        this.privateDomains
            .delete(DeletePrivateDomainRequest.builder()
                .privateDomainId("test-private-domain-id")
                .build())
            .as(StepVerifier::create)
            .expectComplete()
            .verify(Duration.ofSeconds(5));
    }

    @Test
    public void deleteAsync() {
        mockRequest(InteractionContext.builder()
            .request(TestRequest.builder()
                .method(DELETE).path("/private_domains/test-private-domain-id?async=true")
                .build())
            .response(TestResponse.builder()
                .status(ACCEPTED)
                .payload("fixtures/client/v2/private_domains/DELETE_{id}_async_response.json")
                .build())
            .build());

        this.privateDomains
            .delete(DeletePrivateDomainRequest.builder()
                .async(true)
                .privateDomainId("test-private-domain-id")
                .build())
            .as(StepVerifier::create)
            .expectNext(DeletePrivateDomainResponse.builder()
                .metadata(Metadata.builder()
                    .id("2d9707ba-6f0b-4aef-a3de-fe9bdcf0c9d1")
                    .createdAt("2016-02-02T17:16:31Z")
                    .url("/v2/jobs/2d9707ba-6f0b-4aef-a3de-fe9bdcf0c9d1")
                    .build())
                .entity(JobEntity.builder()
                    .id("2d9707ba-6f0b-4aef-a3de-fe9bdcf0c9d1")
                    .status("queued")
                    .build())
                .build())
            .expectComplete()
            .verify(Duration.ofSeconds(5));
    }

    @Test
    public void get() {
        mockRequest(InteractionContext.builder()
            .request(TestRequest.builder()
                .method(GET).path("/private_domains/test-private-domain-id")
                .build())
            .response(TestResponse.builder()
                .status(OK)
                .payload("fixtures/client/v2/private_domains/GET_{id}_response.json")
                .build())
            .build());

        this.privateDomains
            .get(GetPrivateDomainRequest.builder()
                .privateDomainId("test-private-domain-id")
                .build())
            .as(StepVerifier::create)
            .expectNext(GetPrivateDomainResponse.builder()
                .metadata(Metadata.builder()
                    .id("3de9db5f-8e3b-4d10-a8c9-8137caafe43d")
                    .url("/v2/private_domains/3de9db5f-8e3b-4d10-a8c9-8137caafe43d")
                    .createdAt("2016-02-19T02:04:00Z")
                    .build())
                .entity(PrivateDomainEntity.builder()
                    .name("my-domain.com")
                    .owningOrganizationId("2f70efed-abb2-4b7a-9f31-d4fe4d849932")
                    .owningOrganizationUrl("/v2/organizations/2f70efed-abb2-4b7a-9f31-d4fe4d849932")
                    .sharedOrganizationsUrl("/v2/private_domains/3de9db5f-8e3b-4d10-a8c9-8137caafe43d/shared_organizations")
                    .build())
                .build())
            .expectComplete()
            .verify(Duration.ofSeconds(5));
    }

    @Test
    public void list() {
        mockRequest(InteractionContext.builder()
            .request(TestRequest.builder()
                .method(GET).path("/private_domains?q=name%3Atest-name.com&page=-1")
                .build())
            .response(TestResponse.builder()
                .status(OK)
                .payload("fixtures/client/v2/private_domains/GET_response.json")
                .build())
            .build());

        this.privateDomains
            .list(ListPrivateDomainsRequest.builder()
                .name("test-name.com")
                .page(-1)
                .build())
            .as(StepVerifier::create)
            .expectNext(ListPrivateDomainsResponse.builder()
                .totalResults(1)
                .totalPages(1)
                .resource(PrivateDomainResource.builder()
                    .metadata(Metadata.builder()
                        .id("3de9db5f-8e3b-4d10-a8c9-8137caafe43d")
                        .url("/v2/private_domains/3de9db5f-8e3b-4d10-a8c9-8137caafe43d")
                        .createdAt("2016-02-19T02:04:00Z")
                        .build())
                    .entity(PrivateDomainEntity.builder()
                        .name("my-domain.com")
                        .owningOrganizationId("2f70efed-abb2-4b7a-9f31-d4fe4d849932")
                        .owningOrganizationUrl("/v2/organizations/2f70efed-abb2-4b7a-9f31-d4fe4d849932")
                        .sharedOrganizationsUrl("/v2/private_domains/3de9db5f-8e3b-4d10-a8c9-8137caafe43d/shared_organizations")
                        .build())
                    .build())
                .build())
            .expectComplete()
            .verify(Duration.ofSeconds(5));
    }

    @Test
    public void listSharedOrganizations() {
        mockRequest(InteractionContext.builder()
            .request(TestRequest.builder()
                .method(GET).path("/private_domains/b2a35f0c-d5ad-4a59-bea7-461711d96b0d/shared_organizations")
                .build())
            .response(TestResponse.builder()
                .status(OK)
                .payload("fixtures/client/v2/private_domains/GET_{id}_shared_organizations_response.json")
                .build())
            .build());

        this.privateDomains
            .listSharedOrganizations(ListPrivateDomainSharedOrganizationsRequest.builder()
                .privateDomainId("b2a35f0c-d5ad-4a59-bea7-461711d96b0d")
                .build())
            .as(StepVerifier::create)
            .expectNext(ListPrivateDomainSharedOrganizationsResponse.builder()
                .totalResults(1)
                .totalPages(1)
                .resource(OrganizationResource.builder()
                    .metadata(Metadata.builder()
                        .id("420bb3ef-6064-42ed-9131-c1fe980bf9c6")
                        .createdAt("2016-06-08T16:41:39Z")
                        .updatedAt("2016-06-08T16:41:26Z")
                        .url("/v2/organizations/420bb3ef-6064-42ed-9131-c1fe980bf9c6")
                        .build())
                    .entity(OrganizationEntity.builder()
                        .name("name-2005")
                        .billingEnabled(false)
                        .quotaDefinitionId("2e863ad3-7bb9-4c5c-928c-cde2caec341b")
                        .status("active")
                        .quotaDefinitionUrl("/v2/quota_definitions/2e863ad3-7bb9-4c5c-928c-cde2caec341b")
                        .spacesUrl("/v2/organizations/420bb3ef-6064-42ed-9131-c1fe980bf9c6/spaces")
                        .domainsUrl("/v2/organizations/420bb3ef-6064-42ed-9131-c1fe980bf9c6/domains")
                        .privateDomainsUrl("/v2/organizations/420bb3ef-6064-42ed-9131-c1fe980bf9c6/private_domains")
                        .usersUrl("/v2/organizations/420bb3ef-6064-42ed-9131-c1fe980bf9c6/users")
                        .managersUrl("/v2/organizations/420bb3ef-6064-42ed-9131-c1fe980bf9c6/managers")
                        .billingManagersUrl("/v2/organizations/420bb3ef-6064-42ed-9131-c1fe980bf9c6/billing_managers")
                        .auditorsUrl("/v2/organizations/420bb3ef-6064-42ed-9131-c1fe980bf9c6/auditors")
                        .applicationEventsUrl("/v2/organizations/420bb3ef-6064-42ed-9131-c1fe980bf9c6/app_events")
                        .spaceQuotaDefinitionsUrl("/v2/organizations/420bb3ef-6064-42ed-9131-c1fe980bf9c6/space_quota_definitions")
                        .build())
                    .build())
                .build())
            .expectComplete()
            .verify(Duration.ofSeconds(5));
    }

}
