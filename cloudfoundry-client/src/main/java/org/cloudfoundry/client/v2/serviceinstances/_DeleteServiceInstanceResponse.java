/*
 * Copyright 2013-2017 the original author or authors.
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

package org.cloudfoundry.client.v2.serviceinstances;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.databind.DatabindContext;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonTypeIdResolver;
import com.fasterxml.jackson.databind.jsontype.impl.TypeIdResolverBase;
import org.cloudfoundry.Nullable;
import org.cloudfoundry.client.v2.Resource;
import org.cloudfoundry.client.v2.jobs.JobEntity;
import org.immutables.value.Value;

import java.io.IOException;

/**
 * The response for the Delete Service Instance operation
 */
@JsonDeserialize
@Value.Immutable
abstract class _DeleteServiceInstanceResponse extends Resource<Object> {

    /**
     * The resource's entity
     */
    @JsonProperty("entity")
    @JsonTypeIdResolver(_DeleteServiceInstanceResponse.DeleteServiceInstanceResponseTypeIdResolver.class)
    @JsonTypeInfo(use = JsonTypeInfo.Id.CUSTOM, include = JsonTypeInfo.As.EXISTING_PROPERTY, property = "type", defaultImpl = JobEntity.class, visible = true)
    @Nullable
    public abstract Object getEntity();

    static final class DeleteServiceInstanceResponseTypeIdResolver extends TypeIdResolverBase {

        @Override
        public JsonTypeInfo.Id getMechanism() {
            return null;
        }

        @Override
        public String idFromValue(Object value) {
            return null;
        }

        @Override
        public String idFromValueAndType(Object value, Class<?> suggestedType) {
            return null;
        }

        @Override
        public JavaType typeFromId(DatabindContext context, String id) throws IOException {
            return context.constructType(ServiceInstanceEntity.class);
        }

    }

}
