/*
 * Copyright 2013-2019 the original author or authors.
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

package org.cloudfoundry.client.v3.routes;

import org.cloudfoundry.client.v3.Relationship;
import org.cloudfoundry.client.v3.ToOneRelationship;
import org.junit.Test;

public final class CreateRouteRequestTest {

    @Test(expected = IllegalStateException.class)
    public void invalidWithMissingRelationship() {
        CreateRouteRequest.builder().build();
    }

    @Test(expected = IllegalStateException.class)
    public void invalidWithMissingSpaceRelationship() {
        CreateRouteRequest.builder()
            .relationships(RouteRelationships
                .builder()
                .domain(ToOneRelationship.builder()
                    .data(Relationship.builder()
                        .id("test-domain-id")
                        .build()).build())
                .build())
            .build();
    }


    @Test(expected = IllegalStateException.class)
    public void invalidWithMissingDomainRelationship() {
        CreateRouteRequest.builder()
            .relationships(RouteRelationships
                .builder()
                .space(ToOneRelationship.builder()
                    .data(Relationship.builder()
                        .id("test-space-id")
                        .build()).build())
                .build())
            .build();
    }

    @Test
    public void valid() {
        CreateRouteRequest.builder().relationships(RouteRelationships.builder()
            .domain(ToOneRelationship.builder()
                .data(Relationship.builder()
                    .id("test-domain-id")
                    .build())
                .build())
            .space(ToOneRelationship.builder()
                .data(Relationship.builder()
                    .id("test-space-id")
                    .build())
                .build())
            .build());
    }
}
