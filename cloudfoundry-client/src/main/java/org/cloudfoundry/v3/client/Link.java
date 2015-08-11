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

package org.cloudfoundry.v3.client;

/**
 * A link payload. By default it uses {@code GET} for the {@code method}
 *
 * <p><b>This class is NOT threadsafe.</b>
 */
public final class Link implements Validatable {

    private volatile String href;

    private volatile String method = "GET";

    /**
     * Returns the href
     *
     * @return the href
     */
    public String getHref() {
        return this.href;
    }

    /**
     * Configure the href
     *
     * @param href the href
     * @return {@code this}
     */
    public Link withHref(String href) {
        this.href = href;
        return this;
    }

    /**
     * Returns the method
     *
     * @return the method
     */
    public String getMethod() {
        return this.method;
    }

    /**
     * Configure the method
     *
     * @param method the method
     * @return {@code this}
     */
    public Link withMethod(String method) {
        this.method = method;
        return this;
    }

    @Override
    public ValidationResult isValid() {
        ValidationResult result = new ValidationResult();

        if (this.href == null) {
            result.invalid("href must be specified");
        }

        if (this.method == null) {
            result.invalid("method must be specified");
        }

        return result;
    }

}
