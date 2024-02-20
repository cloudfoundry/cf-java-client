package org.cloudfoundry.reactor.client.v3.servicebrokers;

import io.netty.handler.codec.http.HttpMethod;
import io.netty.handler.codec.http.HttpResponseStatus;
import java.time.Duration;
import java.util.Collections;
import org.cloudfoundry.client.v3.Link;
import org.cloudfoundry.client.v3.Metadata;
import org.cloudfoundry.client.v3.Pagination;
import org.cloudfoundry.client.v3.Relationship;
import org.cloudfoundry.client.v3.ToOneRelationship;
import org.cloudfoundry.client.v3.servicebrokers.BasicAuthentication;
import org.cloudfoundry.client.v3.servicebrokers.CreateServiceBrokerRequest;
import org.cloudfoundry.client.v3.servicebrokers.DeleteServiceBrokerRequest;
import org.cloudfoundry.client.v3.servicebrokers.GetServiceBrokerRequest;
import org.cloudfoundry.client.v3.servicebrokers.GetServiceBrokerResponse;
import org.cloudfoundry.client.v3.servicebrokers.ListServiceBrokersRequest;
import org.cloudfoundry.client.v3.servicebrokers.ListServiceBrokersResponse;
import org.cloudfoundry.client.v3.servicebrokers.ServiceBrokerRelationships;
import org.cloudfoundry.client.v3.servicebrokers.ServiceBrokerResource;
import org.cloudfoundry.client.v3.servicebrokers.UpdateServiceBrokerRequest;
import org.cloudfoundry.client.v3.servicebrokers.UpdateServiceBrokerResponse;
import org.cloudfoundry.reactor.InteractionContext;
import org.cloudfoundry.reactor.TestRequest;
import org.cloudfoundry.reactor.TestResponse;
import org.cloudfoundry.reactor.client.AbstractClientApiTest;
import org.junit.jupiter.api.Test;
import reactor.test.StepVerifier;

final class ReactorServiceBrokersV3Test extends AbstractClientApiTest {

    private final ReactorServiceBrokersV3 serviceBrokersV3 =
            new ReactorServiceBrokersV3(
                    CONNECTION_CONTEXT, this.root, TOKEN_PROVIDER, Collections.emptyMap());

    @Test
    void create() {
        mockRequest(
                InteractionContext.builder()
                        .request(
                                TestRequest.builder()
                                        .method(HttpMethod.POST)
                                        .path("/service_brokers")
                                        .payload(
                                                "fixtures/client/v3/service_brokers/POST_request.json")
                                        .build())
                        .response(
                                TestResponse.builder()
                                        .status(HttpResponseStatus.ACCEPTED)
                                        .header(
                                                "Location",
                                                "https://api.example.org/v3/jobs/af5c57f6-8769-41fa-a499-2c84ed896788")
                                        .build())
                        .build());

        this.serviceBrokersV3
                .create(
                        CreateServiceBrokerRequest.builder()
                                .name("my_service_broker")
                                .url("https://example.service-broker.com")
                                .authentication(
                                        BasicAuthentication.builder()
                                                .username("us3rn4me")
                                                .password("p4ssw0rd")
                                                .build())
                                .relationships(
                                        ServiceBrokerRelationships.builder()
                                                .space(
                                                        ToOneRelationship.builder()
                                                                .data(
                                                                        Relationship.builder()
                                                                                .id(
                                                                                        "2f35885d-0c9d-4423-83ad-fd05066f8576")
                                                                                .build())
                                                                .build())
                                                .build())
                                .build())
                .as(StepVerifier::create)
                .expectNext("af5c57f6-8769-41fa-a499-2c84ed896788")
                .expectComplete()
                .verify(Duration.ofSeconds(5));
    }

