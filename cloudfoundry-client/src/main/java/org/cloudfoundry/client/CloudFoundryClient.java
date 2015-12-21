/*
 * Copyright 2013-2015 the original author or authors.
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

package org.cloudfoundry.client;

import org.cloudfoundry.client.v2.applications.ApplicationsV2;
import org.cloudfoundry.client.v2.domains.Domains;
import org.cloudfoundry.client.v2.events.Events;
import org.cloudfoundry.client.v2.info.Info;
import org.cloudfoundry.client.v2.organizations.Organizations;
import org.cloudfoundry.client.v2.routes.Routes;
import org.cloudfoundry.client.v2.serviceinstances.ServiceInstances;
import org.cloudfoundry.client.v2.spacequotadefinitions.SpaceQuotaDefinitions;
import org.cloudfoundry.client.v2.spaces.Spaces;
import org.cloudfoundry.client.v3.applications.ApplicationsV3;
import org.cloudfoundry.client.v3.droplets.Droplets;
import org.cloudfoundry.client.v3.packages.Packages;

/**
 * Main entry point to the Cloud Foundry Client API
 */
public interface CloudFoundryClient {

    /**
     * The currently supported Cloud Controller API version
     */
    String SUPPORTED_API_VERSION = "2.35.0";

    /**
     * Main entry point to the Cloud Foundry Applications V2 Client API
     *
     * @return the Cloud Foundry Application V2 Client API
     */
    ApplicationsV2 applicationsV2();

    /**
     * Main entry point to the Cloud Foundry Applications V3 Client API
     *
     * @return the Cloud Foundry Application V3 Client API
     */
    ApplicationsV3 applicationsV3();

    /**
     * Main entry point to the Cloud Foundry Domains Client API
     *
     * @return the Cloud Foundry Domains Client API
     */
    Domains domains();

    /**
     * Main entry point to the Cloud Foundry Droplets Client API
     *
     * @return the Cloud Foundry Application Droplets Client API
     */
    Droplets droplets();

    /**
     * Main entry point to the Cloud Foundry Events Client API
     *
     * @return the Cloud Foundry Application Events Client API
     */
    Events events();

    /**
     * Main entry point to the Cloud Foundry Info Client API
     *
     * @return the Cloud Foundry Info Client API
     */
    Info info();

    /**
     * Main entry point to the Cloud Foundry Organizations Client API
     *
     * @return the Cloud Foundry Organizations Client API
     */
    Organizations organizations();

    /**
     * Main entry point to the Cloud Foundry Packages Client API
     *
     * @return the Cloud Foundry Packages Client API
     */
    Packages packages();

    /**
     * Main entry point to the Cloud Foundry Routes Client API
     *
     * @return the Cloud Foundry Packages Client API
     */
    Routes routes();

    /**
     * Main entry point to the Cloud Foundry Service Instances Client API
     *
     * @return the Cloud Foundry Service Instances Client API
     */
    ServiceInstances serviceInstances();

    /**
     * Main entry point to the Cloud Foundry Space Quota Definitions Client API
     *
     * @return the Cloud Foundry Space Quota Definitions Client API
     */
    SpaceQuotaDefinitions spaceQuotaDefinitions();

    /**
     * Main entry point to the Cloud Foundry Spaces Client API
     *
     * @return the Cloud Foundry Space Client API
     */
    Spaces spaces();

}
