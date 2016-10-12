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

package org.cloudfoundry.reactor;

import org.junit.Test;
import org.reactivestreams.Publisher;
import reactor.test.subscriber.ScriptedSubscriber;

import java.time.Duration;

public abstract class AbstractApiTest<REQ, RSP> extends AbstractRestTest {

    @Test
    public final void success() throws Exception {
        mockRequest(interactionContext());

        ScriptedSubscriber<RSP> subscriber = expectations();
        invoke(validRequest()).subscribe(subscriber);

        subscriber.verify(Duration.ofSeconds(5));
        verify();
    }

    protected abstract ScriptedSubscriber<RSP> expectations();

    protected abstract InteractionContext interactionContext();

    protected abstract Publisher<RSP> invoke(REQ request);

    protected abstract REQ validRequest();

}
