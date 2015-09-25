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

import org.cloudfoundry.client.spring.util.FallbackHttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.test.web.client.MockRestServiceServer;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;

import static com.fasterxml.jackson.annotation.JsonInclude.Include.NON_NULL;

public abstract class AbstractRestTest {

    protected final RestTemplate restTemplate = new RestTemplate();

    {
        this.restTemplate.getMessageConverters().stream()
                .filter(converter -> converter instanceof MappingJackson2HttpMessageConverter)
                .map(converter -> (MappingJackson2HttpMessageConverter) converter)
                .findFirst()
                .ifPresent(converter -> {
                    converter.getObjectMapper()
                            .setSerializationInclusion(NON_NULL);
                });

        this.restTemplate.getMessageConverters().add(new FallbackHttpMessageConverter());
    }

    protected final URI root = UriComponentsBuilder.newInstance()
            .scheme("https").host("api.run.pivotal.io")
            .build().toUri();

    protected final MockRestServiceServer mockServer = MockRestServiceServer.createServer(this.restTemplate);

}
