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

package org.cloudfoundry.doppler;

import lombok.ToString;

/**
 * The type of peer handling request
 */
@ToString
public enum PeerType {

    /**
     * The request is made by this process
     */
    CLIENT,

    /**
     * The request is received by this process
     */
    SERVER;

    static PeerType dropsonde(org.cloudfoundry.dropsonde.events.PeerType dropsonde) {
        switch (dropsonde) {
            case Client:
                return CLIENT;
            case Server:
                return SERVER;
            default:
                throw new IllegalArgumentException("Unknown Peer Type");
        }
    }

}
