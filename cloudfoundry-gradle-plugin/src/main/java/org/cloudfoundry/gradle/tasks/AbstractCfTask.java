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

import org.cloudfoundry.client.CloudFoundryClient;
import org.cloudfoundry.gradle.CfPluginExtension;
import org.cloudfoundry.gradle.CfProperties;
import org.cloudfoundry.gradle.CfPropertiesMapper;
import org.cloudfoundry.operations.CloudFoundryOperations;
import org.cloudfoundry.operations.DefaultCloudFoundryOperations;
import org.cloudfoundry.reactor.ConnectionContext;
import org.cloudfoundry.reactor.DefaultConnectionContext;
import org.cloudfoundry.reactor.TokenProvider;
import org.cloudfoundry.reactor.client.ReactorCloudFoundryClient;
import org.cloudfoundry.reactor.tokenprovider.PasswordGrantTokenProvider;
import org.gradle.api.DefaultTask;
import org.gradle.api.logging.Logger;
import org.gradle.api.logging.Logging;

/**
 * Base class for all Concrete CF tasks
 */
abstract class AbstractCfTask extends DefaultTask {

    protected long defaultWaitTimeout = 600_000L; // 10 mins

    static Logger LOGGER = Logging.getLogger(AbstractCfTask.class);

    protected CfPropertiesMapper cfPropertiesMapper;

    protected AbstractCfTask() {
        this.cfPropertiesMapper = new CfPropertiesMapper(getProject());
    }

    protected CloudFoundryOperations getCfOperations() {
        CfProperties cfAppProperties = this.cfPropertiesMapper.getProperties();

        ConnectionContext connectionContext = DefaultConnectionContext.builder()
            .apiHost(cfAppProperties.ccHost())
            .skipSslValidation(true)
            .build();

        TokenProvider tokenProvider = PasswordGrantTokenProvider.builder()
            .password(cfAppProperties.ccPassword())
            .username(cfAppProperties.ccUser())
            .build();

        CloudFoundryClient cfClient = ReactorCloudFoundryClient.builder()
            .connectionContext(connectionContext)
            .tokenProvider(tokenProvider)
            .build();

        CloudFoundryOperations cfOperations = DefaultCloudFoundryOperations.builder()
            .cloudFoundryClient(cfClient)
            .organization(cfAppProperties.org())
            .space(cfAppProperties.space())
            .build();

        return cfOperations;
    }

    protected CfPluginExtension getExtension() {
        return this.getProject().getExtensions().findByType(CfPluginExtension.class);
    }

    protected CfProperties getCfProperties() {
        return this.cfPropertiesMapper.getProperties();
    }

    @Override
    public String getGroup() {
        return "Cloud Foundry Tasks";
    }
}
