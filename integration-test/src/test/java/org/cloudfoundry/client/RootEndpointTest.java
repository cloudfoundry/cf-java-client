package org.cloudfoundry.client;

import com.github.zafarkhaja.semver.Version;
import java.time.Duration;
import org.cloudfoundry.AbstractIntegrationTest;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import reactor.test.StepVerifier;

class RootEndpointTest extends AbstractIntegrationTest {

    @Qualifier("cloudFoundryClient")
    @Autowired
    CloudFoundryClient client;

    @Test
    void rootVersion() {
        client.rootEndpoint()
                .get(GetRootRequest.builder().build())
                .map(GetRootResponse::getApiVersionV3)
                .map(Version::parse)
                .as(StepVerifier::create)
                .consumeNextWith(v -> v.isHigherThan(Version.of(3)))
                .expectComplete()
                .verify(Duration.ofMinutes(1));
    }
}
