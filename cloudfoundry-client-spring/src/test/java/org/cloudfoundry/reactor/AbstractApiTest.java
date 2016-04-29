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

import org.cloudfoundry.util.RequestValidationException;
import org.cloudfoundry.util.test.TestSubscriber;
import org.junit.Test;
import org.reactivestreams.Publisher;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.Duration;

public abstract class AbstractApiTest<REQ, RSP> extends AbstractRestTest {

    protected final TestSubscriber<RSP> testSubscriber = new TestSubscriber<>();

    @Test
    public final void invalidRequest() throws Exception {
        REQ request = getInvalidRequest();
        if (request == null) {
            return;
        }

        this.testSubscriber.assertError(RequestValidationException.class, null); // ignore message
        invoke(request).subscribe(this.testSubscriber);

        this.testSubscriber.verify(Duration.ofSeconds(5));
        verify();
    }

    @Test
    public final void success() throws Exception {
        Publisher<RSP> responses = getResponses();
        assertions(this.testSubscriber, responses);

        mockRequest(getInteractionContext());
        invoke(getValidRequest()).subscribe(this.testSubscriber);

        this.testSubscriber.verify(Duration.ofSeconds(5));
        verify();
    }

    protected void assertions(TestSubscriber<RSP> testSubscriber, Publisher<RSP> expected) {
        if (expected != null) {
            Flux.from(expected)
                .subscribe(testSubscriber::assertEquals);
        }
    }

    protected abstract InteractionContext getInteractionContext();

    protected abstract REQ getInvalidRequest();

    protected abstract RSP getResponse();

    protected Publisher<RSP> getResponses() {
        return Mono.just(getResponse());
    }

    protected abstract REQ getValidRequest() throws Exception;

    protected abstract Publisher<RSP> invoke(REQ request);

}
