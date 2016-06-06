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

package org.cloudfoundry.uaa.identityzones;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;
import org.cloudfoundry.Nullable;
import org.immutables.value.Value;

import java.io.IOException;

/**
 * The payload for the identity zone links
 */
@JsonDeserialize
@Value.Immutable
abstract class _Links {

    /**
     * The logout link
     */
    @JsonDeserialize(using = LogoutLinkDeserializer.class)
    @JsonProperty("logout")
    @Nullable
    abstract LogoutLink getLogout();

    /**
     * The self service link
     */
    @JsonDeserialize(using = SelfServiceLinkDeserializer.class)
    @JsonProperty("selfService")
    @Nullable
    abstract SelfServiceLink getSelfService();

    static final class LogoutLinkDeserializer extends StdDeserializer<LogoutLink> {


        LogoutLinkDeserializer() {
            super(LogoutLink.class);
        }

        @SuppressWarnings("deprecation")
        @Override
        public LogoutLink deserialize(JsonParser p, DeserializationContext ctx) throws IOException {
            return p.readValueAs(LogoutLink.class);
        }
    }

    static final class SelfServiceLinkDeserializer extends StdDeserializer<SelfServiceLink> {


        SelfServiceLinkDeserializer() {
            super(SelfServiceLink.class);
        }

        @SuppressWarnings("deprecation")
        @Override
        public SelfServiceLink deserialize(JsonParser p, DeserializationContext ctx) throws IOException {
            return p.readValueAs(SelfServiceLink.class);
        }
    }

}
