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

package org.cloudfoundry.client.v3;

import org.junit.Test;

import java.util.Arrays;

import static org.assertj.core.api.Assertions.assertThat;

public final class ClientV3ExceptionTest {

    @Test
    public void test() {
        ClientV3Exception exception = new ClientV3Exception(-1,
            Arrays.asList(
                Error.builder()
                    .code(-2)
                    .detail("test-detail-1")
                    .title("test-title-1")
                    .build(),
                Error.builder()
                    .code(-3)
                    .detail("test-detail-2")
                    .title("test-title-2")
                    .build()));

        assertThat(exception)
            .hasNoCause()
            .hasMessage("test-title-1(-2): test-detail-1, test-title-2(-3): test-detail-2")
            .extracting("statusCode").isEqualTo(-1);

        assertThat(exception.getErrors())
            .flatExtracting(Error::getCode, Error::getDetail, Error::getTitle)
            .contains(-2, "test-detail-1", "test-title-1", -3, "test-detail-2", "test-title-2");
    }

}
