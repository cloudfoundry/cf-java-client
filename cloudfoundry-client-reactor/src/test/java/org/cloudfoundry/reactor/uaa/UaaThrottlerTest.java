package org.cloudfoundry.reactor.uaa;

import static io.netty.handler.codec.http.HttpMethod.GET;
import static io.netty.handler.codec.http.HttpResponseStatus.OK;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.atLeast;
import static org.mockito.Mockito.never;

import java.time.Duration;
import java.util.Collections;
import org.cloudfoundry.reactor.InteractionContext;
import org.cloudfoundry.reactor.TestRequest;
import org.cloudfoundry.reactor.TestResponse;
import org.cloudfoundry.reactor.uaa.groups.ReactorGroups;
import org.cloudfoundry.uaa.Metadata;
import org.cloudfoundry.uaa.groups.GetGroupRequest;
import org.cloudfoundry.uaa.groups.GetGroupResponse;
import org.cloudfoundry.uaa.groups.MemberSummary;
import org.cloudfoundry.uaa.groups.MemberType;
import org.cloudfoundry.uaa.ratelimit.LimiterMapping;
import org.cloudfoundry.uaa.ratelimit.PathSelector;
import org.cloudfoundry.uaa.ratelimit.PathSelectorModel.PathMatchType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

public class UaaThrottlerTest extends AbstractUaaApiTest {
    private final ReactorGroups groups =
            new ReactorGroups(
                    CONNECTION_CONTEXT, super.root, TOKEN_PROVIDER, Collections.emptyMap());
    private static final Logger LOGGER = LoggerFactory.getLogger("cloudfoundry-client.test");

    @BeforeEach
    void clean() {
        UaaThrottler.reset();
    }

    @Test
    void exhaustUaaThrottlerTest() throws InterruptedException {
        UaaThrottler throttlerSpy = Mockito.spy(UaaThrottler.getInstance());
        UaaThrottler.setInstance(throttlerSpy);
        GetGroupResponse resp1Body = createGroupResponse();
        mockRequestParallel(
                createInteractionContext(
                        "/Groups/test-group-id1", "fixtures/uaa/groups/GET_{id}_response.json"),
                Duration.ofMillis(400));
        mockRequestParallel(
                createInteractionContext(
                        "/Groups/test-group-id2", "fixtures/uaa/groups/GET_{id}_response.json"),
                Duration.ofMillis(200));
        mockRequestParallel(
                createInteractionContext(
                        "/Groups/test-group-id3", "fixtures/uaa/groups/GET_{id}_response.json"),
                Duration.ofMillis(0));
        PathSelector tmp =
                PathSelector.builder().type(PathMatchType.startsWith).path("/Groups").build();
        throttlerSpy.addLimiterMapping(
                LimiterMapping.builder()
                        .timeBase(1)
                        .limit(2)
                        .name("test")
                        .pathSelector(tmp)
                        .build());

        Mono<GetGroupResponse> resp1 =
                groups.get(GetGroupRequest.builder().groupId("test-group-id1").build())
                        .delaySubscription(Duration.ofMillis(0))
                        .doOnSubscribe(s -> LOGGER.trace("Subscribing resp1"))
                        .doOnNext(v -> LOGGER.trace("resp1 emits: " + v));
        Mono<GetGroupResponse> resp2 =
                groups.get(GetGroupRequest.builder().groupId("test-group-id2").build())
                        .delaySubscription(Duration.ofMillis(100))
                        .doOnSubscribe(s -> LOGGER.trace("Subscribing resp2"))
                        .doOnNext(v -> LOGGER.trace("resp2 emits: " + v));
        Mono<GetGroupResponse> resp3 =
                groups.get(GetGroupRequest.builder().groupId("test-group-id3").build())
                        .delaySubscription(Duration.ofMillis(300))
                        .doOnSubscribe(s -> LOGGER.trace("Subscribing resp3"))
                        .doOnNext(v -> LOGGER.trace("resp3 emits: " + v));

        Flux<GetGroupResponse> merged = Flux.merge(resp1, resp2, resp3);

        StepVerifier.create(merged)
                .expectNext(resp1Body)
                .expectNext(resp1Body)
                .expectNext(resp1Body)
                .verifyComplete();
        org.mockito.Mockito.verify(throttlerSpy, atLeast(1)).doDelay(any(), any());
        Thread.sleep(1100);
        throttlerSpy.checkDelayNeededAndResume("/Groups/test-group-id3");
        assertThat(throttlerSpy.verifyAllQueuesEmpty(System.out)).isTrue();
    }

