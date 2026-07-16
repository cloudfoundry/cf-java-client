package org.cloudfoundry.operations;

import java.time.Duration;
import org.cloudfoundry.AbstractIntegrationTest;
import org.cloudfoundry.operations.stacks.GetStackRequest;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

class StacksTest extends AbstractIntegrationTest {
    @Autowired private CloudFoundryOperations cloudFoundryOperations;

    @Autowired private Mono<String> stackName;

    @Test
    public void create() {
        this.stackName
                .flatMap(
                        name ->
                                this.cloudFoundryOperations
                                        .stacks()
                                        .get(GetStackRequest.builder().name(name).build()))
                .as(StepVerifier::create)
                .expectNextMatches(
                        s -> s.getDescription().contains("Cloud Foundry Linux-based filesystem"))
                .expectComplete()
                .verify(Duration.ofMinutes(5));
    }

    @Test
    public void list() {
        String stackName = this.stackName.block();
        this.cloudFoundryOperations
                .stacks()
                .list()
                .filter(s -> s.getName().equals(stackName))
                .as(StepVerifier::create)
                .expectNextMatches(
                        s -> s.getDescription().startsWith("Cloud Foundry Linux-based filesystem"))
                .expectComplete()
                .verify(Duration.ofMinutes(5));
    }
}
