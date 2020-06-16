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

package org.cloudfoundry.client.v3.auditevents;

import com.fasterxml.jackson.annotation.JsonProperty;
import org.cloudfoundry.AllowNulls;
import org.cloudfoundry.Nullable;
import org.cloudfoundry.client.v3.Relationship;
import org.cloudfoundry.client.v3.Resource;

import java.util.Map;

/**
 * Base class for responses that are audit events
 */
public abstract class AuditEvent extends Resource {

    /**
     * The event actor
     */
    @JsonProperty("actor")
    @Nullable
    public abstract AuditEventActor getAuditEventActor();

    /**
     * The event target
     */
    @JsonProperty("target")
    @Nullable
    public abstract AuditEventTarget getAuditEventTarget();

    /**
     * Additional information about event
     */
    @AllowNulls
    @JsonProperty("data")
    @Nullable
    public abstract Map<String, Object> getData();

    /**
     * The organization where the event occurred
     */
    @JsonProperty("organization")
    @Nullable
    public abstract Relationship getOrganizationRelationship();

    /**
     * The space where the event occurred.
     */
    @JsonProperty("space")
    @Nullable
    public abstract Relationship getSpaceRelationship();

    /**
     * The event type
     */
    @JsonProperty("type")
    @Nullable
    public abstract String getType();

}
