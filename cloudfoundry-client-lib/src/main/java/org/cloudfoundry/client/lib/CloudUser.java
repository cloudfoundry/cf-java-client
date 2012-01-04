/*
 * Copyright 2009-2011 the original author or authors.
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

package org.cloudfoundry.client.lib;

/**
 * Cloud Foundry Account information.
 */
public class CloudUser {
    private java.util.Map<String, Object> attributes;
    private String email;
    private boolean admin;

    /**
     * Account constructor. Password will not be set in this object.
     *
     * @param attributes The attributes to set.
     */
    public CloudUser(java.util.Map<String, Object> attributes) {
        this.attributes = attributes;
        this.email = CloudUtil.parse(String.class, attributes.get("email"));
        this.admin = CloudUtil.parse(Boolean.class, attributes.get("admin"));
    }

    /**
     * The email address for this account.
     *
     * @return should never be empty
     */
    public String getEmail() {
        return email;
    }

    /**
     * If this user is an admin.
     *
     * @return true if an admin
     */
    public boolean isAdmin() {
        return admin;
    }
}
