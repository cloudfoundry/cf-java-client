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

package org.cloudfoundry.operations;

import reactor.rx.Stream;
import reactor.rx.Streams;

abstract class AbstractOperations {

    private final String organizationId;

    private final String spaceId;

    protected AbstractOperations(String organizationId, String spaceId) {
        this.organizationId = organizationId;
        this.spaceId = spaceId;
    }

    protected final Stream<String> getTargetedOrganization() {
        if (AbstractOperations.this.organizationId == null) {
            return Streams.fail(new IllegalStateException("No organization targeted"));
        } else {
            return Streams.just(this.organizationId);
        }
    }

    protected final Stream<String> getTargetedSpace() {
        if (AbstractOperations.this.spaceId == null) {
            return Streams.fail(new IllegalStateException("No space targeted"));
        } else {
            return Streams.just(AbstractOperations.this.spaceId);
        }
    }

}

