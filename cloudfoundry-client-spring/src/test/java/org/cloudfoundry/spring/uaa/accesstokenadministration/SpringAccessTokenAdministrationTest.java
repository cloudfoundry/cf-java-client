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

package org.cloudfoundry.spring.uaa.accesstokenadministration;

import org.cloudfoundry.spring.AbstractApiTest;
import org.cloudfoundry.uaa.accesstokenadministration.GetTokenKeyRequest;
import org.cloudfoundry.uaa.accesstokenadministration.GetTokenKeyResponse;
import reactor.core.publisher.Mono;

import static org.springframework.http.HttpMethod.GET;
import static org.springframework.http.HttpStatus.OK;

public final class SpringAccessTokenAdministrationTest {

    public static final class Get extends AbstractApiTest<GetTokenKeyRequest, GetTokenKeyResponse> {

        private final SpringAccessTokenAdministration accessTokenAdministration = new SpringAccessTokenAdministration(this.restTemplate, this.root, PROCESSOR_GROUP);

        @Override
        protected GetTokenKeyRequest getInvalidRequest() {
            return null;
        }

        @Override
        protected RequestContext getRequestContext() {
            return new RequestContext()
                .method(GET).path("/token_key")
                .status(OK)
                .responsePayload("fixtures/uaa/token_key/GET_response.json");
        }

        @Override
        protected GetTokenKeyResponse getResponse() {
            return GetTokenKeyResponse.builder()
                .algorithm("SHA256withRSA")
                .e("AQAB")
                .keyType("RSA")
                .n("ANJufZdrvYg5zG61x36pDq59nVUN73wSanA7hVCtN3ftT2Rm1ZTQqp5KSCfLMhaaVvJY51sHj" +
                    "+/i4lqUaM9CO32G93fE44VfOmPfexZeAwa8YDOikyTrhP7sZ6A4WUNeC4DlNnJF4zsznU7JxjCkASwpdL6XFwbRSzGkm6b9aM4vIewyclWehJxUGVFhnYEzIQ65qnr38feVP9enOVgQzpKsCJ+xpa8vZ/UrscoG3" +
                    "/IOQM6VnLrGYAyyCGeyU1JXQW/KlNmtA5eJry2Tp+MD6I34/QsNkCArHOfj8H9tXz/oc3/tVkkR252L/Lmp0TtIGfHpBmoITP9h+oKiW6NpyCc=")
                .use("sig")
                .value("-----BEGIN PUBLIC KEY-----\n" +
                    "MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEA0m59l2u9iDnMbrXHfqkO\n" +
                    "rn2dVQ3vfBJqcDuFUK03d+1PZGbVlNCqnkpIJ8syFppW8ljnWweP7+LiWpRoz0I7\n" +
                    "fYb3d8TjhV86Y997Fl4DBrxgM6KTJOuE/uxnoDhZQ14LgOU2ckXjOzOdTsnGMKQB\n" +
                    "LCl0vpcXBtFLMaSbpv1ozi8h7DJyVZ6EnFQZUWGdgTMhDrmqevfx95U/16c5WBDO\n" +
                    "kqwIn7Glry9n9Suxygbf8g5AzpWcusZgDLIIZ7JTUldBb8qU2a0Dl4mvLZOn4wPo\n" +
                    "jfj9Cw2QICsc5+Pwf21fP+hzf+1WSRHbnYv8uanRO0gZ8ekGaghM/2H6gqJbo2nI\n" +
                    "JwIDAQAB\n" +
                    "-----END PUBLIC KEY-----")

                .build();
        }

        @Override
        protected GetTokenKeyRequest getValidRequest() {
            return GetTokenKeyRequest.builder()
                .build();
        }

        @Override
        protected Mono<GetTokenKeyResponse> invoke(GetTokenKeyRequest request) {
            return this.accessTokenAdministration.getTokenKey(request);
        }

    }

}
