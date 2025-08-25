/*
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.cloudfoundry.reactor.tokenprovider;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.IOException;
import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import org.cloudfoundry.reactor.ConnectionContext;
import org.cloudfoundry.reactor.DefaultConnectionContext;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import reactor.netty.http.client.HttpClientForm;
import reactor.netty.http.client.HttpClientRequest;

/**
 * Integration-style tests for AbstractUaaTokenProvider that verify the
 * corrected behavior for concurrent token requests with expired access tokens.
 *
 * These tests verify the fix for issue #1146: "Parallel Requests with Expired
 * Access Tokens triggering Refresh Token Flow leads to Broken State".
 */
class AbstractUaaTokenProviderConcurrencyTest {

    private static final Logger LOGGER =
            LoggerFactory.getLogger(AbstractUaaTokenProviderConcurrencyTest.class);

    private MockUaaServer mockUaaServer;
    private ConnectionContext connectionContext;
    private TestTokenProvider tokenProvider;
    private ExecutorService executorService;

    @BeforeEach
    void setUp() throws IOException {
        mockUaaServer = new MockUaaServer();

        // Extract port from URL like "http://localhost:12345/"
        final String baseUrl = mockUaaServer.getBaseUrl();
        final int port = Integer.parseInt(baseUrl.split(":")[2].split("/")[0]);

        connectionContext =
                DefaultConnectionContext.builder()
                        .apiHost("localhost")
                        .port(port)
                        .secure(false)
                        .cacheDuration(Duration.ofMillis(100)) // Short cache for testing
                        .build();

        tokenProvider = new TestTokenProvider();
        executorService = Executors.newFixedThreadPool(10);
    }

    @AfterEach
    void tearDown() throws IOException {
        if (mockUaaServer != null) {
            mockUaaServer.shutdown();
        }
        if (executorService != null) {
            executorService.shutdown();
        }
    }

    /**
     * Test that concurrent token requests don't cause broken state.
     * This is the main test case for issue #1146.
     */
    @Test
    void concurrentTokenRequestsWithRefreshTokenRotation() throws Exception {
        // Set up initial refresh token
        mockUaaServer.setInitialRefreshToken("initial-refresh-token");

        // Get initial token to establish refresh token
        final String initialToken =
                tokenProvider.getToken(connectionContext).block(Duration.ofSeconds(5));
        assertThat(initialToken).isNotNull();

        // Reset request count to focus on concurrent requests
        mockUaaServer.resetRequestCount();

        // Invalidate the token to force refresh on next request
        tokenProvider.invalidate(connectionContext);

        // Launch multiple concurrent requests
        final int concurrentRequests = 5;
        final CountDownLatch startLatch = new CountDownLatch(1);
        final List<CompletableFuture<String>> futures = new ArrayList<>();

        for (int i = 0; i < concurrentRequests; i++) {
            final CompletableFuture<String> future =
                    CompletableFuture.supplyAsync(
                            () -> {
                                try {
                                    startLatch.await(5, TimeUnit.SECONDS);
                                    return tokenProvider
                                            .getToken(connectionContext)
                                            .block(Duration.ofSeconds(10));
                                } catch (final Exception e) {
                                    LOGGER.error("Error getting token", e);
                                    throw new RuntimeException(e);
                                }
                            },
                            executorService);
            futures.add(future);
        }

        // Start all requests simultaneously
        startLatch.countDown();

        // Wait for all requests to complete
        final List<String> tokens = new ArrayList<>();
        for (final CompletableFuture<String> future : futures) {
            final String token = future.get(15, TimeUnit.SECONDS);
            assertThat(token).isNotNull();
            tokens.add(token);
        }

        // Verify all tokens are valid (not null/empty)
        assertThat(tokens).hasSize(concurrentRequests);
        for (final String token : tokens) {
            assertThat(token).isNotNull().isNotEmpty();
        }

        // Verify that subsequent requests still work (no broken state)
        final String subsequentToken =
                tokenProvider.getToken(connectionContext).block(Duration.ofSeconds(5));
        assertThat(subsequentToken).isNotNull();

        LOGGER.info(
                "Concurrent test completed successfully. Total UAA requests: {}",
                mockUaaServer.getRequestCount());
    }

