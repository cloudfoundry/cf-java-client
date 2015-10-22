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

package org.cloudfoundry.client.spring.v3.packages;

import org.cloudfoundry.client.RequestValidationException;
import org.cloudfoundry.client.spring.AbstractRestTest;
import org.cloudfoundry.client.v2.CloudFoundryException;
import org.cloudfoundry.client.v3.Hash;
import org.cloudfoundry.client.v3.packages.CopyPackageRequest;
import org.cloudfoundry.client.v3.packages.CopyPackageResponse;
import org.cloudfoundry.client.v3.packages.CreatePackageRequest;
import org.cloudfoundry.client.v3.packages.CreatePackageResponse;
import org.cloudfoundry.client.v3.packages.DeletePackageRequest;
import org.cloudfoundry.client.v3.packages.GetPackageRequest;
import org.cloudfoundry.client.v3.packages.GetPackageResponse;
import org.cloudfoundry.client.v3.packages.ListPackagesRequest;
import org.cloudfoundry.client.v3.packages.ListPackagesResponse;
import org.cloudfoundry.client.v3.packages.StagePackageRequest;
import org.cloudfoundry.client.v3.packages.StagePackageResponse;
import org.cloudfoundry.client.v3.packages.UploadPackageRequest;
import org.cloudfoundry.client.v3.packages.UploadPackageResponse;
import org.junit.Test;
import org.springframework.core.io.ClassPathResource;
import reactor.rx.Streams;

import java.io.IOException;
import java.util.Collections;

import static org.cloudfoundry.client.v3.packages.CreatePackageRequest.PackageType.DOCKER;
import static org.hamcrest.core.StringStartsWith.startsWith;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.springframework.http.HttpMethod.DELETE;
import static org.springframework.http.HttpMethod.GET;
import static org.springframework.http.HttpMethod.POST;
import static org.springframework.http.HttpStatus.CREATED;
import static org.springframework.http.HttpStatus.OK;
import static org.springframework.http.MediaType.MULTIPART_FORM_DATA_VALUE;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.header;

public final class SpringPackagesTest extends AbstractRestTest {

    private final SpringPackages packages = new SpringPackages(this.restTemplate, this.root);

    @Test
    public void copy() {
        mockRequest(new RequestContext()
                .method(POST).path("/v3/apps/test-application-id/packages?source_package_guid=test-source-package-id")
                .status(OK)
                .responsePayload("v3/apps/POST_{id}_packages_response.json"));

        CopyPackageRequest request = new CopyPackageRequest()
                .withApplicationId("test-application-id")
                .withSourcePackageId("test-source-package-id");

        CopyPackageResponse response = Streams.wrap(this.packages.copy(request)).next().get();

        assertEquals("2015-08-06T00:36:55Z", response.getCreatedAt());
        assertNull(response.getError());

        assertEquals("sha1", response.getHash().getType());
        assertNull(response.getHash().getValue());

        assertEquals("126e54c4-811d-4f7a-9a34-804130a75ab2", response.getId());

        assertEquals(2, response.getLinks().size());
        assertNotNull(response.getLink("self"));
        assertNotNull(response.getLink("app"));

        assertEquals("READY", response.getState());
        assertEquals("docker", response.getType());
        assertNull(response.getUpdatedAt());
        assertEquals("docker://cloudfoundry/runtime-ci", response.getUrl());
        verify();
    }

    @Test(expected = CloudFoundryException.class)
    public void copyError() {
        mockRequest(new RequestContext()
                .method(POST).path("/v3/apps/test-application-id/packages?source_package_guid=test-source-package-id")
                .errorResponse());

        CopyPackageRequest request = new CopyPackageRequest()
                .withApplicationId("test-application-id")
                .withSourcePackageId("test-source-package-id");

        Streams.wrap(this.packages.copy(request)).next().get();
    }

    @Test(expected = RequestValidationException.class)
    public void copyInvalidRequest() {
        Streams.wrap(this.packages.copy(new CopyPackageRequest())).next().get();
    }

    @Test
    public void create() {
        mockRequest(new RequestContext()
                .method(POST).path("/v3/apps/test-application-id/packages")
                .requestPayload("v3/apps/POST_{id}_packages_request.json")
                .status(CREATED)
                .responsePayload("v3/apps/POST_{id}_packages_response.json"));

        CreatePackageRequest request = new CreatePackageRequest()
                .withApplicationId("test-application-id")
                .withType(DOCKER)
                .withUrl("docker://cloudfoundry/runtime-ci");

        CreatePackageResponse response = Streams.wrap(this.packages.create(request)).next().get();

        assertEquals("2015-08-06T00:36:55Z", response.getCreatedAt());
        assertNull(response.getError());

        assertEquals("sha1", response.getHash().getType());
        assertNull(response.getHash().getValue());

        assertEquals("126e54c4-811d-4f7a-9a34-804130a75ab2", response.getId());

        assertEquals(2, response.getLinks().size());
        assertNotNull(response.getLink("self"));
        assertNotNull(response.getLink("app"));

        assertEquals("READY", response.getState());
        assertEquals("docker", response.getType());
        assertNull(response.getUpdatedAt());
        assertEquals("docker://cloudfoundry/runtime-ci", response.getUrl());
        verify();
    }

