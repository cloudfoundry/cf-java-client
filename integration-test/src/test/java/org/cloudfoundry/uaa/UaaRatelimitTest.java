package org.cloudfoundry.uaa;

import java.time.Duration;
import org.cloudfoundry.ThrottlingUaaClient;
import org.cloudfoundry.uaa.ratelimit.Current;
import org.cloudfoundry.uaa.ratelimit.RatelimitRequest;
import org.cloudfoundry.uaa.ratelimit.RatelimitResponse;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

@SpringJUnitConfig(classes = RatelimitTestConfiguration.class)
public class UaaRatelimitTest {
    private static final Logger LOGGER = LoggerFactory.getLogger(UaaRatelimitTest.class);

    @Autowired private UaaClient adminUaaClient;

    @Test
    public void getRatelimit() {
        int envRatelimit;
        if (adminUaaClient instanceof ThrottlingUaaClient) {
            ThrottlingUaaClient throttlingClient = (ThrottlingUaaClient) adminUaaClient;
            envRatelimit = throttlingClient.getMaxRequestsPerSecond();
        } else {
            envRatelimit = 0;
        }
        Mono<Boolean> tmp =
                adminUaaClient
                        .rateLimit()
                        .getRatelimit(RatelimitRequest.builder().build())
                        .map(response -> getServerRatelimit(response, envRatelimit))
                        .timeout(Duration.ofSeconds(5))
                        .onErrorResume(
                                ex -> {
                                    LOGGER.error(
                                            "Warning: could not fetch UAA rate limit, using default"
                                                    + " "
                                                    + 0
                                                    + ". Cause: "
                                                    + ex);
                                    return Mono.just(false);
                                });
        StepVerifier.create(tmp.materialize()).expectNextCount(1).verifyComplete();
    }

    private Boolean getServerRatelimit(RatelimitResponse response, int maxRequestsPerSecond) {
        Current curr = response.getCurrentData();
        if (!"ACTIVE".equals(curr.getStatus())) {
            LOGGER.debug(
                    "UaaRatelimitInitializer server ratelimit is not 'ACTIVE', but "
                            + curr.getStatus()
                            + ". Ignoring server value for ratelimit.");
            return false;
        }
        Integer result = curr.getLimiterMappings();
        LOGGER.info(
                "Server uses uaa rate limiting. There are "
                        + result
                        + " mappings declared in file "
                        + response.getFromSource());
        if (maxRequestsPerSecond == 0) {
            LOGGER.warn(
                    "Ratelimiting is not set locally, set variable 'UAA_API_REQUEST_LIMIT' to a"
                            + " save value or you might experience http 429 responses.");
        }
        return true;
    }
}
