/*
 * Copyright 2013-2015 the original author or authors.
 *
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

package org.cloudfoundry.client.spring;

import org.cloudfoundry.client.spring.loggregator.LoggregatorMessageHttpMessageConverter;
import org.cloudfoundry.client.spring.util.FallbackHttpMessageConverter;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.security.oauth2.client.DefaultOAuth2ClientContext;
import org.springframework.security.oauth2.client.OAuth2RestTemplate;
import org.springframework.security.oauth2.client.token.grant.client.ClientCredentialsResourceDetails;
import org.springframework.security.oauth2.common.DefaultOAuth2AccessToken;
import org.springframework.test.web.client.MockRestServiceServer;
import org.springframework.test.web.client.RequestMatcher;
import org.springframework.test.web.client.ResponseActions;
import org.springframework.test.web.client.ResponseCreator;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static com.fasterxml.jackson.annotation.JsonInclude.Include.NON_NULL;
import static org.springframework.http.HttpStatus.UNPROCESSABLE_ENTITY;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.content;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.method;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.requestTo;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withStatus;

public abstract class AbstractRestTest {

    protected final OAuth2RestTemplate restTemplate = new OAuth2RestTemplate(new ClientCredentialsResourceDetails(),
            new DefaultOAuth2ClientContext(new DefaultOAuth2AccessToken("test-access-token")));

    protected final URI root = UriComponentsBuilder.newInstance()
            .scheme("https").host("api.run.pivotal.io")
            .build().toUri();

    private final MockRestServiceServer mockServer = MockRestServiceServer.createServer(this.restTemplate);

    {
        List<HttpMessageConverter<?>> messageConverters = this.restTemplate.getMessageConverters();

        messageConverters.stream()
                .filter(converter -> converter instanceof MappingJackson2HttpMessageConverter)
                .map(converter -> (MappingJackson2HttpMessageConverter) converter)
                .findFirst()
                .ifPresent(converter -> {
                    converter.getObjectMapper()
                            .setSerializationInclusion(NON_NULL);
                });

        messageConverters.add(new LoggregatorMessageHttpMessageConverter());
        messageConverters.add(new FallbackHttpMessageConverter());
    }

    protected final void mockRequest(RequestContext requestContext) {
        HttpMethod method = requestContext.getMethod()
                .orElseThrow(() -> new IllegalStateException("method must be set"));

        String uri = requestContext.getPath()
                .map(path -> UriComponentsBuilder.fromUri(this.root).path(path).build(false).toString())
                .orElseThrow(() -> new IllegalStateException("path must be set"));

        ResponseActions responseActions = this.mockServer
                .expect(method(method))
                .andExpect(requestTo(uri));

        if (!requestContext.getAnyRequestPayload()) {
            RequestMatcher payloadMatcher = requestContext.getRequestPayload()
                    .map(ContentMatchers::jsonPayload)
                    .orElse(content().string(""));

            responseActions = responseActions.andExpect(payloadMatcher);
        }

        for (RequestMatcher requestMatcher : requestContext.getRequestMatchers()) {
            responseActions = responseActions.andExpect(requestMatcher);
        }

        HttpStatus status = requestContext.getStatus()
                .orElseThrow(() -> new IllegalStateException("status must be set"));

        MediaType contentType = requestContext.getContentType()
                .orElse(APPLICATION_JSON);

        ResponseCreator responseCreator = requestContext.getResponsePayload()
                .map(resource -> withStatus(status).contentType(contentType).body(resource))
                .orElse(withStatus(status));

        responseActions.andRespond(responseCreator);
    }

    protected final void verify() {
        this.mockServer.verify();
    }

    public static final class RequestContext {

        private final List<RequestMatcher> requestMatchers = new ArrayList<>();

        private volatile boolean anyRequestPayload = false;

        private volatile Optional<MediaType> contentType = Optional.empty();

        private volatile Optional<HttpMethod> method = Optional.empty();

        private volatile Optional<String> path = Optional.empty();

        private volatile Optional<Resource> requestPayload = Optional.empty();

        private volatile Optional<Resource> responsePayload = Optional.empty();

        private volatile Optional<HttpStatus> status = Optional.empty();

        public RequestContext anyRequestPayload() {
            this.anyRequestPayload = true;
            return this;
        }

        public RequestContext contentType(MediaType contentType) {
            this.contentType = Optional.of(contentType);
            return this;
        }

        public RequestContext errorResponse() {
            status(UNPROCESSABLE_ENTITY);
            responsePayload("v2/error_response.json");
            return this;
        }

        public RequestContext method(HttpMethod method) {
            this.method = Optional.of(method);
            return this;
        }

        public RequestContext path(String path) {
            this.path = Optional.of(path);
            return this;
        }

        public RequestContext requestMatcher(RequestMatcher requestMatcher) {
            this.requestMatchers.add(requestMatcher);
            return this;
        }

        public RequestContext requestPayload(String path) {
            this.requestPayload = Optional.of(path).map(ClassPathResource::new);
            return this;
        }

        public RequestContext responsePayload(String path) {
            this.responsePayload = Optional.of(path).map(ClassPathResource::new);
            return this;
        }

        public RequestContext status(HttpStatus status) {
            this.status = Optional.of(status);
            return this;
        }

        Boolean getAnyRequestPayload() {
            return this.anyRequestPayload;
        }

        Optional<MediaType> getContentType() {
            return this.contentType;
        }

        Optional<HttpMethod> getMethod() {
            return this.method;
        }

        Optional<String> getPath() {
            return this.path;
        }

        List<RequestMatcher> getRequestMatchers() {
            return this.requestMatchers;
        }

        Optional<Resource> getRequestPayload() {
            return this.requestPayload;
        }

        Optional<Resource> getResponsePayload() {
            return this.responsePayload;
        }

        Optional<HttpStatus> getStatus() {
            return this.status;
        }

    }

}
