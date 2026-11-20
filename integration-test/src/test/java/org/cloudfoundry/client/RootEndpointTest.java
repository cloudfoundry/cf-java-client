package org.cloudfoundry.client;

import static org.assertj.core.api.Assertions.assertThat;

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
                .consumeNextWith(v -> assertThat(v.isHigherThan(Version.of(3))).isTrue())
                .expectComplete()
                .verify(Duration.ofMinutes(1));
    }
}
