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

import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import java.time.Instant;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;
import mockwebserver3.Dispatcher;
import mockwebserver3.MockResponse;
import mockwebserver3.MockWebServer;
import mockwebserver3.RecordedRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Mock UAA server for testing token provider behavior.
 * Simulates UAA refresh token rotation and invalidation behavior.
 */
class MockUaaServer {

    private static final Logger LOGGER = LoggerFactory.getLogger(MockUaaServer.class);

    private final MockWebServer mockWebServer;
    private final ObjectMapper objectMapper;
    private final AtomicReference<String> currentValidRefreshToken;
    private final AtomicInteger requestCount;
    private volatile boolean shouldFailRefreshRequests = false;
    private volatile int failureCount = 0;
    private volatile int maxFailures = 0;

    public MockUaaServer() throws IOException {
        this.mockWebServer = new MockWebServer();
        this.objectMapper = new ObjectMapper();
        this.currentValidRefreshToken = new AtomicReference<>();
        this.requestCount = new AtomicInteger(0);

        // Set up dispatcher to handle token requests
        this.mockWebServer.setDispatcher(new TokenRequestDispatcher());

        this.mockWebServer.start();
    }

    public String getBaseUrl() {
        return mockWebServer.url("/").toString();
    }

    public void setInitialRefreshToken(final String refreshToken) {
        this.currentValidRefreshToken.set(refreshToken);
    }

    public void setShouldFailRefreshRequests(final boolean shouldFail) {
        this.shouldFailRefreshRequests = shouldFail;
        this.failureCount = 0;
    }

    public void setShouldFailRefreshRequests(final boolean shouldFail, final int maxFailures) {
        this.shouldFailRefreshRequests = shouldFail;
        this.maxFailures = maxFailures;
        this.failureCount = 0;
    }

    public int getRequestCount() {
        return requestCount.get();
    }

    public void resetRequestCount() {
        requestCount.set(0);
    }

    public void shutdown() throws IOException {
        mockWebServer.shutdown();
    }

    private MockResponse handleRequest(final RecordedRequest request) throws IOException {
        requestCount.incrementAndGet();

        final String path = request.getPath();
        LOGGER.debug("Received request: {} {}", request.getMethod(), path);

        if ("/v2/info".equals(path)) {
            return handleInfoRequest();
        } else if ("/oauth/token".equals(path)) {
            return handleTokenRequest(request);
        } else {
            LOGGER.debug("Unknown path requested: {}", path);
            return new MockResponse().setResponseCode(404);
        }
    }

    private MockResponse handleTokenRequest(final RecordedRequest request) throws IOException {
        final String requestBody = request.getBody().readUtf8();
        LOGGER.debug("Received token request: {}", requestBody);

        // Check if this is a refresh token request
        if (requestBody.contains("grant_type=refresh_token")) {
            return handleRefreshTokenRequest(requestBody);
        }

        // Handle other grant types (password, client_credentials, etc.)
        return handlePrimaryTokenRequest();
    }

    private MockResponse handleInfoRequest() throws IOException {
        LOGGER.debug("Handling /v2/info request");

        final Map<String, Object> infoResponse = new HashMap<>();
        infoResponse.put("authorization_endpoint", getBaseUrl().replaceAll("/$", ""));
        infoResponse.put("token_endpoint", getBaseUrl().replaceAll("/$", ""));
        infoResponse.put("app_ssh_endpoint", "ssh.localhost:2222");
        infoResponse.put("app_ssh_host_key_fingerprint", "test-fingerprint");
        infoResponse.put("api_version", "2.165.0");
        infoResponse.put("name", "test-cf");
        infoResponse.put("build", "test-build");
        infoResponse.put("version", 0);
        infoResponse.put("description", "Unit Test Cloud Foundry");

        final String responseBody = objectMapper.writeValueAsString(infoResponse);
        LOGGER.debug("Generated info response: {}", responseBody);

        return new MockResponse()
                .setResponseCode(200)
                .setHeader("Content-Type", "application/json")
                .setBody(responseBody);
    }

    /* Warning! synchronized is really necessary here to ensure no concurrent threads are trying to run into a refresh token handling! */
    private synchronized MockResponse handleRefreshTokenRequest(final String requestBody)
            throws IOException {
        // Check if we should fail requests
        if (shouldFailRefreshRequests) {
            if (maxFailures == 0 || failureCount < maxFailures) {
                failureCount++;
                LOGGER.debug(
                        "Failing refresh token request (failure {} of {})",
                        failureCount,
                        maxFailures);
                return new MockResponse()
                        .setResponseCode(401)
                        .setBody(
                                "{\"error\":\"invalid_grant\",\"error_description\":\"Invalid"
                                        + " refresh token\"}");
            } else {
                // Reset failure mode after max failures reached
                shouldFailRefreshRequests = false;
            }
        }

        // Extract refresh token from request body
        final String refreshTokenFromRequest = extractRefreshTokenFromRequest(requestBody);
        final String currentValid = currentValidRefreshToken.get();

        if (currentValid != null && !currentValid.equals(refreshTokenFromRequest)) {
            LOGGER.debug(
                    "Invalid refresh token provided. Expected: {}, Got: {}",
                    currentValid,
                    refreshTokenFromRequest);
            return new MockResponse()
                    .setResponseCode(401)
                    .setBody(
                            "{\"error\":\"invalid_grant\",\"error_description\":\"Invalid refresh"
                                    + " token\"}");
        }

        // Generate new tokens
        return generateTokenResponse();
    }

    private MockResponse handlePrimaryTokenRequest() throws IOException {
        // For primary token requests (password grant, etc.), always succeed
        return generateTokenResponse();
    }

    private MockResponse generateTokenResponse() throws IOException {
        final long now = Instant.now().getEpochSecond();
        final long expiresAt = now + 3600; // 1 hour from now

        // Create a simple JWT-like token (not cryptographically valid, just for testing)
        final String header = Base64.getEncoder().encodeToString("{\"alg\":\"none\"}".getBytes());
        final String payload =
                Base64.getEncoder()
                        .encodeToString(
                                String.format(
                                                "{\"exp\":%d,\"iat\":%d,\"sub\":\"test-user\"}",
                                                expiresAt, now)
                                        .getBytes());
        final String accessToken = header + "." + payload + ".";

        // Generate new refresh token
        final String newRefreshToken = "refresh-token-" + System.nanoTime();
        currentValidRefreshToken.set(newRefreshToken);

        final Map<String, Object> response = new HashMap<>();
        response.put("access_token", accessToken);
        response.put("token_type", "Bearer");
        response.put("expires_in", 3600);
        response.put("refresh_token", newRefreshToken);
        response.put("scope", "openid");

        final String responseBody = objectMapper.writeValueAsString(response);
        LOGGER.debug("Generated token response with refresh token: {}", newRefreshToken);

        return new MockResponse()
                .setResponseCode(200)
                .setHeader("Content-Type", "application/json")
                .setBody(responseBody);
    }

    private String extractRefreshTokenFromRequest(final String requestBody) {
        // Simple extraction of refresh_token parameter
        final String[] parts = requestBody.split("&");
        for (final String part : parts) {
            if (part.startsWith("refresh_token=")) {
                return part.substring("refresh_token=".length());
            }
        }
        return null;
    }

    private class TokenRequestDispatcher extends Dispatcher {
        @Override
        public MockResponse dispatch(final RecordedRequest request) {
            try {
                return handleRequest(request);
            } catch (final Exception e) {
                LOGGER.error("Error handling request", e);
                return new MockResponse().setResponseCode(500);
            }
        }
    }
}
