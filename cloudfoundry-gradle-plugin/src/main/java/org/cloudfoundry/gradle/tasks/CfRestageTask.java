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

package org.cloudfoundry.gradle.tasks;

import org.cloudfoundry.gradle.CfProperties;
import org.cloudfoundry.operations.CloudFoundryOperations;
import org.cloudfoundry.operations.applications.RestageApplicationRequest;
import org.gradle.api.tasks.TaskAction;
import reactor.core.publisher.Mono;

import java.time.Duration;

/**
 * Responsible for cf restage.
 *
 * @author Biju Kunjummen
 */
public class CfRestageTask extends AbstractCfTask {

    @TaskAction
    public void restage() {
        CfProperties cfAppProperties = getCfProperties();
        LOGGER.info("About to call Restage task : {} ", cfAppProperties.toString());

        CloudFoundryOperations cfOperations = getCfOperations();

        Mono<Void> resp = cfOperations.applications().restage(RestageApplicationRequest.builder()
            .name(cfAppProperties.name())
            .stagingTimeout(Duration.ofMinutes(cfAppProperties.stagingTimeout()))
            .startupTimeout(Duration.ofMinutes(cfAppProperties.startupTimeout())).build()
        );

        resp.block(Duration.ofMillis(defaultWaitTimeout));
    }

    @Override
    public String getDescription() {
        return "Restage an Application";
    }
}
