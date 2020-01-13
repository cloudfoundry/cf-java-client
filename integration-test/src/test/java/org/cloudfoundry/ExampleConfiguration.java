/*
 * Copyright 2013-2020 the original author or authors.
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

package org.cloudfoundry;

import org.cloudfoundry.client.CloudFoundryClient;
import org.cloudfoundry.doppler.DopplerClient;
import org.cloudfoundry.operations.DefaultCloudFoundryOperations;
import org.cloudfoundry.reactor.ConnectionContext;
import org.cloudfoundry.reactor.DefaultConnectionContext;
import org.cloudfoundry.reactor.TokenProvider;
import org.cloudfoundry.reactor.client.ReactorCloudFoundryClient;
import org.cloudfoundry.reactor.doppler.ReactorDopplerClient;
import org.cloudfoundry.reactor.routing.ReactorRoutingClient;
import org.cloudfoundry.reactor.tokenprovider.PasswordGrantTokenProvider;
import org.cloudfoundry.reactor.uaa.ReactorUaaClient;
import org.cloudfoundry.routing.RoutingClient;
import org.cloudfoundry.uaa.UaaClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;

/**
 * The ExampleConfiguration should typically be used like this:
 *
 * <pre><code>
 * {@literal}@Component
 * final class Example implements ApplicationRunner {
 *
 *     private final Logger logger = LoggerFactory.getLogger("example");
 *
 *     public static void main(String[] args) {
 *         new SpringApplicationBuilder(Example.class, ExampleConfiguration.class)
 *        .run(args);
 *     }
 *
 *     {@literal}@Override
 *     public void run(ApplicationArguments args) throws Exception {
 *     }
 *
 * }
 * </code></pre>
 */
@Configuration
@EnableAutoConfiguration
public class ExampleConfiguration {

    @Bean
    @Lazy
    ReactorCloudFoundryClient cloudFoundryClient(ConnectionContext connectionContext, TokenProvider tokenProvider) {
        return ReactorCloudFoundryClient.builder()
            .connectionContext(connectionContext)
            .tokenProvider(tokenProvider)
            .build();
    }

    @Bean
    @Lazy
    DefaultCloudFoundryOperations cloudFoundryOperations(CloudFoundryClient cloudFoundryClient,
                                                         DopplerClient dopplerClient,
                                                         RoutingClient routingClient,
                                                         UaaClient uaaClient,
                                                         @Value("${example.organization}") String organization,
                                                         @Value("${example.space}") String space) {
        return DefaultCloudFoundryOperations.builder()
            .cloudFoundryClient(cloudFoundryClient)
            .dopplerClient(dopplerClient)
            .routingClient(routingClient)
            .uaaClient(uaaClient)
            .organization(organization)
            .space(space)
            .build();
    }

    @Bean
    @Lazy
    DefaultConnectionContext connectionContext(@Value("${example.apiHost}") String apiHost,
                                               @Value("${example.skipSslValidation:false}") Boolean skipSslValidation) {

        return DefaultConnectionContext.builder()
            .apiHost(apiHost)
            .skipSslValidation(skipSslValidation)
            .build();
    }

    @Bean
    @Lazy
    DopplerClient dopplerClient(ConnectionContext connectionContext, TokenProvider tokenProvider) {
        return ReactorDopplerClient.builder()
            .connectionContext(connectionContext)
            .tokenProvider(tokenProvider)
            .build();
    }

    @Bean
    @Lazy
    RoutingClient routingClient(ConnectionContext connectionContext, TokenProvider tokenProvider) {
        return ReactorRoutingClient.builder()
            .connectionContext(connectionContext)
            .tokenProvider(tokenProvider)
            .build();
    }

    @Bean
    @Lazy
    PasswordGrantTokenProvider tokenProvider(@Value("${example.password}") String password,
                                             @Value("${example.username}") String username) {

        return PasswordGrantTokenProvider.builder()
            .password(password)
            .username(username)
            .build();
    }

    @Bean
    @Lazy
    ReactorUaaClient uaaClient(ConnectionContext connectionContext, TokenProvider tokenProvider) {
        return ReactorUaaClient.builder()
            .connectionContext(connectionContext)
            .tokenProvider(tokenProvider)
            .build();
    }

}
