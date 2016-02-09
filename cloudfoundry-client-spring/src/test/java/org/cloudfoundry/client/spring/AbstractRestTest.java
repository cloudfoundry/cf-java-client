/*
 * Copyright 2013-2016 the original author or authors.
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

import lombok.Getter;
import org.cloudfoundry.client.spring.logging.LoggregatorMessageHttpMessageConverter;
import org.cloudfoundry.client.spring.util.FallbackHttpMessageConverter;
import org.cloudfoundry.client.spring.util.LoggingClientHttpRequestInterceptor;
import org.cloudfoundry.utils.test.FailingDeserializationProblemHandler;
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
import org.springframework.util.Assert;
import org.springframework.web.util.UriComponentsBuilder;
import reactor.core.publisher.SchedulerGroup;
import reactor.core.util.PlatformDependent;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;

import static com.fasterxml.jackson.annotation.JsonInclude.Include.NON_NULL;
import static org.springframework.http.HttpStatus.UNPROCESSABLE_ENTITY;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.content;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.method;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.requestTo;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withStatus;

public abstract class AbstractRestTest {

    protected static final SchedulerGroup PROCESSOR_GROUP = SchedulerGroup.io("cloudfoundry-client-spring", PlatformDependent.MEDIUM_BUFFER_SIZE, SchedulerGroup.DEFAULT_POOL_SIZE, false);

    protected final OAuth2RestTemplate restTemplate = new OAuth2RestTemplate(new ClientCredentialsResourceDetails(), new DefaultOAuth2ClientContext(new DefaultOAuth2AccessToken("test-access-token")));

    {
        this.restTemplate.getInterceptors().add(new LoggingClientHttpRequestInterceptor());
    }

    protected final URI root = UriComponentsBuilder.newInstance()
        .scheme("https").host("api.run.pivotal.io")
        .build().toUri();

    private final MockRestServiceServer mockServer = MockRestServiceServer.createServer(this.restTemplate);

    {
        List<HttpMessageConverter<?>> messageConverters = this.restTemplate.getMessageConverters();
        for (HttpMessageConverter<?> messageConverter : messageConverters) {
            if (messageConverter instanceof MappingJackson2HttpMessageConverter) {
                ((MappingJackson2HttpMessageConverter) messageConverter).getObjectMapper()
                    .setSerializationInclusion(NON_NULL)
                    .addHandler(new FailingDeserializationProblemHandler());
            }
        }

        messageConverters.add(new LoggregatorMessageHttpMessageConverter());
        messageConverters.add(new FallbackHttpMessageConverter());
    }

    protected final void mockRequest(RequestContext requestContext) {
        HttpMethod method = requestContext.getMethod();
        Assert.notNull(method, "method must be set");

        Assert.notNull(requestContext.getPath(), "path must be set");
        String uri = UriComponentsBuilder.fromUri(this.root).path(requestContext.getPath()).build().toString();

        ResponseActions responseActions = this.mockServer
            .expect(method(method))
            .andExpect(requestTo(uri));

        if (!requestContext.isAnyRequestPayload()) {
            RequestMatcher payloadMatcher;
            if (requestContext.getRequestPayload() != null) {
                payloadMatcher = ContentMatchers.jsonPayload(requestContext.getRequestPayload());
            } else {
                payloadMatcher = content().string("");
            }

            responseActions = responseActions.andExpect(payloadMatcher);
        }

        for (RequestMatcher requestMatcher : requestContext.getRequestMatchers()) {
            responseActions = responseActions.andExpect(requestMatcher);
        }

        HttpStatus status = requestContext.getStatus();
        Assert.notNull(status, "status must be set");

        MediaType contentType = requestContext.getContentType();

        ResponseCreator responseCreator;
        if (requestContext.getResponsePayload() != null) {
            responseCreator = withStatus(status).contentType(contentType).body(requestContext.getResponsePayload());
        } else {
            responseCreator = withStatus(status);
        }

        responseActions.andRespond(responseCreator);
    }

    protected final void verify() {
        this.mockServer.verify();
    }

    @Getter
    public static final class RequestContext {

        private final List<RequestMatcher> requestMatchers = new ArrayList<>();

        private boolean anyRequestPayload;

        private MediaType contentType = APPLICATION_JSON;

        private HttpMethod method;

        private String path;

        private Resource requestPayload;

        private Resource responsePayload;

        private HttpStatus status;

        public RequestContext anyRequestPayload() {
            this.anyRequestPayload = true;
            return this;
        }

        public RequestContext contentType(MediaType contentType) {
            this.contentType = contentType;
            return this;
        }

        public RequestContext errorResponse() {
            status(UNPROCESSABLE_ENTITY);
            responsePayload("v2/error_response.json");
            return this;
        }

        public RequestContext method(HttpMethod method) {
            this.method = method;
            return this;
        }

        public RequestContext path(String path) {
            this.path = path;
            return this;
        }

        public RequestContext requestMatcher(RequestMatcher requestMatcher) {
            this.requestMatchers.add(requestMatcher);
            return this;
        }

        public RequestContext requestPayload(String path) {
            if (path != null) {
                this.requestPayload = new ClassPathResource(path);
            }

            return this;
        }

        public RequestContext responsePayload(String path) {
            if (path != null) {
                this.responsePayload = new ClassPathResource(path);
            }

            return this;
        }

        public RequestContext status(HttpStatus status) {
            this.status = status;
            return this;
        }

    }

}
