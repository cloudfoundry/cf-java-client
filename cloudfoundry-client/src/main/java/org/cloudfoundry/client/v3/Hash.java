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

package org.cloudfoundry.client.v3;

/**
 * A hash payload
 *
 * <p><b>This class is NOT threadsafe.</b>
 */
public final class Hash {

    private volatile String type;

    private volatile String value;

    /**
     * Returns the type
     *
     * @return the type
     */
    public String getType() {
        return this.type;
    }

    /**
     * Configure the type
     *
     * @param type the type
     * @return {@code this}
     */
    public Hash withType(String type) {
        this.type = type;
        return this;
    }

    /**
     * Returns the value
     *
     * @return the value
     */
    public String getValue() {
        return this.value;
    }

    /**
     * Configure the value
     *
     * @param value the value
     * @return {@code this}
     */
    public Hash withValue(String value) {
        this.value = value;
        return this;
    }

}
