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

package org.cloudfoundry.client.v3.servicebindings;


import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Getter;
import org.cloudfoundry.Validatable;
import org.cloudfoundry.ValidationResult;
import org.cloudfoundry.client.v3.Relationship;

import java.util.Map;

/**
 * The request payload for the Delete Service Binding operation.
 */
@lombok.Data
public final class CreateServiceBindingRequest implements Validatable {

    /**
     * The data
     *
     * @param data the data
     * @return the data
     */
    @Getter(onMethod = @__(@JsonProperty("data")))
    private final Data data;

    /**
     * The relationships
     *
     * @param relationships the relationships
     * @return the relationships
     */
    @Getter(onMethod = @__(@JsonProperty("relationships")))
    private final Relationships relationships;

    /**
     * The type
     *
     * @param type the type
     * @return the type
     */
    @Getter(onMethod = @__(@JsonProperty("type")))
    private final ServiceBindingType type;

    @Builder
    CreateServiceBindingRequest(Data data,
                                Relationships relationships,
                                ServiceBindingType type) {
        this.data = data;
        this.relationships = relationships;
        this.type = type;
    }

    @Override
    public ValidationResult isValid() {
        ValidationResult.ValidationResultBuilder builder = ValidationResult.builder();

        if (this.type == null) {
            builder.message("type must be specified");
        }

        if (this.data != null) {
            builder.messages(this.data.isValid().getMessages());
        }

        if (this.relationships == null) {
            builder.message("relationships must be specified");
        } else {
            builder.messages(this.relationships.isValid().getMessages());
        }

        return builder.build();
    }

    @lombok.Data
    public static final class Data implements Validatable {

        @Getter(onMethod = @__(@JsonProperty("parameters")))
        private final Map<String, Object> parameters;

        @Builder
        public Data(Map<String, Object> parameters) {
            this.parameters = parameters;
        }

        @Override
        public ValidationResult isValid() {
            return ValidationResult.builder().build();
        }

    }

    @lombok.Data
    public static final class Relationships implements Validatable {

        @Getter(onMethod = @__(@JsonProperty("app")))
        private final Relationship application;

        @Getter(onMethod = @__(@JsonProperty("service_instance")))
        private final Relationship serviceInstance;

        @Builder
        public Relationships(Relationship application, Relationship serviceInstance) {
            this.application = application;
            this.serviceInstance = serviceInstance;
        }

        @Override
        public ValidationResult isValid() {
            ValidationResult.ValidationResultBuilder builder = ValidationResult.builder();

            if (this.application == null) {
                builder.message("application relationship must be specified");
            } else {
                builder.messages(this.application.isValid().getMessages());
            }

            if (this.serviceInstance == null) {
                builder.message("service instance relationship must be specified");
            } else {
                builder.messages(this.serviceInstance.isValid().getMessages());
            }

            return builder.build();
        }

    }

}
