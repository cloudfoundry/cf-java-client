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

package org.cloudfoundry.operations;

import org.cloudfoundry.AbstractIntegrationTest;
import org.cloudfoundry.client.v2.CloudFoundryException;
import org.cloudfoundry.operations.domains.CreateDomainRequest;
import org.cloudfoundry.operations.domains.ShareDomainRequest;
import org.cloudfoundry.operations.domains.UnshareDomainRequest;
import org.cloudfoundry.operations.organizations.CreateOrganizationRequest;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import reactor.core.publisher.Mono;

public final class DomainsTest extends AbstractIntegrationTest {

    @Autowired
    private CloudFoundryOperations cloudFoundryOperations;

    @Autowired
    private String organizationName;

    @Test
    public void create() {
        String domainName = getDomainName();

        this.cloudFoundryOperations.domains()
            .create(CreateDomainRequest.builder()
                .domain(domainName)
                .organization(this.organizationName)
                .build())
            .subscribe(testSubscriber());
    }

    @Test
    public void createInvalidDomain() {
        this.cloudFoundryOperations.domains()
            .create(CreateDomainRequest.builder()
                .domain("invalid-domain")
                .organization(this.organizationName)
                .build())
            .after()
            .subscribe(testSubscriber()
                .assertError(CloudFoundryException.class, "CF-DomainInvalid(130001): The domain is invalid: name format"));
    }

    @Test
    public void share() {
        String domainName = getDomainName();
        String targetOrganizationName = getOrganizationName();

        requestCreateOrganization(this.cloudFoundryOperations, targetOrganizationName)
            .after(requestCreateDomain(this.cloudFoundryOperations, domainName, this.organizationName))
            .after(this.cloudFoundryOperations.domains()
                .share(ShareDomainRequest.builder()
                    .domain(domainName)
                    .organization(targetOrganizationName)
                    .build()))
            .subscribe(testSubscriber());
    }

    @Test
    public void unshare() {
        String domainName = getDomainName();
        String targetOrganizationName = getOrganizationName();

        requestCreateOrganization(this.cloudFoundryOperations, targetOrganizationName)
            .after(requestCreateDomain(this.cloudFoundryOperations, domainName, this.organizationName))
            .after(requestShareDomain(this.cloudFoundryOperations, targetOrganizationName, domainName))
            .after(this.cloudFoundryOperations.domains()
                .unshare(UnshareDomainRequest.builder()
                    .domain(domainName)
                    .organization(targetOrganizationName)
                    .build()))
            .subscribe(testSubscriber());
    }

    private static Mono<Void> requestCreateDomain(CloudFoundryOperations cloudFoundryOperations, String domainName, String organizationName) {
        return cloudFoundryOperations.domains()
            .create(CreateDomainRequest.builder()
                .domain(domainName)
                .organization(organizationName)
                .build());
    }

    private static Mono<Void> requestCreateOrganization(CloudFoundryOperations cloudFoundryOperations, String name) {
        return cloudFoundryOperations.organizations()
            .create(CreateOrganizationRequest.builder()
                .organizationName(name)
                .build());
    }

    private static Mono<Void> requestShareDomain(CloudFoundryOperations cloudFoundryOperations, String organizationName, String domainName) {
        return cloudFoundryOperations.domains()
            .share(ShareDomainRequest.builder()
                .domain(domainName)
                .organization(organizationName)
                .build());
    }

}
