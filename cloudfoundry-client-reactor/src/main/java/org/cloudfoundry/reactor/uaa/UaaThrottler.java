package org.cloudfoundry.reactor.uaa;

import java.io.PrintStream;
import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Queue;
import java.util.Set;
import java.util.concurrent.ConcurrentLinkedQueue;
import org.cloudfoundry.uaa.ratelimit.LimiterMapping;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import reactor.core.publisher.Mono;
import reactor.core.publisher.Sinks;
import reactor.core.scheduler.Schedulers;

/**
 * Throttle calls to uaa in order to avoid running into a rate limit.
 * If your UAA server is configured with a rate limit, the number of request within a given time
 * must be set here in order to slow down the client and avoid http 429-responses.
 *
 * @author D034003
 *
 */
public class UaaThrottler {
    public static Token NON_UAA_TOKEN;
    private static UaaThrottler instance = null;

    // List of all rate limits. Each url may have a different value. For each limit, the list of
    // calls that are not outdated is kept.
    private List<LimiterMappingWithRunningRequests> mappings;

    // All requests that must not run now because they would violate a limit.
    private Queue<Sinks.One<Void>> waitingQueue;

    // Time base of the limits in seconds.
    private int minDelay;

    private static final Logger LOGGER = LoggerFactory.getLogger("cloudfoundry-client.test");

    public static UaaThrottler getInstance() {
        if (instance == null) {
            instance = new UaaThrottler();
        }
        return instance;
    }

    private UaaThrottler() {
        NON_UAA_TOKEN = Token.empty();
        mappings = new ArrayList<>();
        waitingQueue = new ConcurrentLinkedQueue<>();
        minDelay = Integer.MAX_VALUE;
    }

    /**
     * Add one entry for every mapping configured on the server.
     * @param mapping
     */
    public void addLimiterMapping(LimiterMapping mapping) {
        mappings.add(new LimiterMappingWithRunningRequests(mapping));
        int delay = mapping.timeBase();
        if (minDelay > delay) {
            minDelay = delay;
        }
    }

    public Mono<Token> acquire(String url) {
        if ((mappings.size() > 0)) {
            LOGGER.trace(
                    "UaaThrottler: about to acquire one token "
                            + this.logString()
                            + " for url "
                            + url);
            return Mono.defer(
                            () -> {
                                Sinks.One<Void> waiter = Sinks.one();
                                waitingQueue.add(waiter); // first add, so no signal get's lost.
                                return tryAcquire(url, waiter);
                            })
                    .subscribeOn(Schedulers.parallel());
        } else {
            return Mono.just(NON_UAA_TOKEN);
        }
    }

    private synchronized Mono<Token> tryAcquire(String url, Sinks.One<Void> waiter) {
        if (checkDelayNeededAndResume(url)) {
            // LOGGER.debug("UaaThrottler: Delay needed for "+url+", "+ this.logString());
            // too much noise
            return doDelay(url, waiter);
        } else {
            waitingQueue.remove(waiter); // no need to wait, so delete the waiter.
            // LOGGER.debug("UaaThrottler: no Delay needed for "+ url);  too much noise
            Token token = new Token(url);
            for (LimiterMappingWithRunningRequests mapping : mappings) {
                if (mapping.limiter.matches(url)) {
                    mapping.runningRequests.add(token);
                }
            }
            return Mono.just(token);
        }
    }

    /**
     * check if all windows in all mappers are below limit and return that value.
     * As a side effect, clean up windows and trigger resume of waiting requests.
     *
     * @param url
     * @return
     */
    synchronized boolean checkDelayNeededAndResume(String url) {
        Instant now = Instant.now();
        Set<Token> removed = new HashSet<>();
        try {
            for (LimiterMappingWithRunningRequests mapping : mappings) {
                if (mapping.limiter.matches(url)) {
                    removed.addAll(clearOutdatedTokens(mapping, now));
                    if (mapping.runningRequests.size() >= mapping.limiter.limit()) {
                        return true;
                    }
                }
            }
        } finally {
            checkIfResumeIsPossible(removed);
        }
        return false;
    }