    /**
     * Test that the token provider handles UAA server errors gracefully during
     * concurrent requests.
     */
    @Test
    void concurrentTokenRequestsWithServerErrors() throws Exception {
        // Set up initial refresh token
        mockUaaServer.setInitialRefreshToken("initial-refresh-token");

        // Get initial token
        final String initialToken =
                tokenProvider.getToken(connectionContext).block(Duration.ofSeconds(5));
        assertThat(initialToken).isNotNull();

        // Configure server to fail the first few refresh requests
        mockUaaServer.setShouldFailRefreshRequests(true, 2);

        // Invalidate to force refresh
        tokenProvider.invalidate(connectionContext);

        // Launch concurrent requests
        final int concurrentRequests = 3;
        final CountDownLatch startLatch = new CountDownLatch(1);
        final List<CompletableFuture<String>> futures = new ArrayList<>();

        for (int i = 0; i < concurrentRequests; i++) {
            final CompletableFuture<String> future =
                    CompletableFuture.supplyAsync(
                            () -> {
                                try {
                                    startLatch.await(5, TimeUnit.SECONDS);
                                    return tokenProvider
                                            .getToken(connectionContext)
                                            .block(Duration.ofSeconds(10));
                                } catch (final Exception e) {
                                    LOGGER.error("Error getting token", e);
                                    return null; // Allow some failures
                                }
                            },
                            executorService);
            futures.add(future);
        }

        startLatch.countDown();

        // Collect results (some may be null due to failures)
        final List<String> tokens = new ArrayList<>();
        for (final CompletableFuture<String> future : futures) {
            final String token = future.get(15, TimeUnit.SECONDS);
            if (token != null) {
                tokens.add(token);
            }
        }

        // At least one request should succeed eventually
        assertThat(tokens).isNotEmpty();

        // Verify system recovers and subsequent requests work
        final String recoveryToken =
                tokenProvider.getToken(connectionContext).block(Duration.ofSeconds(5));
        assertThat(recoveryToken).isNotNull();

        LOGGER.info(
                "Error handling test completed. Successful tokens: {}, Total UAA requests: {}",
                tokens.size(),
                mockUaaServer.getRequestCount());
    }

    /**
     * Test that the token provider properly serializes token requests to prevent
     * multiple concurrent UAA requests with the same refresh token.
     */
    @Test
    void tokenRequestSerialization() throws Exception {
        // Set up initial refresh token
        mockUaaServer.setInitialRefreshToken("initial-refresh-token");

        // Get initial token
        final String initialToken =
                tokenProvider.getToken(connectionContext).block(Duration.ofSeconds(5));
        assertThat(initialToken).isNotNull();

        final int initialRequestCount = mockUaaServer.getRequestCount();

        // Invalidate to force refresh
        tokenProvider.invalidate(connectionContext);

        // Launch many concurrent requests
        final int concurrentRequests = 10;
        final CountDownLatch startLatch = new CountDownLatch(1);
        final List<CompletableFuture<String>> futures = new ArrayList<>();

        for (int i = 0; i < concurrentRequests; i++) {
            final CompletableFuture<String> future =
                    CompletableFuture.supplyAsync(
                            () -> {
                                try {
                                    startLatch.await(5, TimeUnit.SECONDS);
                                    return tokenProvider
                                            .getToken(connectionContext)
                                            .block(Duration.ofSeconds(10));
                                } catch (final Exception e) {
                                    LOGGER.error("Error getting token", e);
                                    throw new RuntimeException(e);
                                }
                            },
                            executorService);
            futures.add(future);
        }

        startLatch.countDown();

        // Wait for all to complete
        for (final CompletableFuture<String> future : futures) {
            final String token = future.get(15, TimeUnit.SECONDS);
            assertThat(token).isNotNull();
        }

        final int finalRequestCount = mockUaaServer.getRequestCount();
        final int newRequests = finalRequestCount - initialRequestCount;

        // The key assertion: despite many concurrent requests, only a minimal number of
        // actual UAA requests should be made due to proper serialization and caching
        assertThat(newRequests).isLessThanOrEqualTo(3); // Allow some margin for timing

        LOGGER.info(
                "Serialization test completed. Concurrent requests: {}, Actual UAA requests: {}",
                concurrentRequests,
                newRequests);
    }

    /**
     * Test token provider implementation for testing purposes.
     */
    private static class TestTokenProvider extends AbstractUaaTokenProvider {

        @Override
        String getIdentityZoneSubdomain() {
            return null;
        }

        @Override
        void tokenRequestTransformer(final HttpClientRequest request, final HttpClientForm form) {
            form.multipart(false)
                    .attr("client_id", getClientId())
                    .attr("client_secret", getClientSecret())
                    .attr("grant_type", "password")
                    .attr("username", "test-user")
                    .attr("password", "test-password");
        }
    }
}
