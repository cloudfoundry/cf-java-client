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

package org.cloudfoundry.uaa.groups;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.ObjectCodec;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.immutables.value.Value;

import java.io.IOException;
import java.util.Optional;

/**
 * A member of a {@link Group}
 */
@JsonDeserialize(using = _Member.MemberDeserializer.class)
@Value.Immutable
abstract class _Member extends AbstractMember {

    /**
     * Present only if requested with returnEntities; user or group with membership in the group
     */
    abstract Optional<Entity> getEntity();

    static final class MemberDeserializer extends StdDeserializer<Member> {

        private static final long serialVersionUID = 6109722182337225713L;

        MemberDeserializer() {
            super(Member.class);
        }

        @Override
        public Member deserialize(JsonParser p, DeserializationContext ctxt) throws IOException {
            Member.Builder builder = Member.builder();

            ObjectCodec codec = p.getCodec();
            ObjectNode tree = p.readValueAsTree();

            if (tree.has("entity")) {
                JsonNode entity = tree.get("entity");
                String type = tree.get("type").asText();

                if (MemberType.GROUP.getValue().equalsIgnoreCase(type)) {
                    builder.entity(codec.treeToValue(entity, GroupEntity.class));
                } else if (MemberType.USER.getValue().equalsIgnoreCase(type)) {
                    builder.entity(codec.treeToValue(entity, UserEntity.class));
                } else {
                    throw new IllegalArgumentException(String.format("Unknown member type: %s", type));
                }
            }

            builder.memberId(tree.get("value").asText());

            if (tree.has("origin")) {
                builder.origin(tree.get("origin").asText());
            }

            if (tree.has("type")) {
                builder.type(codec.treeToValue(tree.get("type"), MemberType.class));
            }

            return builder.build();
        }
    }

}
