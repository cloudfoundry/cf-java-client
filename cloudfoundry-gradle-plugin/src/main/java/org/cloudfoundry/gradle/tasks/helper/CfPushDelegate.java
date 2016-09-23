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
import org.cloudfoundry.operations.applications.PushApplicationRequest;
import org.cloudfoundry.operations.applications.RestartApplicationRequest;
import org.cloudfoundry.operations.applications.SetEnvironmentVariableApplicationRequest;
import org.cloudfoundry.operations.services.BindServiceInstanceRequest;
import org.gradle.api.logging.Logger;
import org.gradle.api.logging.Logging;
import reactor.core.publisher.Mono;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.Duration;
import java.util.Map;

/**
 * Responsible for cf-push, since this functionality is going to be called from a few places this logic has been
 * centralized here.
 *
 * @author Biju Kunjummen
 */
public class CfPushDelegate {

    private static final Logger LOGGER = Logging.getLogger(CfPushDelegate.class);

    public Mono<Void> push(CloudFoundryOperations cfOperations, CfProperties cfAppProperties) {
        LOGGER.lifecycle("Pushing app '{}'", cfAppProperties.name());
        Path path = Paths.get(cfAppProperties.filePath());


        Mono<Void> resp = cfOperations.applications()
            .push(PushApplicationRequest.builder()
                .name(cfAppProperties.name())
                .application(path)
                .buildpack(cfAppProperties.buildpack())
                .command(cfAppProperties.command())
                .diskQuota(cfAppProperties.diskQuota())
                .instances(cfAppProperties.instances())
                .memory(cfAppProperties.memory())
                .timeout(cfAppProperties.timeout())
                .stagingTimeout(Duration.ofMinutes(cfAppProperties.stagingTimeout()))
                .startupTimeout(Duration.ofMinutes(cfAppProperties.startupTimeout()))
                .domain(cfAppProperties.domain())
                .host(cfAppProperties.hostName())
                .routePath(cfAppProperties.path())
                .noStart(true)
                .build());

        if (cfAppProperties.environment() != null) {
            for (Map.Entry<String, String> entry : cfAppProperties.environment().entrySet()) {
                LOGGER.lifecycle("Setting env variable '{}'", entry.getKey());
                resp = resp.then(cfOperations.applications()
                    .setEnvironmentVariable(SetEnvironmentVariableApplicationRequest
                        .builder()
                        .name(cfAppProperties.name())
                        .variableName(entry.getKey())
                        .variableValue(entry.getValue())
                        .build()));
            }
        }

        if (cfAppProperties.services() != null) {
            for (String serviceName : cfAppProperties.services()) {
                LOGGER.lifecycle("Binding Service '{}'", serviceName);
                resp = resp.then(cfOperations.services()
                    .bind(BindServiceInstanceRequest.builder()
                        .serviceInstanceName(serviceName)
                        .applicationName(cfAppProperties.name())
                        .build()));
            }
        }

        LOGGER.lifecycle("Starting app '{}'", cfAppProperties.name());
        return resp.then(cfOperations.applications().restart(RestartApplicationRequest
            .builder()
            .name(cfAppProperties.name())
            .startupTimeout(Duration.ofMinutes(cfAppProperties.startupTimeout()))
            .build()));

    }
}
