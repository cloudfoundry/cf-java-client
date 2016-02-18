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

package org.cloudfoundry.spring.util.network;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.deser.DeserializationProblemHandler;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.Accessors;
import org.cloudfoundry.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.client.ClientHttpRequestFactory;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.security.oauth2.client.OAuth2ClientContext;
import org.springframework.security.oauth2.client.OAuth2RestTemplate;
import org.springframework.security.oauth2.client.resource.OAuth2ProtectedResourceDetails;
import org.springframework.security.oauth2.client.token.grant.password.ResourceOwnerPasswordAccessTokenProvider;
import org.springframework.util.Assert;
import org.springframework.web.client.RestTemplate;
import reactor.fn.Consumer;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLContext;
import java.util.ArrayList;
import java.util.List;

import static com.fasterxml.jackson.annotation.JsonInclude.Include.NON_NULL;

@Accessors(chain = true, fluent = true)
@Setter
@ToString
public final class OAuth2RestTemplateBuilder {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    private OAuth2ClientContext clientContext;

    private HostnameVerifier hostnameVerifier;

    private List<HttpMessageConverter<?>> messageConverters = new ArrayList<>();

    private List<DeserializationProblemHandler> problemHandlers = new ArrayList<>();

    private OAuth2ProtectedResourceDetails protectedResourceDetails;

    private SSLContext sslContext;

    public OAuth2RestTemplate build() {
        OAuth2RestTemplate restTemplate = getRestTemplate(this.clientContext, this.protectedResourceDetails);

        addLogging(restTemplate);
        addMessageConverters(restTemplate, this.messageConverters);
        modifyObjectMapper(getObjectMapper(restTemplate), this.problemHandlers, this.logger);
        setRequestFactory(restTemplate, this.hostnameVerifier, this.sslContext);

        return restTemplate;
    }

    public OAuth2RestTemplateBuilder messageConverter(HttpMessageConverter<?> messageConverter) {
        this.messageConverters.add(messageConverter);
        return this;
    }

    public OAuth2RestTemplateBuilder problemHandler(DeserializationProblemHandler problemHandler) {
        this.problemHandlers.add(problemHandler);
        return this;
    }

    private static void addLogging(RestTemplate restTemplate) {
        restTemplate.getInterceptors().add(new LoggingClientHttpRequestInterceptor());
    }

    private static void addMessageConverters(RestTemplate restTemplate, List<HttpMessageConverter<?>> httpMessageConverters) {
        for (HttpMessageConverter<?> httpMessageConverter : httpMessageConverters) {
            restTemplate.getMessageConverters().add(httpMessageConverter);
        }
    }

    private static Optional<ObjectMapper> getObjectMapper(RestTemplate restTemplate) {
        for (HttpMessageConverter<?> messageConverter : restTemplate.getMessageConverters()) {
            if (messageConverter instanceof MappingJackson2HttpMessageConverter) {
                ObjectMapper objectMapper = ((MappingJackson2HttpMessageConverter) messageConverter).getObjectMapper();
                return Optional.of(objectMapper);
            }
        }

        return Optional.empty();
    }

    private static OAuth2RestTemplate getRestTemplate(OAuth2ClientContext clientContext, OAuth2ProtectedResourceDetails protectedResourceDetails) {
        Assert.notNull(clientContext, "clientContext must not be null");
        Assert.notNull(protectedResourceDetails, "protectedResourceDetails must not be null");

        return new OAuth2RestTemplate(protectedResourceDetails, clientContext);
    }

    private static void modifyObjectMapper(Optional<ObjectMapper> objectMapper, final List<DeserializationProblemHandler> problemHandlers, final Logger logger) {
        objectMapper
            .ifPresent(new Consumer<ObjectMapper>() {

                @Override
                public void accept(ObjectMapper objectMapper) {
                    logger.debug("Modifying ObjectMapper configuration");

                    objectMapper.setSerializationInclusion(NON_NULL);

                    for (DeserializationProblemHandler problemHandler : problemHandlers) {
                        objectMapper.addHandler(problemHandler);
                    }
                }

            });
    }

    private static void setRequestFactory(OAuth2RestTemplate restTemplate, HostnameVerifier hostnameVerifier, SSLContext sslContext) {
        if (hostnameVerifier != null && sslContext != null) {
            CustomSslSimpleClientHttpRequestFactory requestFactory = new CustomSslSimpleClientHttpRequestFactory(hostnameVerifier, sslContext);

            restTemplate.setRequestFactory(requestFactory);
            restTemplate.setAccessTokenProvider(getAccessTokenProvider(requestFactory));
        }
    }

    private static ResourceOwnerPasswordAccessTokenProvider getAccessTokenProvider(ClientHttpRequestFactory requestFactory) {
        ResourceOwnerPasswordAccessTokenProvider accessTokenProvider = new ResourceOwnerPasswordAccessTokenProvider();
        accessTokenProvider.setRequestFactory(requestFactory);
        return accessTokenProvider;
    }

}
