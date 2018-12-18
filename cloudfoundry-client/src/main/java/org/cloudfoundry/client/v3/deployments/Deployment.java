/*
 * Copyright 2013-2018 the original author or authors.
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

package org.cloudfoundry.client.v3.deployments;

import com.fasterxml.jackson.annotation.JsonProperty;
import org.cloudfoundry.Nullable;
import org.cloudfoundry.client.v3.Relationship;
import org.cloudfoundry.client.v3.Resource;
import org.cloudfoundry.client.v3.ToOneRelationship;

import java.util.List;

/**
 * Base class for responses that are deployments
 */
public abstract class Deployment extends Resource {

    /**
     * The state of the deployment
     */
    @JsonProperty("state")
    public abstract DeploymentState getState();

    /**
     * The relationships
     */
    @JsonProperty("relationships")
    @Nullable
    abstract DeploymentRelationships getRelationships();

    /**
     * The app the deployment is updating
     */
    @JsonProperty("app")
    @Nullable
    abstract ToOneRelationship getApp();

    /**
     * The droplet the deployment is transitioning the app to
     */
    @JsonProperty("droplet")
    @Nullable
    abstract Relationship getDroplet();

    /**
     * The app’s current droplet before the deployment was created
     */
    @JsonProperty("previous_droplet")
    @Nullable
    abstract Relationship getPreviousDroplet();

    /**
     * The revision the deployment is transitioning the app to
     */
    @JsonProperty("revision")
    @Nullable
    abstract Revision getRevision();

    /**
     * The revision the deployment is transitioning the app to
     */
    @JsonProperty("new_processes")
    @Nullable
    abstract List<Process> getNewProcesses();

}
