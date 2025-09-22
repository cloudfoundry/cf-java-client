package org.cloudfoundry.uaa;

import java.time.Duration;
import org.cloudfoundry.reactor.uaa.UaaThrottler;
import org.cloudfoundry.uaa.ratelimit.Current;
import org.cloudfoundry.uaa.ratelimit.Ratelimit;
import org.cloudfoundry.uaa.ratelimit.RatelimitRequest;
import org.cloudfoundry.uaa.ratelimit.RatelimitResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

@Component
public class UaaRatelimitInitializer implements InitializingBean {
    private final Logger logger = LoggerFactory.getLogger("cloudfoundry-client.test");

    private final Ratelimit ratelimitService;
    private Integer commandlineRequestlimit;

    public UaaRatelimitInitializer(Ratelimit ratelimitService, Integer commandlineRequestLimit) {
        this.ratelimitService = ratelimitService;
        this.commandlineRequestlimit = commandlineRequestLimit;
    }

    private void init() {
        int limit = 0;

        Integer serverRatelimit =
                ratelimitService
                        .getRatelimit(RatelimitRequest.builder().build())
                        .map(response -> getServerRatelimit(response))
                        .timeout(Duration.ofSeconds(5))
                        .onErrorResume(
                                ex -> {
                                    logger.error(
                                            "Warning: could not fetch UAA rate limit, using default"
                                                    + " "
                                                    + 0
                                                    + ". Cause: "
                                                    + ex);
                                    return Mono.just(0);
                                })
                        .block();

        if (serverRatelimit != null) {
            limit = serverRatelimit.intValue();
        }
        if (commandlineRequestlimit != null) {
            limit = commandlineRequestlimit.intValue();
            logger.debug("UaaRatelimitInitializer using configured value " + limit);
        }

        UaaThrottler.setUaaRateLimit(limit);
    }

    private Integer getServerRatelimit(RatelimitResponse response) {
        Current curr = response.getCurrentData();
        if (!"ACTIVE".equals(curr.getStatus())) {
            logger.debug(
                    "UaaRatelimitInitializer server ratelimit is not 'ACTIVE', but "
                            + curr.getStatus()
                            + ". Ignoring server value for ratelimit.");
            return null;
        }
        Integer result = curr.getLimit() - 1; // using the value returned from server
        // will not stop the 429-Errors. Decreased value is safe.
        logger.debug(
                "UaaRatelimitInitializer using server value for ratelimit -1, resulting in "
                        + result);
        return result;
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        this.init();
    }
}
