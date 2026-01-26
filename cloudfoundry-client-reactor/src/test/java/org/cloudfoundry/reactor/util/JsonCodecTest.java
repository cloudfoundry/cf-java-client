package org.cloudfoundry.reactor.util;

import static org.junit.jupiter.api.Assertions.assertEquals;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Path;
import java.time.Duration;
import java.util.Map;
import org.cloudfoundry.uaa.clients.ListClientsResponse;
import org.cloudfoundry.uaa.identityzones.ListIdentityZonesResponse;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import reactor.netty.ByteBufFlux;
import reactor.test.StepVerifier;

public class JsonCodecTest {

    private ObjectMapper objectMapper;
    private ByteBufFlux body;

    @BeforeEach
    void setup() throws IOException {
        objectMapper = new ObjectMapper();
        objectMapper.addHandler(new JsonDeserializationProblemHandler());
    }

    @AfterEach
    void tearDown() {
        JsonDeserializationProblemHandler.flush();
    }

    @Test
    void invalidProperyGetsReported() throws URISyntaxException {
        URL fileUrl =
                getClass()
                        .getResource("/fixtures/util/AddedProperty_ListIdentityZonesResponse.json");
        Path path = Path.of(fileUrl.toURI());
        body = ByteBufFlux.fromPath(path);
        StepVerifier.create(JsonCodec.decode(objectMapper, body, ListIdentityZonesResponse.class))
                .expectError(JsonParsingException.class)
                .verify(Duration.ofSeconds(2));
    }

    @Test
    void invalidProperyWithWrongIgnoreGetsReported() throws URISyntaxException {
        JsonDeserializationProblemHandler.addPropertyToIgnore(
                ListIdentityZonesResponse.class.getCanonicalName(),
                "newPropertyNotKnownInType",
                "/*/invalid/other");
        URL fileUrl =
                getClass()
                        .getResource("/fixtures/util/AddedProperty_ListIdentityZonesResponse.json");
        Path path = Path.of(fileUrl.toURI());
        body = ByteBufFlux.fromPath(path);
        StepVerifier.create(JsonCodec.decode(objectMapper, body, ListIdentityZonesResponse.class))
                .expectError(JsonParsingException.class)
                .verify(Duration.ofSeconds(2));
    }

    @Test
    void invalidPropertyWrongTypeGetsReported() throws URISyntaxException {
        URL fileUrl =
                getClass()
                        .getResource("/fixtures/util/PropertyWrongType2_ListClientsResponse.json");
        Path path = Path.of(fileUrl.toURI());
        body = ByteBufFlux.fromPath(path);
        StepVerifier.create(JsonCodec.decode(objectMapper, body, ListClientsResponse.class))
                .expectError(JsonParsingException.class)
                .verify(Duration.ofSeconds(2));
    }

    @Test
    void invalidValueGetsReported() throws URISyntaxException {
        JsonDeserializationProblemHandler.addPropertyToIgnore(
                ListClientsResponse.class.getCanonicalName(),
                "autoapprove",
                "/resources/*/autoapprove");
        URL fileUrl = getClass().getResource("/fixtures/util/AddedValue_ListClientsResponse.json");
        Path path = Path.of(fileUrl.toURI());
        body = ByteBufFlux.fromPath(path);
        StepVerifier.create(JsonCodec.decode(objectMapper, body, ListClientsResponse.class))
                .expectError(JsonParsingException.class)
                .verify(Duration.ofSeconds(2));
    }

    @Test
    void invalidProperyGetsDeleted() throws URISyntaxException {
        JsonDeserializationProblemHandler.addPropertyToIgnore(
                "org.cloudfoundry.uaa.identityzones.IdentityZoneConfiguration.Json",
                "newPropertyNotKnownInType",
                "/0/config/newPropertyNotKnownInType");
        URL fileUrl =
                getClass()
                        .getResource("/fixtures/util/AddedProperty_ListIdentityZonesResponse.json");
        Path path = Path.of(fileUrl.toURI());
        body = ByteBufFlux.fromPath(path);
        StepVerifier.create(JsonCodec.decode(objectMapper, body, ListIdentityZonesResponse.class))
                .assertNext(this::checkResponse1)
                .verifyComplete();
    }

    @Test
    void invalidProperyGetsDeletedWithWildcard() throws URISyntaxException {
        JsonDeserializationProblemHandler.addPropertyToIgnore(
                "org.cloudfoundry.uaa.identityzones.IdentityZoneConfiguration.Json",
                "newPropertyNotKnownInType",
                "/*/config/newPropertyNotKnownInType");
        URL fileUrl =
                getClass()
                        .getResource("/fixtures/util/AddedProperty_ListIdentityZonesResponse.json");
        Path path = Path.of(fileUrl.toURI());
        body = ByteBufFlux.fromPath(path);
        StepVerifier.create(JsonCodec.decode(objectMapper, body, ListIdentityZonesResponse.class))
                .assertNext(this::checkResponse1)
                .verifyComplete();
    }

