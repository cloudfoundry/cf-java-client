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

package org.cloudfoundry.operations;

import org.cloudfoundry.operations.applications.Applications;
import org.cloudfoundry.operations.buildpacks.Buildpacks;
import org.cloudfoundry.operations.domains.Domains;
import org.cloudfoundry.operations.organizations.Organizations;
import org.cloudfoundry.operations.quotas.Quotas;
import org.cloudfoundry.operations.routes.Routes;
import org.cloudfoundry.operations.services.Services;
import org.cloudfoundry.operations.spacequotas.SpaceQuotas;
import org.cloudfoundry.operations.spaces.Spaces;
import org.cloudfoundry.operations.stacks.Stacks;

/**
 * Main entry point to the Cloud Foundry Operations API
 */
public interface CloudFoundryOperations {

    /**
     * The currently supported Cloud Foundry CLI version
     */
    String SUPPORTED_CLI_VERSION = "6.16.1";

    /**
     * Main entry point to the Cloud Foundry Applications Operations API
     *
     * @return the Cloud Foundry Applications Operations API
     */
    Applications applications();

    /**
     * Main entry point to the Cloud Foundry Buildpacks Operations API
     *
     * @return the Cloud Foundry Buildpacks Operations API
     */
    Buildpacks buildpacks();

    /**
     * Main entry point to the Cloud Foundry Domains Operations API
     *
     * @return the Cloud Foundry Domains Operations API
     */
    Domains domains();

    /**
     * Main entry point to the Cloud Foundry Organizations Operations API
     *
     * @return the Cloud Foundry Organizations Operations API
     */
    Organizations organizations();

    /**
     * Main entry point to the Cloud Foundry Quotas Operations API
     *
     * @return the Cloud Foundry Quotas Operations API
     */
    Quotas quotas();

    /**
     * Main entry point to the Cloud Foundry Routes Operations API
     *
     * @return the Cloud Foundry Routes Operations API
     */
    Routes routes();

    /**
     * Main entry point to the Cloud Foundry Services Operations API
     *
     * @return the Cloud Foundry Services Operations API
     */
    Services services();

    /**
     * Main entry point to the Cloud Foundry Space Quotas Operations API
     *
     * @return the Cloud Foundry Space Quotas Operations API
     */
    SpaceQuotas spaceQuotas();

    /**
     * Main entry point to the Cloud Foundry Spaces Operations API
     *
     * @return the Cloud Foundry Spaces Operations API
     */
    Spaces spaces();

    /**
     * Main entry point to the Cloud Foundry Stacks Operations API
     *
     * @return the Cloud Foundry Stacks Operations API
     */
    Stacks stacks();

}
