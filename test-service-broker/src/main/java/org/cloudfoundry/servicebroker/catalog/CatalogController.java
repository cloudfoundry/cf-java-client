/*
 * Copyright 2013-2017 the original author or authors.
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

package org.cloudfoundry.servicebroker.catalog;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
final class CatalogController {

    private final Catalog catalog;

    CatalogController(@Value("${service.name}") String serviceName, @Value("${plan.name}") String planName) {
        this.catalog = Catalog.builder()
            .service(Service.builder()
                .id(UUID.randomUUID().toString())
                .name(serviceName)
                .require("route_forwarding")
                .description("test-service-description")
                .bindable(true)
                .planUpdateable(true)
                .metadata(ServiceMetadata.builder()
                    .displayName("test-service-display-name")
                    .listing(Listing.builder()
                        .blurb("test-listing-blurb")
                        .imageUrl("http://test-image-host/test-image.gif")
                        .longDescription("test-listing-long-description")
                        .build())
                    .provider(Provider.builder()
                        .name("test-provider-name")
                        .build())
                    .build())
                .dashboardClient(DashboardClient.builder()
                    .id(UUID.randomUUID().toString())
                    .redirectUri("http://test-dashboard-host")
                    .secret(UUID.randomUUID().toString())
                    .build())
                .plan(Plan.builder()
                    .id(UUID.randomUUID().toString())
                    .name(planName)
                    .description("test-plan-description")
                    .metadata(PlanMetadata.builder()
                        .bullet(Bullet.builder()
                            .content("test-plan-metadata-bullet")
                            .build())
                        .cost(Cost.builder()
                            .amount("usd", 1)
                            .unit("MONTHLY")
                            .build())
                        .build())
                    .build())
                .build())
            .build();
    }

    @GetMapping("/v2/catalog")
    ResponseEntity<Catalog> catalog() {
        return ResponseEntity.ok(this.catalog);
    }

}