    @Test
    void invalidProperyFails() throws URISyntaxException {
        URL fileUrl =
                getClass().getResource("/fixtures/util/PropertyWrongType_ListClientsResponse.json");
        Path path = Path.of(fileUrl.toURI());
        body = ByteBufFlux.fromPath(path);
        StepVerifier.create(JsonCodec.decode(objectMapper, body, ListClientsResponse.class))
                .expectError(JsonParsingException.class)
                .verify(Duration.ofSeconds(2));
    }

    @Test
    void invalidProperyGetsDeleted3() throws URISyntaxException {
        JsonDeserializationProblemHandler.addPropertyToIgnore(
                "java.util.ArrayList<java.lang.String>", "true", "/resources/*/autoapprove/*");
        JsonDeserializationProblemHandler.addPropertyToIgnore(
                "java.util.ArrayList", "autoapprove", "/resources/*/autoapprove/*");
        URL fileUrl =
                getClass().getResource("/fixtures/util/PropertyWrongType_ListClientsResponse.json");
        Path path = Path.of(fileUrl.toURI());
        body = ByteBufFlux.fromPath(path);
        StepVerifier.create(JsonCodec.decode(objectMapper, body, ListClientsResponse.class))
                .assertNext(r -> checkResponse2(r, 1))
                .verifyComplete();
    }

    @Test
    void invalidProperyGetsDeletedWildcardPropertyName() throws URISyntaxException {
        JsonDeserializationProblemHandler.addPropertyToIgnore(
                "java.util.ArrayList<java.lang.String>", "*", "/resources/*/autoapprove");
        JsonDeserializationProblemHandler.addPropertyToIgnore(
                "java.util.ArrayList", "autoapprove", "/resources/*/autoapprove/*");
        URL fileUrl =
                getClass().getResource("/fixtures/util/PropertyWrongType_ListClientsResponse.json");
        Path path = Path.of(fileUrl.toURI());
        body = ByteBufFlux.fromPath(path);
        StepVerifier.create(JsonCodec.decode(objectMapper, body, ListClientsResponse.class))
                .assertNext(r -> checkResponse2(r, 1))
                .verifyComplete();
    }

    @Test
    void invalidProperyWrongTypeGetsDeleted() throws URISyntaxException {
        JsonDeserializationProblemHandler.addPropertyToIgnore(
                "java.util.ArrayList", "autoapprove", "/resources/*/autoapprove");
        URL fileUrl =
                getClass()
                        .getResource("/fixtures/util/PropertyWrongType2_ListClientsResponse.json");
        Path path = Path.of(fileUrl.toURI());
        body = ByteBufFlux.fromPath(path);
        StepVerifier.create(JsonCodec.decode(objectMapper, body, ListClientsResponse.class))
                .assertNext(r -> checkResponse2(r, 1))
                .verifyComplete();
    }

    @Test
    void invalidValueGetsDeleted12() throws URISyntaxException {
        JsonDeserializationProblemHandler.addPropertyToIgnore(
                "org.cloudfoundry.uaa.tokens.GrantType",
                "grant_type_that_is_not_known",
                "/resources/*/authorized_grant_types/*");
        URL fileUrl = getClass().getResource("/fixtures/util/AddedValue_ListClientsResponse.json");
        Path path = Path.of(fileUrl.toURI());
        body = ByteBufFlux.fromPath(path);
        StepVerifier.create(JsonCodec.decode(objectMapper, body, ListClientsResponse.class))
                .assertNext(r -> checkResponse2(r, 1))
                .verifyComplete();
    }

    private void checkResponse1(ListIdentityZonesResponse response) {
        assertEquals(1, response.getIdentityZones().size());
    }

    private void checkResponse2(ListClientsResponse response, int expectedValue) {
        assertEquals(expectedValue, response.getResources().size());
    }

    @Test
    void invalidRootElementFails() throws URISyntaxException {
        JsonDeserializationProblemHandler.addPropertyToIgnore(
                "java.util.LinkedHashMap", "*", "/String");
        JsonDeserializationProblemHandler.addPropertyToIgnore("java.util.LinkedHashMap", "*", "/");
        URL fileUrl = getClass().getResource("/fixtures/util/AddedRoot_ListClientsResponse.json");
        Path path = Path.of(fileUrl.toURI());
        body = ByteBufFlux.fromPath(path);
        StepVerifier.create(JsonCodec.decode(objectMapper, body, Map.class))
                .expectError(JsonParsingException.class)
                .verify(Duration.ofSeconds(2));
    }
}
