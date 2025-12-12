package org.cloudfoundry.uaa;

import java.time.Duration;
import org.cloudfoundry.reactor.uaa.UaaThrottler;
import org.cloudfoundry.uaa.ratelimit.Current;
import org.cloudfoundry.uaa.ratelimit.LimiterMapping;
import org.cloudfoundry.uaa.ratelimit.PathSelector;
import org.cloudfoundry.uaa.ratelimit.PathSelectorModel.PathMatchType;
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
    private Integer environmentRequestlimit;

    public UaaRatelimitInitializer(Ratelimit ratelimitService, Integer envRequestLimit) {
        this.ratelimitService = ratelimitService;
        this.environmentRequestlimit = envRequestLimit;
    }

    private void init() {
        int limit = 0;

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
                            return Mono.just(false);
                        })
                .block();

        if (environmentRequestlimit != null) {
            limit = environmentRequestlimit.intValue();
            logger.debug("UaaRatelimitInitializer using configured value " + limit);
        }

        LimiterMapping tmp =
                LimiterMapping.builder()
                        .limit(limit)
                        .name("Test")
                        .timeBase(1)
                        .pathSelector(
                                PathSelector.builder().path("").type(PathMatchType.other).build())
                        .build();
        UaaThrottler.getInstance().addLimiterMapping(tmp);
    }

    private Boolean getServerRatelimit(RatelimitResponse response) {
        Current curr = response.getCurrentData();
        if (!"ACTIVE".equals(curr.getStatus())) {
            logger.debug(
                    "UaaRatelimitInitializer server ratelimit is not 'ACTIVE', but "
                            + curr.getStatus()
                            + ". Ignoring server value for ratelimit.");
            return false;
        }
        Integer result = curr.getLimiterMappings();
        logger.info(
                "Server uses uaa rate limiting. There are "
                        + result
                        + " mappings declared in file "
                        + response.getFromSource());
        logger.info(
                "If you encounter 429 return codes, configure uaa rate limiting or set variable"
                        + " 'UAA_API_REQUEST_LIMIT' to a save value.");
        return true;
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        this.init();
    }
}