    @Test
    void delete() {
        mockRequest(
                InteractionContext.builder()
                        .request(
                                TestRequest.builder()
                                        .method(HttpMethod.DELETE)
                                        .path("/service_brokers/test-service-broker-id")
                                        .build())
                        .response(
                                TestResponse.builder()
                                        .status(HttpResponseStatus.ACCEPTED)
                                        .header(
                                                "Location",
                                                "https://api.example.org/v3/jobs/af5c57f6-8769-41fa-a499-2c84ed896788")
                                        .build())
                        .build());

        this.serviceBrokersV3
                .delete(
                        DeleteServiceBrokerRequest.builder()
                                .serviceBrokerId("test-service-broker-id")
                                .build())
                .as(StepVerifier::create)
                .expectNext("af5c57f6-8769-41fa-a499-2c84ed896788")
                .expectComplete()
                .verify(Duration.ofSeconds(5));
    }

    @Test
    void get() {
        mockRequest(
                InteractionContext.builder()
                        .request(
                                TestRequest.builder()
                                        .method(HttpMethod.GET)
                                        .path("/service_brokers/test-service-broker-id")
                                        .build())
                        .response(
                                TestResponse.builder()
                                        .status(HttpResponseStatus.OK)
                                        .payload(
                                                "fixtures/client/v3/service_brokers/GET_{id}_response.json")
                                        .build())
                        .build());

        this.serviceBrokersV3
                .get(
                        GetServiceBrokerRequest.builder()
                                .serviceBrokerId("test-service-broker-id")
                                .build())
                .as(StepVerifier::create)
                .expectNext(
                        GetServiceBrokerResponse.builder()
                                .id("dde5ad2a-d8f4-44dc-a56f-0452d744f1c3")
                                .name("my_service_broker")
                                .url("https://example.service-broker.com")
                                .createdAt("2015-11-13T17:02:56Z")
                                .updatedAt("2016-06-08T16:41:26Z")
                                .relationships(
                                        ServiceBrokerRelationships.builder()
                                                .space(
                                                        ToOneRelationship.builder()
                                                                .data(
                                                                        Relationship.builder()
                                                                                .id(
                                                                                        "2f35885d-0c9d-4423-83ad-fd05066f8576")
                                                                                .build())
                                                                .build())
                                                .build())
                                .metadata(
                                        Metadata.builder()
                                                .label("type", "dev")
                                                .annotations(Collections.emptyMap())
                                                .build())
                                .link(
                                        "self",
                                        Link.builder()
                                                .href(
                                                        "https://api.example.org/v3/service_brokers/dde5ad2a-d8f4-44dc-a56f-0452d744f1c3")
                                                .build())
                                .link(
                                        "service_offerings",
                                        Link.builder()
                                                .href(
                                                        "https://api.example.org/v3/service_offerings?service_broker_guids=dde5ad2a-d8f4-44dc-a56f-0452d744f1c3")
                                                .build())
                                .link(
                                        "space",
                                        Link.builder()
                                                .href(
                                                        "https://api.example.org/v3/spaces/2f35885d-0c9d-4423-83ad-fd05066f8576")
                                                .build())
                                .build())
                .expectComplete()
                .verify(Duration.ofSeconds(5));
    }

