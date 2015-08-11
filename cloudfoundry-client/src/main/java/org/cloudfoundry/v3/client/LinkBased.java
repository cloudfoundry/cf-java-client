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

import java.util.Map;

/**
 * Interface for types that have {@code _link} payloads
 */
public interface LinkBased {

    /**
     * Returns a link for a specified rel
     *
     * @param rel the rel for the link
     * @return a link for a specified rel or {@code null} if the rel does not exist
     */
    Link getLink(String rel);

    /**
     * Returns all links
     *
     * @return all links
     */
    Map<String, Link> getLinks();

}
