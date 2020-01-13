/*
 * Copyright 2013-2020 the original author or authors.
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
import org.cloudfoundry.Nullable;

import java.util.List;

/**
 * The summary entity response payload for User
 */
public abstract class AbstractUserSummary extends AbstractUserId {

    /**
     * Whether the user is active
     */
    @JsonProperty("active")
    public abstract Boolean getActive();

    /**
     * The emails for the user
     */
    @JsonProperty("emails")
    public abstract List<Email> getEmail();

    /**
     * The unix epoch timestamp of when the user last authenticated
     */
    @JsonProperty("lastLogonTime")
    @Nullable
    public abstract Long getLastLogonTime();

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
     * The timestamp when the user's password was last modified
     */
    @JsonProperty("passwordLastModified")
    public abstract String getPasswordLastModified();

    /**
     * The phone numbers for the user
     */
    @JsonProperty("phoneNumbers")
    public abstract List<PhoneNumber> getPhoneNumbers();

    /**
     * The unix epoch timestamp of when the user last authenticated
     */
    @JsonProperty("previousLogonTime")
    @Nullable
    public abstract Long getPreviousLoginTime();

    /**
     * The schemas
     */
    @JsonProperty("schemas")
    public abstract List<String> getSchemas();

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
