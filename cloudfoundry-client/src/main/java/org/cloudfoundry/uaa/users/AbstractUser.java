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

package org.cloudfoundry.uaa.users;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

/**
 * The entity response payload for User
 */
public abstract class AbstractUser {

    /**
     * Whether the user is active
     */
    @JsonProperty("active")
    public abstract Boolean getActive();

    /**
     * The approvals for the user
     */
    @JsonProperty("approvals")
    public abstract List<Approval> getApproval();

    /**
     * The emails for the user
     */
    @JsonProperty("emails")
    public abstract List<Email> getEmail();

    /**
     * The external id
     */
    @JsonProperty("externalId")
    public abstract String getExternalId();

    /**
     * The groups for the user
     */
    @JsonProperty("groups")
    public abstract List<Group> getGroup();

    /**
     * The id
     */
    @JsonProperty("id")
    public abstract String getId();

    /**
     * Metadata for the result
     */
    @JsonProperty("meta")
    public abstract Meta getMeta();

    /**
     * The user's name
     */
    @JsonProperty("name")
    public abstract Name getName();

    /**
     * The identity provider that authenticated this user
     */
    @JsonProperty("origin")
    public abstract String getOrigin();

    /**
     * The timestamp when the user's password was last modified
     */
    @JsonProperty("passwordLastModified")
    public abstract String getPasswordLastModified();

    /**
     * The schemas
     */
    @JsonProperty("schemas")
    public abstract List<String> getSchemas();

    /**
     * The user name
     */
    @JsonProperty("userName")
    public abstract String getUserName();

    /**
     * Whether the user's email is verified
     */
    @JsonProperty("verified")
    public abstract Boolean getVerified();

    /**
     * The zone id the user belongs to
     */
    @JsonProperty("zoneId")
    public abstract String getZoneId();

}