    @Test
    void list() {
        mockRequest(
                InteractionContext.builder()
                        .request(
                                TestRequest.builder()
                                        .method(HttpMethod.GET)
                                        .path("/service_brokers")
                                        .build())
                        .response(
                                TestResponse.builder()
                                        .status(HttpResponseStatus.OK)
                                        .payload(
                                                "fixtures/client/v3/service_brokers/GET_response.json")
                                        .build())
                        .build());

        this.serviceBrokersV3
                .list(ListServiceBrokersRequest.builder().build())
                .as(StepVerifier::create)
                .expectNext(
                        ListServiceBrokersResponse.builder()
                                .pagination(
                                        Pagination.builder()
                                                .totalResults(3)
                                                .totalPages(2)
                                                .first(
                                                        Link.builder()
                                                                .href(
                                                                        "https://api.example.org/v3/service_brokers?page=1&per_page=2")
                                                                .build())
                                                .last(
                                                        Link.builder()
                                                                .href(
                                                                        "https://api.example.org/v3/service_brokers?page=2&per_page=2")
                                                                .build())
                                                .next(
                                                        Link.builder()
                                                                .href(
                                                                        "https://api.example.org/v3/service_brokers?page=2&per_page=2")
                                                                .build())
                                                .build())
                                .resource(
                                        ServiceBrokerResource.builder()
                                                .id("dde5ad2a-d8f4-44dc-a56f-0452d744f1c3")
                                                .name("my_service_broker")
                                                .url("https://example.service-broker.com")
                                                .createdAt("2015-11-13T17:02:56Z")
                                                .updatedAt("2016-06-08T16:41:26Z")
                                                .relationships(
                                                        ServiceBrokerRelationships.builder()
                                                                .build())
                                                .metadata(
                                                        Metadata.builder()
                                                                .labels(Collections.emptyMap())
                                                                .annotations(Collections.emptyMap())
                                                                .build())
                                                .link(
                                                        "self",
                                                        Link.builder()
                                                                .href(
                                                                        "https://api.example.org/v3/service_brokers/dde5ad2a-d8f4-44dc-a56f-0452d744f1c3")
                                                                .build())
                                                .link(
                                                        "service_offerings",
                                                        Link.builder()
                                                                .href(
                                                                        "https://api.example.org/v3/service_offerings?service_broker_guids=dde5ad2a-d8f4-44dc-a56f-0452d744f1c3")
                                                                .build())
                                                .build())
                                .resource(
                                        ServiceBrokerResource.builder()
                                                .id("7aa37bad-6ccb-4ef9-ba48-9ce3a91b2b62")
                                                .name("another_service_broker")
                                                .url("https://another-example.service-broker.com")
                                                .createdAt("2015-11-13T17:02:56Z")
                                                .updatedAt("2016-06-08T16:41:26Z")
                                                .relationships(
                                                        ServiceBrokerRelationships.builder()
                                                                .space(
                                                                        ToOneRelationship.builder()
                                                                                .data(
                                                                                        Relationship
                                                                                                .builder()
                                                                                                .id(
                                                                                                        "2f35885d-0c9d-4423-83ad-fd05066f8576")
                                                                                                .build())
                                                                                .build())
                                                                .build())
                                                .metadata(
                                                        Metadata.builder()
                                                                .labels(Collections.emptyMap())
                                                                .annotations(Collections.emptyMap())
                                                                .build())
                                                .link(
                                                        "self",
                                                        Link.builder()
                                                                .href(
                                                                        "https://api.example.org/v3/service_brokers/7aa37bad-6ccb-4ef9-ba48-9ce3a91b2b62")
                                                                .build())
                                                .link(
                                                        "service_offerings",
                                                        Link.builder()
                                                                .href(
                                                                        "https://api.example.org/v3/service_offerings?service_broker_guids=7aa37bad-6ccb-4ef9-ba48-9ce3a91b2b62")
                                                                .build())
                                                .link(
                                                        "space",
                                                        Link.builder()
                                                                .href(
                                                                        "https://api.example.org/v3/spaces/2f35885d-0c9d-4423-83ad-fd05066f8576")
                                                                .build())
                                                .build())
                                .build())
                .expectComplete()
                .verify(Duration.ofSeconds(5));
    }