    @Test
    void slowServerDoesNotCreateTest() throws InterruptedException {
        UaaThrottler throttlerSpy = Mockito.spy(UaaThrottler.getInstance());
        UaaThrottler.setInstance(throttlerSpy);
        GetGroupResponse resp1Body = createGroupResponse();
        mockRequestParallel(
                createInteractionContext(
                        "/Groups/test-group-id1", "fixtures/uaa/groups/GET_{id}_response.json"),
                Duration.ofMillis(0));
        mockRequestParallel(
                createInteractionContext(
                        "/Groups/test-group-id2", "fixtures/uaa/groups/GET_{id}_response.json"),
                Duration.ofMillis(0));
        mockRequestParallel(
                createInteractionContext(
                        "/Groups/test-group-id3", "fixtures/uaa/groups/GET_{id}_response.json"),
                Duration.ofMillis(0));
        PathSelector tmp =
                PathSelector.builder().type(PathMatchType.startsWith).path("/Groups").build();
        throttlerSpy.addLimiterMapping(
                LimiterMapping.builder()
                        .timeBase(1)
                        .limit(2)
                        .name("test")
                        .pathSelector(tmp)
                        .build());

        Mono<GetGroupResponse> resp1 =
                groups.get(GetGroupRequest.builder().groupId("test-group-id1").build())
                        .delaySubscription(Duration.ofMillis(0))
                        .doOnSubscribe(s -> LOGGER.trace("Subscribing resp1"))
                        .doOnNext(v -> LOGGER.trace("resp1 emits: " + v));
        Mono<GetGroupResponse> resp2 =
                groups.get(GetGroupRequest.builder().groupId("test-group-id2").build())
                        .delaySubscription(Duration.ofMillis(100))
                        .doOnSubscribe(s -> LOGGER.trace("Subscribing resp2"))
                        .doOnNext(v -> LOGGER.trace("resp2 emits: " + v));
        Mono<GetGroupResponse> resp3 =
                groups.get(GetGroupRequest.builder().groupId("test-group-id3").build())
                        .delaySubscription(Duration.ofMillis(1500))
                        .doOnSubscribe(s -> LOGGER.trace("Subscribing resp3"))
                        .doOnNext(v -> LOGGER.trace("resp3 emits: " + v));

        Flux<GetGroupResponse> merged = Flux.merge(resp1, resp2, resp3);

        StepVerifier.create(merged)
                .expectNext(resp1Body)
                .expectNext(resp1Body)
                .expectNext(resp1Body)
                .verifyComplete();
        org.mockito.Mockito.verify(throttlerSpy, never()).doDelay(any(), any());
    }

    @Test
    void noopWhenNoLimitIsSetTest() throws InterruptedException {
        UaaThrottler throttlerSpy = Mockito.spy(UaaThrottler.getInstance());
        UaaThrottler.setInstance(throttlerSpy);
        GetGroupResponse resp1Body = createGroupResponse();
        mockRequestParallel(
                createInteractionContext(
                        "/Groups/test-group-id1", "fixtures/uaa/groups/GET_{id}_response.json"),
                Duration.ofMillis(0));
        mockRequestParallel(
                createInteractionContext(
                        "/Groups/test-group-id2", "fixtures/uaa/groups/GET_{id}_response.json"),
                Duration.ofMillis(0));
        mockRequestParallel(
                createInteractionContext(
                        "/Groups/test-group-id3", "fixtures/uaa/groups/GET_{id}_response.json"),
                Duration.ofMillis(0));

        Mono<GetGroupResponse> resp1 =
                groups.get(GetGroupRequest.builder().groupId("test-group-id1").build())
                        .doOnSubscribe(s -> LOGGER.trace("Subscribing resp1"))
                        .doOnNext(v -> LOGGER.trace("resp1 emits: " + v));
        Mono<GetGroupResponse> resp2 =
                groups.get(GetGroupRequest.builder().groupId("test-group-id2").build())
                        .doOnSubscribe(s -> LOGGER.trace("Subscribing resp2"))
                        .doOnNext(v -> LOGGER.trace("resp2 emits: " + v));
        Mono<GetGroupResponse> resp3 =
                groups.get(GetGroupRequest.builder().groupId("test-group-id3").build())
                        .doOnSubscribe(s -> LOGGER.trace("Subscribing resp3"))
                        .doOnNext(v -> LOGGER.trace("resp3 emits: " + v));

        Flux<GetGroupResponse> merged = Flux.merge(resp1, resp2, resp3);

        StepVerifier.create(merged)
                .expectNext(resp1Body)
                .expectNext(resp1Body)
                .expectNext(resp1Body)
                .verifyComplete();
        org.mockito.Mockito.verify(throttlerSpy, never()).doDelay(any(), any());
    }

    @Test
    void isDelayNeeded_worksWithSeveralLimiters() {
        UaaThrottler throttler = UaaThrottler.getInstance();
        throttler.addLimiterMapping(
                LimiterMapping.builder()
                        .timeBase(1)
                        .name("test1")
                        .limit(-1)
                        .pathSelector(
                                PathSelector.builder()
                                        .type(PathMatchType.equals)
                                        .path("/SomethingElse")
                                        .build())
                        .build());
        throttler.addLimiterMapping(
                LimiterMapping.builder()
                        .timeBase(1)
                        .name("test2")
                        .limit(-1)
                        .pathSelector(
                                PathSelector.builder()
                                        .type(PathMatchType.startsWith)
                                        .path("/Groups")
                                        .build())
                        .build());
        assertThat(throttler.checkDelayNeededAndResume("/Users")).isFalse();
        assertThat(throttler.checkDelayNeededAndResume("/Groups")).isTrue();
    }

    private GetGroupResponse createGroupResponse() {
        return GetGroupResponse.builder()
                .id("test-group-id")
                .metadata(
                        Metadata.builder()
                                .created("2016-06-03T17:59:30.527Z")
                                .lastModified("2016-06-03T17:59:30.561Z")
                                .version(1)
                                .build())
                .description("the cool group")
                .displayName("Cooler Group Name for Retrieve")
                .member(
                        MemberSummary.builder()
                                .origin("uaa")
                                .type(MemberType.USER)
                                .memberId("f0e6a061-6e3a-4be9-ace5-142ee24e20b7")
                                .build())
                .schema("urn:scim:schemas:core:1.0")
                .zoneId("uaa")
                .build();
    }

    private InteractionContext createInteractionContext(String path, String responseFile) {
        return InteractionContext.builder()
                .request(TestRequest.builder().method(GET).path(path).build())
                .response(TestResponse.builder().status(OK).payload(responseFile).build())
                .build();
    }
}
