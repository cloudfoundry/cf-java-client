/*
 * Copyright 2013-2021 the original author or authors.
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

package org.cloudfoundry.operations.applications;

import org.junit.jupiter.api.Test;
import reactor.test.StepVerifier;

import java.time.Duration;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

class RouteUtilsTest {

    @Test
    void complexHit() {
        List<DomainSummary> availableDomains = new ArrayList<>();
        availableDomains.add(DomainSummary.builder()
            .id("1")
            .name("test.com")
            .build());
        availableDomains.add(DomainSummary.builder()
            .id("2")
            .name("test.test.com")
            .build());

        DecomposedRoute expected = DecomposedRoute.builder()
            .domain("test.test.com")
            .build();

        RouteUtils.decomposeRoute(availableDomains, "test.test.com", null)
            .as(StepVerifier::create)
            .expectNext(expected)
            .expectComplete()
            .verify(Duration.ofSeconds(5));
    }

    @Test
    void complexPathHit() {
        List<DomainSummary> availableDomains = new ArrayList<>();
        availableDomains.add(DomainSummary.builder()
            .id("1")
            .name("test.com")
            .build());
        availableDomains.add(DomainSummary.builder()
            .id("2")
            .name("test.test.com")
            .build());

        DecomposedRoute expected = DecomposedRoute.builder()
            .domain("test.test.com")
            .path("/path")
            .build();

        RouteUtils.decomposeRoute(availableDomains, "test.test.com/path", null)
            .as(StepVerifier::create)
            .expectNext(expected)
            .expectComplete()
            .verify(Duration.ofSeconds(5));
    }

    @Test
    void empty() {
        assertThrows(IllegalArgumentException.class, () -> {
            List<DomainSummary> availableDomains = Collections.emptyList();

            RouteUtils.decomposeRoute(availableDomains, "test.test.com", null)
                .as(StepVerifier::create)
                .consumeErrorWith(t -> assertThat(t).isInstanceOf(IllegalArgumentException.class).hasMessage("The route test.test.com did not match any existing domains"))
                .verify(Duration.ofSeconds(5));
        });
    }

    @Test
    void partialMatchMiss() {
        assertThrows(IllegalArgumentException.class, () -> {
            List<DomainSummary> availableDomains = new ArrayList<>();
            availableDomains.add(DomainSummary.builder()
                .id("1")
                .name("test.something.com")
                .build());
            availableDomains.add(DomainSummary.builder()
                .id("2")
                .name("something.com")
                .build());
            availableDomains.add(DomainSummary.builder()
                .id("3")
                .name("hing.com")
                .build());

            RouteUtils.decomposeRoute(availableDomains, "thing.com", null)
                .as(StepVerifier::create)
                .consumeErrorWith(t -> assertThat(t).isInstanceOf(IllegalArgumentException.class).hasMessage("The route thing.com did not match any existing domains"))
                .verify(Duration.ofSeconds(5));
        });
    }

    @Test
    void partialMiss() {
        assertThrows(IllegalArgumentException.class, () -> {
            List<DomainSummary> availableDomains = new ArrayList<>();
            availableDomains.add(DomainSummary.builder()
                .id("1")
                .name("test.com")
                .build());
            availableDomains.add(DomainSummary.builder()
                .id("2")
                .name("test.test.com")
                .build());

            RouteUtils.decomposeRoute(availableDomains, "est.com", null)
                .as(StepVerifier::create)
                .consumeErrorWith(t -> assertThat(t).isInstanceOf(IllegalArgumentException.class).hasMessage("The route est.com did not match any existing domains"))
                .verify(Duration.ofSeconds(5));
        });
    }

    @Test
    void pathMiss() {
        assertThrows(IllegalArgumentException.class, () -> {
            List<DomainSummary> availableDomains = new ArrayList<>();
            availableDomains.add(DomainSummary.builder()
                .id("1")
                .name("test.com")
                .build());
            availableDomains.add(DomainSummary.builder()
                .id("2")
                .name("test.test.com")
                .build());

            RouteUtils.decomposeRoute(availableDomains, "miss.com/path", null)
                .as(StepVerifier::create)
                .consumeErrorWith(t -> assertThat(t).isInstanceOf(IllegalArgumentException.class).hasMessage("The route miss.com/path did not match any existing domains"))
                .verify(Duration.ofSeconds(5));
        });
    }

    @Test
    void routeDecompositionPathHit() {
        List<DomainSummary> availableDomains = new ArrayList<>();
        availableDomains.add(DomainSummary.builder()
            .id("1")
            .name("test.com")
            .build());
        availableDomains.add(DomainSummary.builder()
            .id("2")
            .name("test.test.com")
            .build());

        DecomposedRoute expected = DecomposedRoute.builder()
            .domain("test.com")
            .path("/path")
            .build();

        RouteUtils.decomposeRoute(availableDomains, "test.com/path", null)
            .as(StepVerifier::create)
            .expectNext(expected)
            .expectComplete()
            .verify(Duration.ofSeconds(5));
    }

    @Test
    void simpleHit() {
        List<DomainSummary> availableDomains = new ArrayList<>();
        availableDomains.add(DomainSummary.builder()
            .id("1")
            .name("test.com")
            .build());
        availableDomains.add(DomainSummary.builder()
            .id("2")
            .name("test.test.com")
            .build());

        DecomposedRoute expected = DecomposedRoute.builder()
            .domain("test.com")
            .build();

        RouteUtils.decomposeRoute(availableDomains, "test.com", null)
            .as(StepVerifier::create)
            .expectNext(expected)
            .expectComplete()
            .verify(Duration.ofSeconds(5));
    }

    @Test
    void simpleHostHit() {
        List<DomainSummary> availableDomains = new ArrayList<>();
        availableDomains.add(DomainSummary.builder()
            .id("1")
            .name("test.com")
            .build());
        availableDomains.add(DomainSummary.builder()
            .id("2")
            .name("test.test.com")
            .build());

        DecomposedRoute expected = DecomposedRoute.builder()
            .domain("test.com")
            .host("host")
            .build();

        RouteUtils.decomposeRoute(availableDomains, "host.test.com", null)
            .as(StepVerifier::create)
            .expectNext(expected)
            .expectComplete()
            .verify(Duration.ofSeconds(5));
    }

    @Test
    void simpleHostPathHit() {
        List<DomainSummary> availableDomains = new ArrayList<>();
        availableDomains.add(DomainSummary.builder()
            .id("1")
            .name("test.com")
            .build());
        availableDomains.add(DomainSummary.builder()
            .id("2")
            .name("test.test.com")
            .build());

        DecomposedRoute expected = DecomposedRoute.builder()
            .domain("test.com")
            .host("host")
            .path("/path")
            .build();

        RouteUtils.decomposeRoute(availableDomains, "host.test.com/path", null)
            .as(StepVerifier::create)
            .expectNext(expected)
            .expectComplete()
            .verify(Duration.ofSeconds(5));
    }

    @Test
    void simpleMiss() {
        assertThrows(IllegalArgumentException.class, () -> {
            List<DomainSummary> availableDomains = new ArrayList<>();
            availableDomains.add(DomainSummary.builder()
                .id("1")
                .name("test.com")
                .build());
            availableDomains.add(DomainSummary.builder()
                .id("2")
                .name("test.test.com")
                .build());

            RouteUtils.decomposeRoute(availableDomains, "miss.com", null)
                .as(StepVerifier::create)
                .consumeErrorWith(t -> assertThat(t).isInstanceOf(IllegalArgumentException.class).hasMessage("The route miss.com did not match any existing domains"))
                .verify(Duration.ofSeconds(5));
        });
    }

    @Test
    void simplePathOverride() {
        List<DomainSummary> availableDomains = new ArrayList<>();
        availableDomains.add(DomainSummary.builder()
            .id("1")
            .name("test.com")
            .build());
        availableDomains.add(DomainSummary.builder()
            .id("2")
            .name("test.test.com")
            .build());

        DecomposedRoute expected = DecomposedRoute.builder()
            .domain("test.com")
            .host("host")
            .path("/override-path")
            .build();

        RouteUtils.decomposeRoute(availableDomains, "host.test.com/path", "/override-path")
            .as(StepVerifier::create)
            .expectNext(expected)
            .expectComplete()
            .verify(Duration.ofSeconds(5));
    }

}