    /**
     * explicit method to allow unit testing. Do not use.
     */
    Mono<Token> doDelay(String url, Sinks.One<Void> waiter) {
        return Mono.delay(Duration.ofMillis(minDelay))
                .then(Mono.defer(() -> tryAcquire(url, waiter)));
    }

    private Set<Token> clearOutdatedTokens(LimiterMappingWithRunningRequests mapping, Instant now) {
        int window = mapping.limiter.timeBase();
        Instant windowStart = now.minus(Duration.ofSeconds(window));
        Iterator<Token> it = mapping.runningRequests.iterator();
        Set<Token> removed = new HashSet<>();
        while (it.hasNext()) {
            Token token = it.next();
            if (token.startTime != null && token.startTime.isBefore(windowStart)) {
                removed.add(token);
                it.remove();
            } else {
                // not started or still counting, do nothing
            }
        }
        return removed;
    }

    // if a token that is outdated in one mapping is not listed in any of the other mapping-windows,
    // we can resume.
    private void checkIfResumeIsPossible(Set<Token> removed) {
        for (Token oneToken : removed) {
            boolean isUsed = false;
            for (LimiterMappingWithRunningRequests mapping : mappings) {
                if (mapping.runningRequests.contains(oneToken)) {
                    isUsed = true;
                    break;
                }
            }
            if (!isUsed) {
                resume(oneToken.id);
            }
        }
    }

    private void resume(String url) {
        Sinks.One<Void> next = waitingQueue.poll();
        LOGGER.trace("UaaThrottler: Releasing because " + url + " is out of window.");
        if (next != null) {
            LOGGER.trace("UaaThrottler: waking up " + next.name());
            next.tryEmitEmpty(); // wake up one waiting caller
        } else {
            LOGGER.trace("UaaThrottler: waiting queue is empty");
        }
    }

    private String logString() {
        String mappingValues = "";
        for (LimiterMappingWithRunningRequests mapping : mappings) {
            mappingValues +=
                    "mapping limiter "
                            + mapping.limiter.name()
                            + " limit "
                            + mapping.limiter.limit()
                            + "/"
                            + mapping.limiter.timeBase()
                            + "s for path "
                            + mapping.limiter.pathSelectors()
                            + "; ";
        }
        return "QueueLenght "
                + waitingQueue.size()
                + " number of mappings "
                + mappings.size()
                + mappingValues;
    }

    /**
     * This method should only be used to get a defined state in test coding.
     */
    static void reset() {
        instance = null;
    }

    /** only for unit testing.
     *
     * @param spy
     */
    static void setInstance(UaaThrottler spy) {
        instance = spy;
    }

    /**
     * Only for unittests.
     * @param out
     * @return
     */
    boolean verifyAllQueuesEmpty(PrintStream out) {
        boolean result = true;
        if (waitingQueue.size() > 0) {
            result = false;
            waitingQueue.forEach(
                    sink -> {
                        out.println("Sinks entry is left over");
                    });
        }
        for (LimiterMappingWithRunningRequests mapping : mappings) {
            if (mapping.runningRequests.size() > 0) {
                out.println(
                        "Mapping for "
                                + mapping.limiter.name()
                                + " is not 0 as expected, but "
                                + mapping.runningRequests.size());
                result = false;
            }
        }
        return result;
    }

    public static class Token {
        private final String id;
        private Instant startTime;

        private Token(String url) {
            this.id = url;
        }

        private static Token empty() {
            return new Token(null);
        }

        public void activate() {
            if (this != NON_UAA_TOKEN) {
                startTime = Instant.now();
            }
        }

        public String id() {
            return id;
        }
    }

    private static class LimiterMappingWithRunningRequests {
        private LimiterMapping limiter;
        private Set<Token> runningRequests;

        private LimiterMappingWithRunningRequests(LimiterMapping mapping) {
            limiter = mapping;
            runningRequests = new HashSet<>();
        }
    }
}