    @Test(expected = CloudFoundryException.class)
    public void createError() {
        mockRequest(new RequestContext()
                .method(POST).path("/v3/apps/test-application-id/packages")
                .requestPayload("v3/apps/POST_{id}_packages_request.json")
                .errorResponse());

        CreatePackageRequest request = new CreatePackageRequest()
                .withApplicationId("test-application-id")
                .withType(DOCKER)
                .withUrl("docker://cloudfoundry/runtime-ci");

        Streams.wrap(this.packages.create(request)).next().get();
    }

    @Test(expected = RequestValidationException.class)
    public void createInvalidRequest() {
        Streams.wrap(this.packages.create(new CreatePackageRequest())).next().get();
    }

    @Test
    public void delete() {
        mockRequest(new RequestContext()
                .method(DELETE).path("/v3/packages/test-id")
                .status(OK));

        DeletePackageRequest request = new DeletePackageRequest()
                .withId("test-id");

        Streams.wrap(this.packages.delete(request)).next().get();

        verify();
    }

    @Test(expected = CloudFoundryException.class)
    public void deleteError() {
        mockRequest(new RequestContext()
                .method(DELETE).path("/v3/packages/test-id")
                .errorResponse());

        DeletePackageRequest request = new DeletePackageRequest()
                .withId("test-id");

        Streams.wrap(this.packages.delete(request)).next().get();
    }

    @Test(expected = RequestValidationException.class)
    public void deleteInvalidRequest() {
        Streams.wrap(this.packages.delete(new DeletePackageRequest())).next().get();
    }

    @Test
    public void get() {
        mockRequest(new RequestContext()
                .method(GET).path("/v3/packages/test-id")
                .status(OK)
                .responsePayload("v3/packages/GET_{id}_response.json"));

        GetPackageRequest request = new GetPackageRequest()
                .withId("test-id");

        GetPackageResponse response = Streams.wrap(this.packages.get(request)).next().get();

        assertEquals("2015-07-27T22:43:15Z", response.getCreatedAt());
        assertNull(response.getError());

        assertEquals("sha1", response.getHash().getType());
        assertNull(response.getHash().getValue());

        assertEquals("guid-9067cc41-b832-4de9-89a2-0987dab65e8e", response.getId());

        assertEquals(5, response.getLinks().size());
        assertNotNull(response.getLink("self"));
        assertNotNull(response.getLink("upload"));
        assertNotNull(response.getLink("download"));
        assertNotNull(response.getLink("stage"));
        assertNotNull(response.getLink("app"));

        assertEquals("AWAITING_UPLOAD", response.getState());
        assertEquals("bits", response.getType());
        assertNull(response.getUpdatedAt());
        assertNull(response.getUrl());
        verify();
    }

    @Test(expected = CloudFoundryException.class)
    public void getError() {
        mockRequest(new RequestContext()
                .method(GET).path("/v3/packages/test-id")
                .errorResponse());

        GetPackageRequest request = new GetPackageRequest()
                .withId("test-id");

        Streams.wrap(this.packages.get(request)).next().get();
    }

    @Test(expected = RequestValidationException.class)
    public void getInvalidRequest() {
        Streams.wrap(this.packages.get(new GetPackageRequest())).next().get();
    }

    @Test
    public void list() {
        mockRequest(new RequestContext()
                .method(GET).path("/v3/packages")
                .status(OK)
                .responsePayload("v3/packages/GET_response.json"));

        ListPackagesRequest request = new ListPackagesRequest();
        ListPackagesResponse response = Streams.wrap(this.packages.list(request)).next().get();

        ListPackagesResponse.Resource resource = response.getResources().get(0);

        assertEquals("2015-07-27T22:43:15Z", resource.getCreatedAt());
        assertNull(resource.getError());
        assertEquals("sha1", resource.getHash().getType());
        assertNull(resource.getHash().getValue());
        assertEquals("guid-84ffc554-5d3a-4ea3-bfeb-d796fa82bf7a", resource.getId());

        assertEquals(5, resource.getLinks().size());
        assertNotNull(resource.getLink("self"));
        assertNotNull(resource.getLink("upload"));
        assertNotNull(resource.getLink("download"));
        assertNotNull(resource.getLink("stage"));
        assertNotNull(resource.getLink("app"));

        assertEquals("AWAITING_UPLOAD", resource.getState());
        assertEquals("bits", resource.getType());
        assertNull(resource.getUpdatedAt());
        assertNull(resource.getUrl());
        verify();
    }

    @Test(expected = CloudFoundryException.class)
    public void listError() {
        mockRequest(new RequestContext()
                .method(GET).path("/v3/packages")
                .errorResponse());

        ListPackagesRequest request = new ListPackagesRequest();

        Streams.wrap(this.packages.list(request)).next().get();
    }

