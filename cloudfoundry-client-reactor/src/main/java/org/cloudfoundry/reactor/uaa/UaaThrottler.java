package org.cloudfoundry.reactor.uaa;

import java.time.Duration;
import java.util.Objects;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Consumer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import reactor.core.publisher.Mono;
import reactor.core.publisher.Sinks;

/**
 * Throttle calls to uaa in order to avoid running into a rate limit.
 * If your UAA server is configured with a rate limit, the number of allowed parallel requests
 * must be set here in order to slow down the client and avoid http 429-responses.
 *
 * @author D034003
 *
 */
public class UaaThrottler {
    public static final ResourceToken NON_UAA_TOKEN = ResourceToken.empty();
    private static UaaThrottler instance = null;
    private static int maxDelay = 8;
    private static int maxResources = 0;
    private AtomicInteger inUse;
    private Queue<SinkWithId> waitingQueue;

    private static final Logger LOGGER = LoggerFactory.getLogger("cloudfoundry-client.test");

    private static Consumer<TimeoutException> timeoutHandler = ex -> ex.printStackTrace();

    public static UaaThrottler getInstance() {
        if (instance == null) {
            instance = new UaaThrottler();
        }
        return instance;
    }

    private UaaThrottler() {
        inUse = new AtomicInteger(0);
        waitingQueue = new ConcurrentLinkedQueue<>();
    }

    /**
     * This method should only be used to get a defined state in test coding.
     */
    public static void reset() {
        instance = null;
    }

    private String logString() {
        return "maxResources "
                + maxResources
                + " inUse "
                + inUse.get()
                + " queue "
                + waitingQueue.size();
    }

    /**
     * Here the number of parallel requests can be configured. Must be less than the value returned by
     * the `RateLimitingStatus` endpoint of your UAA.
     * Default is `0`, meaning no limit.
     * @param rateLimit
     */
    public static void setUaaRateLimit(int rateLimit) {
        maxResources = rateLimit;
    }

    /**
     * Configure the time in seconds until a request is regarded to be lost. After this time, requests will not be canceled or aborted, but they will not count to the limit and new requests may start.
     *
     * @param max time in seconds. Default is 8 seconds.
     */
    public static void setMaxDelay(int max) {
        maxDelay = max;
    }

    public Mono<ResourceToken> acquire(String url) {
        if ((maxResources > 0)) {
            LOGGER.debug(
                    "UaaThrottler about to acquire one token "
                            + this.logString()
                            + " for url "
                            + url);
            return Mono.defer(
                            () -> {
                                if (inUse.incrementAndGet() < maxResources) {
                                    // Slot available
                                    ResourceToken token =
                                            new ResourceToken(
                                                    url,
                                                    timeoutHandler,
                                                    new TimeoutException(
                                                            "Requests block each other and time"
                                                                    + " out"));
                                    LOGGER.debug(
                                            "UaaThrottler created one token "
                                                    + this.logString()
                                                    + " for url "
                                                    + url
                                                    + " token "
                                                    + token);
                                    return Mono.just(token);
                                } else {
                                    Sinks.One<ResourceToken> sink = Sinks.one();
                                    waitingQueue.offer(new SinkWithId(url, sink));
                                    LOGGER.debug(
                                            "UaaThrottler created sink "
                                                    + this.logString()
                                                    + " for url "
                                                    + url);
                                    return sink.asMono();
                                }
                            })
                    .cache();
        } else {
            return Mono.just(NON_UAA_TOKEN);
        }
    }

    private void release(ResourceToken token) {
        String url = token.id;
        if ((maxResources > 0)) {
            LOGGER.debug(
                    "UaaThrottler about to release token "
                            + this.logString()
                            + " released "
                            + token.released.get()
                            + " for url "
                            + url
                            + " token "
                            + token);
            SinkWithId urlSink = waitingQueue.poll();
            if (urlSink != null) {
                Mono.delay(Duration.ofMillis(1))
                        .subscribe(
                                __ -> {
                                    ResourceToken newToken =
                                            new ResourceToken(
                                                    urlSink.id,
                                                    timeoutHandler,
                                                    new TimeoutException(
                                                            "Requests block each other and time"
                                                                    + " out"));
                                    LOGGER.debug(
                                            "UaaThrottler releasing sink and creating token "
                                                    + this.logString()
                                                    + " released "
                                                    + token.released.get()
                                                    + " oldUrl "
                                                    + token.id
                                                    + " newUrl "
                                                    + newToken.id
                                                    + " oldToken "
                                                    + token
                                                    + " newToken "
                                                    + newToken);
                                    inUse.decrementAndGet();
                                    urlSink.sink.tryEmitValue(newToken);
                                });
            } else {
                inUse.decrementAndGet();
                LOGGER.debug(
                        "UaThrottler completed release. "
                                + this.logString()
                                + " for url "
                                + url
                                + " token"
                                + token);
            }
        }
    }

    /**
     * By default, a message and callstack is written to stderr in case a request does not return within maxDelay seconds.
     * Here different behavior can be configured.
     *
     * @param timeoutHandler
     */
    public static void setTimeoutHandler(Consumer<TimeoutException> timeoutHandler) {
        UaaThrottler.timeoutHandler = Objects.requireNonNull(timeoutHandler);
    }

    public static class ResourceToken {
        private final String id;
        private final ScheduledFuture<?> leakTask;
        private final AtomicBoolean released = new AtomicBoolean(false);
        private UaaThrottler instance;

        private ResourceToken(
                String id,
                Consumer<TimeoutException> timeoutHandler,
                TimeoutException tokenAquiredAt) {
            this.id = id;
            this.instance = UaaThrottler.instance;
            if (timeoutHandler == null) { // this is the null-object
                this.leakTask = null;
            } else {
                this.leakTask =
                        Executors.newSingleThreadScheduledExecutor()
                                .schedule(
                                        () -> {
                                            if (!released.get()) {
                                                LOGGER.error(
                                                        "[LEAK DETECTED] UaaThrottler.ResourceToken"
                                                                + " was not released within "
                                                                + maxDelay
                                                                + " seconds "
                                                                + this.id
                                                                + " token "
                                                                + this);
                                                this.release();
                                                timeoutHandler.accept(tokenAquiredAt);
                                            }
                                        },
                                        maxDelay,
                                        TimeUnit.SECONDS);
            }
        }

        private static ResourceToken empty() {
            return new ResourceToken(null, null, null);
        }

        public void release() {
            if (this != NON_UAA_TOKEN) {
                if (released.compareAndSet(false, true)) {
                    leakTask.cancel(true);
                    Mono.delay(Duration.ofMillis(1))
                            .doOnNext(__ -> this.instance.release(this))
                            .subscribe();
                } else {
                    // If a request times out, the token gets released. When the response comes
                    // later, the token is already released and must not be released a second time.
                }
            }
        }

        public String id() {
            return id;
        }
    }

    private class SinkWithId {
        final String id;
        final Sinks.One<ResourceToken> sink;

        private SinkWithId(String idForLogmessages, Sinks.One<ResourceToken> sink) {
            this.id = idForLogmessages;
            this.sink = sink;
        }
    }
}
