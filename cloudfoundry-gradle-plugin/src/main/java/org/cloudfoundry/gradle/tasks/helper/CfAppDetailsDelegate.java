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

package org.cloudfoundry.gradle.tasks.helper;

import org.cloudfoundry.gradle.CfProperties;
import org.cloudfoundry.operations.CloudFoundryOperations;
import org.cloudfoundry.operations.applications.ApplicationDetail;
import org.cloudfoundry.operations.applications.GetApplicationRequest;
import org.gradle.api.logging.Logger;
import org.gradle.api.logging.Logging;
import reactor.core.publisher.Mono;

import java.util.Optional;

/**
 * Responsible for getting the details of an app
 *
 * @author Biju Kunjummen
 */
public class CfAppDetailsDelegate {

    private static final Logger LOGGER = Logging.getLogger(CfAppDetailsDelegate.class);

    public Mono<Optional<ApplicationDetail>> getAppDetails(CloudFoundryOperations cfOperations,
                                                           CfProperties cfAppProperties) {

        LOGGER.lifecycle("Checking details of app '{}'", cfAppProperties.name());
        Mono<ApplicationDetail> applicationDetailMono = cfOperations.applications()
            .get(
                GetApplicationRequest.builder()
                    .name(cfAppProperties.name())
                    .build());

        return applicationDetailMono
            .map(appDetail -> Optional.ofNullable(appDetail))
            .otherwise(Exception.class, e -> Mono.just(Optional.empty()));
    }

}
