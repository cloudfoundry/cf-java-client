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

package org.cloudfoundry.reactor;

import org.immutables.value.Value;

@Value.Immutable
abstract class _InteractionContext {

    @SuppressWarnings("immutables")
    private volatile boolean done = false;

    abstract TestRequest getRequest();

    abstract TestResponse getResponse();

    final boolean isDone() {
        return this.done;
    }

    final void setDone(boolean done) {
        this.done = done;
    }

}