    @Test(expected = RequestValidationException.class)
    public void listInvalidRequest() {
        Streams.wrap(this.packages.list(new ListPackagesRequest().withPage(0))).next().get();
    }

    @Test
    public void stage() {
        mockRequest(new RequestContext()
                .method(POST).path("/v3/packages/test-id/droplets")
                .requestPayload("v3/packages/POST_{id}_droplets_request.json")
                .status(CREATED)
                .responsePayload("v3/packages/POST_{id}_droplets_response.json"));

        StagePackageRequest request = new StagePackageRequest()
                .withBuildpack("http://github.com/myorg/awesome-buildpack")
                .withEnvironmentVariable("CUSTOM_ENV_VAR", "hello")
                .withId("test-id")
                .withStack("cflinuxfs2");

        StagePackageResponse response = Streams.wrap(this.packages.stage(request)).next().get();

        assertEquals("http://buildpack.git.url.com", response.getBuildpack());
        assertEquals("2015-07-27T22:43:30Z", response.getCreatedAt());
        assertEquals(Collections.singletonMap("cloud", "foundry"), response.getEnvironmentVariables());
        assertEquals("example error", response.getError());

        Hash hash = response.getHash();
        assertEquals("sha1", hash.getType());
        assertNull(hash.getValue());

        assertEquals("guid-4dc396dd-9fe3-4b96-847e-d0c63768d5f9", response.getId());

        assertEquals(4, response.getLinks().size());
        assertNotNull(response.getLink("self"));
        assertNotNull(response.getLink("package"));
        assertNotNull(response.getLink("app"));
        assertNotNull(response.getLink("assign_current_droplet"));

        assertNull(response.getProcfile());
        assertEquals("STAGED", response.getState());
        assertNull(response.getUpdatedAt());
        verify();
    }

    @Test(expected = CloudFoundryException.class)
    public void stageError() {
        mockRequest(new RequestContext()
                .method(POST).path("/v3/packages/test-id/droplets")
                .requestPayload("v3/packages/POST_{id}_droplets_request.json")
                .errorResponse());

        StagePackageRequest request = new StagePackageRequest()
                .withBuildpack("http://github.com/myorg/awesome-buildpack")
                .withEnvironmentVariable("CUSTOM_ENV_VAR", "hello")
                .withId("test-id")
                .withStack("cflinuxfs2");

        Streams.wrap(this.packages.stage(request)).next().get();
    }

    @Test(expected = RequestValidationException.class)
    public void stageInvalidRequest() {
        Streams.wrap(this.packages.stage(new StagePackageRequest())).next().get();
    }

    @Test
    public void upload() throws IOException {
        mockRequest(new RequestContext()
                .method(POST).path("/v3/packages/test-id/upload")
                .requestMatcher(header("Content-Type", startsWith(MULTIPART_FORM_DATA_VALUE)))
                .anyRequestPayload()
                .status(CREATED)
                .responsePayload("v3/packages/POST_{id}_upload_response.json"));

        UploadPackageRequest request = new UploadPackageRequest()
                .withFile(new ClassPathResource("v3/packages/test-file").getFile())
                .withId("test-id");

        UploadPackageResponse response = Streams.wrap(this.packages.upload(request)).next().get();

        assertEquals("2015-08-06T00:36:54Z", response.getCreatedAt());
        assertNull(response.getError());

        assertEquals("sha1", response.getHash().getType());
        assertNull(response.getHash().getValue());

        assertEquals("guid-9d6845e9-0dab-41e9-a1fb-48b5b8f35d50", response.getId());

        assertEquals(5, response.getLinks().size());
        assertNotNull(response.getLink("self"));
        assertNotNull(response.getLink("upload"));
        assertNotNull(response.getLink("download"));
        assertNotNull(response.getLink("stage"));
        assertNotNull(response.getLink("app"));

        assertEquals("PROCESSING_UPLOAD", response.getState());
        assertEquals("bits", response.getType());
        assertEquals("2015-08-06T00:36:55Z", response.getUpdatedAt());
        assertNull(response.getUrl());
        verify();
    }

    @Test(expected = CloudFoundryException.class)
    public void uploadError() throws IOException {
        mockRequest(new RequestContext()
                .method(POST).path("/v3/packages/test-id/upload")
                .requestMatcher(header("Content-Type", startsWith(MULTIPART_FORM_DATA_VALUE)))
                .anyRequestPayload()
                .errorResponse());

        UploadPackageRequest request = new UploadPackageRequest()
                .withFile(new ClassPathResource("v3/packages/test-file").getFile())
                .withId("test-id");

        Streams.wrap(this.packages.upload(request)).next().get();
    }

    @Test(expected = RequestValidationException.class)
    public void uploadInvalidRequest() {
        Streams.wrap(this.packages.upload(new UploadPackageRequest())).next().get();
    }

}
