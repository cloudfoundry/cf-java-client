package wf.garnier.cf.loadtest;

import java.time.Duration;
import java.time.Instant;
import java.util.List;
import java.util.stream.IntStream;

import org.cloudfoundry.client.CloudFoundryClient;
import org.cloudfoundry.client.v2.info.GetInfoRequest;
import org.cloudfoundry.client.v3.Relationship;
import org.cloudfoundry.client.v3.ToOneRelationship;
import org.cloudfoundry.client.v3.organizations.CreateOrganizationRequest;
import org.cloudfoundry.client.v3.organizations.CreateOrganizationResponse;
import org.cloudfoundry.client.v3.organizations.DeleteOrganizationRequest;
import org.cloudfoundry.client.v3.organizations.ListOrganizationsRequest;
import org.cloudfoundry.client.v3.spaces.CreateSpaceRequest;
import org.cloudfoundry.client.v3.spaces.ListSpacesRequest;
import org.cloudfoundry.client.v3.spaces.ListSpacesResponse;
import org.cloudfoundry.client.v3.spaces.SpaceRelationships;
import org.cloudfoundry.client.v3.spaces.SpaceResource;
import org.cloudfoundry.operations.CloudFoundryOperations;
import org.cloudfoundry.operations.DefaultCloudFoundryOperations;
import org.cloudfoundry.reactor.ConnectionContext;
import org.cloudfoundry.reactor.DefaultConnectionContext;
import org.cloudfoundry.reactor.ProxyConfiguration;
import org.cloudfoundry.reactor.client.ReactorCloudFoundryClient;
import org.cloudfoundry.reactor.tokenprovider.ClientCredentialsGrantTokenProvider;
import org.cloudfoundry.reactor.tokenprovider.PasswordGrantTokenProvider;
import org.cloudfoundry.reactor.uaa.ReactorUaaClient;
import org.cloudfoundry.uaa.identityzones.ListIdentityZonesRequest;
import org.cloudfoundry.util.PaginationUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.util.StringUtils;

@SpringBootApplication(proxyBeanMethods = true)
public class LoadtestApplication {

    private static final String ORG_PREFIX = "test-load-";

    public static void main(String[] args) {
        SpringApplication.run(LoadtestApplication.class, args);
    }

    private final Logger logger = LoggerFactory.getLogger("load-test");

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


    @Bean
    ReactorCloudFoundryClient cfClient(
            ConnectionContext connectionContext,
            @Value("${test.admin.password}") String password,
            @Value("${test.admin.username}") String username) {
        return ReactorCloudFoundryClient.builder()
                .connectionContext(connectionContext)
                .tokenProvider(
                        PasswordGrantTokenProvider.builder()
                                .password(password)
                                .username(username)
                                .build())
                .build();
    }

    @Bean
    CloudFoundryOperations cfOps(CloudFoundryClient cfClient) {
        return DefaultCloudFoundryOperations.builder()
                .cloudFoundryClient(cfClient)
                .build();
    }

    @Bean
    @Qualifier("admin")
    ReactorUaaClient adminUaaClient(
            ConnectionContext connectionContext,
            @Value("${test.admin.clientId}") String clientId,
            @Value("${test.admin.clientSecret}") String clientSecret) {
        return ReactorUaaClient.builder()
                .connectionContext(connectionContext)
                .tokenProvider(
                        ClientCredentialsGrantTokenProvider.builder()
                                .clientId(clientId)
                                .clientSecret(clientSecret)
                                .build())
                .build();
    }

    @Bean
    ApplicationRunner appRunner(CloudFoundryClient client, CloudFoundryOperations ops, ReactorUaaClient uaaClient) {
        return args -> {
            // Ensure that refresh tokens are revocable and do rotate (eyeballing it)
            var identityZones = uaaClient.identityZones().list(ListIdentityZonesRequest.builder().build()).block().getIdentityZones();
            System.out.println(identityZones);

            System.out.println("👋 hiiiii\n\n");
            System.out.println(client.info().get(GetInfoRequest.builder().build()).block());
            System.out.println("\n\n👋 hooo\n\n");
            var deletedOrgs = cleanOrgs(client).count().block();
            System.out.printf("Deleted %s orgs.%n%n", deletedOrgs);
            // Delete is not fully synchronous / consistent, so we wait for a bit
            for (int i = 0; i < 5; i++) {
                Thread.sleep(1000);

                Long remainingOrgs = countMatchingOrgs(ops).block();
                System.out.printf("Waiting for org deletion... %s remaining.%n", remainingOrgs);
                if (remainingOrgs == 0) break;
            }
            var createdOrgs = seedOrgs(client).collectList().block();
            System.out.println(createdOrgs);

            var startTime = Instant.now();
            var waitDuration = Duration.ofMinutes(5).plusSeconds(10);
            var cutoffTime = startTime.plus(waitDuration);

            while (Instant.now().isBefore(cutoffTime)) {
                System.out.printf("Waiting for token to expire, %s left%n", Duration.between(Instant.now(), cutoffTime));
                Thread.sleep(10_000);
            }

            var spaces = listSpaces(client, createdOrgs).collectList().block();
            System.out.println(spaces);
            System.out.printf("Deleted %s orgs.%n%n", deletedOrgs);
        };
    }

    static Flux<String> listSpaces(CloudFoundryClient client, List<String> orgIds) {
        return Flux.fromIterable(orgIds)
                .flatMap(id -> client.spacesV3().list(ListSpacesRequest.builder()
                        .organizationIds(id)
                        .build())
                )
                .flatMapIterable(ListSpacesResponse::getResources)
                .map(SpaceResource::getName);
    }

    static Flux<String> cleanOrgs(CloudFoundryClient client) {
        return PaginationUtils.requestClientV3Resources(page -> client.organizationsV3()
                        .list(
                                ListOrganizationsRequest.builder()
                                        .page(page)
                                        .build()
                        ))
                .filter(org -> org.getName().startsWith(ORG_PREFIX))
                .doOnNext(r -> System.out.printf("Deleting ... %s%n", r.getName()))
                .flatMap(o ->
                        client.organizationsV3().delete(
                                        DeleteOrganizationRequest.builder()
                                                .organizationId(o.getId())
                                                .build()
                                )
                                .doOnNext(r -> System.out.printf("Deleted org %s%n", o.getName()))
                )
                .switchIfEmpty(Mono.just("No orgs found, no deletion required"));
    }

    static Mono<Long> countMatchingOrgs(CloudFoundryOperations ops) {
        return ops.organizations()
                .list()
                .filter(o -> o.getName().startsWith(ORG_PREFIX))
                .count();
    }


    static Flux<String> seedOrgs(CloudFoundryClient client) {
        return Flux.fromStream(IntStream.range(1, 21).boxed())
                .flatMap(i -> client.organizationsV3().create(
                        CreateOrganizationRequest.builder()
                                .name(ORG_PREFIX + i)
                                .build()
                ))
                .doOnNext(o -> System.out.printf("Created org: %s%n", o.getName()))
                //
                .flatMap(o -> client.spacesV3().create(
                                        CreateSpaceRequest.builder()
                                                .relationships(
                                                        SpaceRelationships.builder()
                                                                .organization(ToOneRelationship.builder()
                                                                        .data(Relationship.builder().id(o.getId()).build())
                                                                        .build()
                                                                )
                                                                .build()
                                                )
                                                .name("space-" + o.getName())
                                                .build()
                                )
                                .doOnNext(s -> System.out.printf("      Created space %s%n", s.getName()))
                                .then(Mono.just(o))
                )
                .map(CreateOrganizationResponse::getId);
    }

}