    @Test
    void update() {
        mockRequest(
                InteractionContext.builder()
                        .request(
                                TestRequest.builder()
                                        .method(HttpMethod.PATCH)
                                        .path("/service_brokers/test-service-broker-id")
                                        .payload(
                                                "fixtures/client/v3/service_brokers/PATCH_{id}_request.json")
                                        .build())
                        .response(
                                TestResponse.builder()
                                        .status(HttpResponseStatus.ACCEPTED)
                                        .header(
                                                "Location",
                                                "https://api.example.org/v3/jobs/af5c57f6-8769-41fa-a499-2c84ed896788")
                                        .build())
                        .build());

        this.serviceBrokersV3
                .update(
                        UpdateServiceBrokerRequest.builder()
                                .serviceBrokerId("test-service-broker-id")
                                .name("my_service_broker")
                                .url("https://example.service-broker.com")
                                .authentication(
                                        BasicAuthentication.builder()
                                                .username("us3rn4me")
                                                .password("p4ssw0rd")
                                                .build())
                                .metadata(
                                        Metadata.builder()
                                                .label("key", "value")
                                                .annotation("note", "detailed information")
                                                .build())
                                .build())
                .doOnSuccess(response -> System.out.println(response))
                .as(StepVerifier::create)
                .expectNext(
                        UpdateServiceBrokerResponse.builder()
                                .jobId("af5c57f6-8769-41fa-a499-2c84ed896788")
                                .build())
                .expectComplete()
                .verify(Duration.ofSeconds(5));
    }

    @Test
    void updateMetadata() {
        mockRequest(
                InteractionContext.builder()
                        .request(
                                TestRequest.builder()
                                        .method(HttpMethod.PATCH)
                                        .path("/service_brokers/test-service-broker-id")
                                        .payload(
                                                "fixtures/client/v3/service_brokers/PATCH_{id}_metadata_request.json")
                                        .build())
                        .response(
                                TestResponse.builder()
                                        .status(HttpResponseStatus.OK)
                                        .payload(
                                                "fixtures/client/v3/service_brokers/PATCH_{id}_metadata_response.json")
                                        .build())
                        .build());

        this.serviceBrokersV3
                .update(
                        UpdateServiceBrokerRequest.builder()
                                .serviceBrokerId("test-service-broker-id")
                                .metadata(
                                        Metadata.builder()
                                                .label("type", "dev")
                                                .annotations(Collections.emptyMap())
                                                .build())
                                .build())
                .doOnSuccess(response -> System.out.println(response))
                .as(StepVerifier::create)
                .expectNext(
                        UpdateServiceBrokerResponse.builder()
                                .serviceBroker(
                                        ServiceBrokerResource.builder()
                                                .id("dde5ad2a-d8f4-44dc-a56f-0452d744f1c3")
                                                .name("my_service_broker")
                                                .url("https://example.service-broker.com")
                                                .createdAt("2015-11-13T17:02:56Z")
                                                .updatedAt("2016-06-08T16:41:26Z")
                                                .relationships(
                                                        ServiceBrokerRelationships.builder()
                                                                .space(
                                                                        ToOneRelationship.builder()
                                                                                .data(
                                                                                        Relationship
                                                                                                .builder()
                                                                                                .id(
                                                                                                        "2f35885d-0c9d-4423-83ad-fd05066f8576")
                                                                                                .build())
                                                                                .build())
                                                                .build())
                                                .metadata(
                                                        Metadata.builder()
                                                                .label("type", "dev")
                                                                .annotations(Collections.emptyMap())
                                                                .build())
                                                .link(
                                                        "self",
                                                        Link.builder()
                                                                .href(
                                                                        "https://api.example.org/v3/service_brokers/dde5ad2a-d8f4-44dc-a56f-0452d744f1c3")
                                                                .build())
                                                .link(
                                                        "service_offerings",
                                                        Link.builder()
                                                                .href(
                                                                        "https://api.example.org/v3/service_offerings?service_broker_guids=dde5ad2a-d8f4-44dc-a56f-0452d744f1c3")
                                                                .build())
                                                .link(
                                                        "space",
                                                        Link.builder()
                                                                .href(
                                                                        "https://api.example.org/v3/spaces/2f35885d-0c9d-4423-83ad-fd05066f8576")
                                                                .build())
                                                .build())
                                .build())
                .expectComplete()
                .verify(Duration.ofSeconds(5));
    }
}
