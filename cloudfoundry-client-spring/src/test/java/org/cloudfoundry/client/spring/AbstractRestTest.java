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
import org.cloudfoundry.client.v3.LinkBased;
import org.springframework.core.io.ClassPathResource;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.security.oauth2.client.DefaultOAuth2ClientContext;
import org.springframework.security.oauth2.client.OAuth2RestTemplate;
import org.springframework.security.oauth2.client.token.grant.client.ClientCredentialsResourceDetails;
import org.springframework.security.oauth2.common.DefaultOAuth2AccessToken;
import org.springframework.test.web.client.MockRestServiceServer;
import org.springframework.test.web.client.RequestMatcher;
import org.springframework.test.web.client.ResponseActions;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;
import java.util.List;

import static com.fasterxml.jackson.annotation.JsonInclude.Include.NON_NULL;
import static org.cloudfoundry.client.spring.ContentMatchers.jsonPayload;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.content;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.method;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.requestTo;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withStatus;

public abstract class AbstractRestTest {

    protected final OAuth2RestTemplate restTemplate = new OAuth2RestTemplate(new ClientCredentialsResourceDetails(),
            new DefaultOAuth2ClientContext(new DefaultOAuth2AccessToken("test-access-token")));

    private MockRestServiceServer mockRestServiceServer;

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

    protected final URI root = UriComponentsBuilder.newInstance()
            .scheme("https").host("api.run.pivotal.io")
            .build().toUri();

    protected final MockRestServiceServer mockServer = MockRestServiceServer.createServer(this.restTemplate);

    protected final void mockRequest(HttpMethod httpMethod, String endpoint, String requestFile,
                                     HttpStatus httpResponse, String responseFile) {
        mockRequest(httpMethod, endpoint, null, requestFile, httpResponse, responseFile);
    }

    protected final void mockRequest(HttpMethod httpMethod, String endpoint, HttpStatus httpResponse,
                                     String responseFile) {
        mockRequest(httpMethod, endpoint, null, null, httpResponse, responseFile);
    }

    protected final void mockRequest(HttpMethod httpMethod, String endpoint, String requestFile,
                                     HttpStatus httpResponse) {
        mockRequest(httpMethod, endpoint, null, requestFile, httpResponse, null);
    }

    protected final void mockRequest(HttpMethod httpMethod, String endpoint, HttpStatus httpResponse) {
        mockRequest(httpMethod, endpoint, null, null, httpResponse, null);
    }

    protected final void mockRequest(HttpMethod httpMethod, String endpoint, RequestMatcher header, String requestFile,
                                     HttpStatus httpResponse, String responseFile) {
        this.mockRestServiceServer = MockRestServiceServer.createServer(this.restTemplate);

        RequestMatcher requestMatcher;
        if (requestFile == null) {
            requestMatcher = content().string("");
        } else {
            requestMatcher = jsonPayload(new ClassPathResource(requestFile));
        }

        ResponseActions baseResponse = this.mockRestServiceServer
                .expect(method(httpMethod))
                .andExpect(requestTo(endpoint))
                .andExpect(requestMatcher);

        if (responseFile == null) {
            baseResponse.andRespond(withStatus(httpResponse));
        } else {
            baseResponse.andRespond(withStatus(httpResponse)
                    .body(new ClassPathResource(responseFile))
                    .contentType(APPLICATION_JSON));
        }
    }

    protected final void validateLinks(LinkBased response, String... links) {
        assertEquals(links.length, response.getLinks().size());
        for (String link : links) {
            assertNotNull(response.getLink(link));
        }
    }

    protected final void verifyMockServer() {
        this.mockRestServiceServer.verify();
    }
}
