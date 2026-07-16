package org.cloudfoundry.uaa;

import java.time.Duration;
import org.cloudfoundry.IntegrationTestConfiguration.FailingDeserializationProblemHandler;
import org.cloudfoundry.ThrottlingUaaClient;
import org.cloudfoundry.reactor.ConnectionContext;
import org.cloudfoundry.reactor.DefaultConnectionContext;
import org.cloudfoundry.reactor.ProxyConfiguration;
import org.cloudfoundry.reactor.tokenprovider.ClientCredentialsGrantTokenProvider;
import org.cloudfoundry.reactor.uaa.ReactorUaaClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.util.StringUtils;

@Configuration
@EnableAutoConfiguration
public class RatelimitTestConfiguration {

    @Bean
    UaaClient adminUaaClient(
            ConnectionContext connectionContext,
            @Value("${test.admin.clientId}") String clientId,
            @Value("${test.admin.clientSecret}") String clientSecret,
            @Value("${uaa.api.request.limit:0}") int uaaLimit) {
        ReactorUaaClient client =
                ReactorUaaClient.builder()
                        .connectionContext(connectionContext)
                        .tokenProvider(
                                ClientCredentialsGrantTokenProvider.builder()
                                        .clientId(clientId)
                                        .clientSecret(clientSecret)
                                        .build())
                        .build();
        if (uaaLimit > 0) {
            return new ThrottlingUaaClient(client, uaaLimit);
        }
        return client;
    }

    @Bean
    DefaultConnectionContext connectionContext(
            @Value("${test.apiHost}") String apiHost,
            @Value("${test.proxy.host:}") String proxyHost,
            @Value("${test.proxy.password:}") String proxyPassword,
            @Value("${test.proxy.port:8080}") Integer proxyPort,
            @Value("${test.proxy.username:}") String proxyUsername,
            @Value("${test.skipSslValidation:false}") Boolean skipSslValidation) {

        DefaultConnectionContext.Builder connectionContext =
                DefaultConnectionContext.builder()
                        .apiHost(apiHost)
                        .problemHandler(
                                new FailingDeserializationProblemHandler()) // Test-only problem
                        // handler
                        .skipSslValidation(skipSslValidation)
                        .sslHandshakeTimeout(Duration.ofSeconds(30));

        if (StringUtils.hasText(proxyHost)) {
            ProxyConfiguration.Builder proxyConfiguration =
                    ProxyConfiguration.builder().host(proxyHost).port(proxyPort);

            if (StringUtils.hasText(proxyUsername)) {
                proxyConfiguration.password(proxyPassword).username(proxyUsername);
            }

            connectionContext.proxyConfiguration(proxyConfiguration.build());
        }

        return connectionContext.build();
    }
}
