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

package org.cloudfoundry.client.v3.packages;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Main entry point to the Cloud Foundry Packages Client API
 */
public interface Packages {

    /**
     * Makes the <a href="https://v3-apidocs.cloudfoundry.org/version/3.27.0/index.html#copy-a-package">Copy Package</a> request
     *
     * @param request the Copy Package request
     * @return the response from the Copy Package request
     */
    Mono<CopyPackageResponse> copy(CopyPackageRequest request);

    /**
     * Makes the <a href="https://v3-apidocs.cloudfoundry.org/version/3.27.0/index.html#create-a-package">Create Package</a> request
     *
     * @param request the Create Package request
     * @return the response from the Create Package request
     */
    Mono<CreatePackageResponse> create(CreatePackageRequest request);

    /**
     * Makes the <a href="https://v3-apidocs.cloudfoundry.org/version/3.27.0/index.html#delete-a-package">Delete Package</a> request
     *
     * @param request the Delete Package request
     * @return the response from the Delete Package request
     */
    Mono<String> delete(DeletePackageRequest request);

    /**
     * Makes the <a href="https://v3-apidocs.cloudfoundry.org/version/3.27.0/index.html#download-package-bits">Download the bits for a package</a> request
     *
     * @param request the Download Package request
     * @return the response from the Download Package request
     */
    Flux<byte[]> download(DownloadPackageRequest request);

    /**
     * Makes the <a href="https://v3-apidocs.cloudfoundry.org/version/3.27.0/index.html#get-a-package">Get Package</a> request
     *
     * @param request the Get Package request
     * @return the response from the Get Package request
     */
    Mono<GetPackageResponse> get(GetPackageRequest request);

    /**
     * Makes the <a href="https://v3-apidocs.cloudfoundry.org/version/3.27.0/index.html#list-packages">List Packages</a> request
     *
     * @param request the List Packages request
     * @return the response from the List Packages request
     */
    Mono<ListPackagesResponse> list(ListPackagesRequest request);

    /**
     * Makes the <a href="https://v3-apidocs.cloudfoundry.org/version/3.27.0/#list-droplets-for-a-package">List Droplets</a> request
     *
     * @param request the List Droplets request
     * @return the response from the List Droplets request
     */
    Mono<ListPackageDropletsResponse> listDroplets(ListPackageDropletsRequest request);

    /**
     * Makes the <a href="https://v3-apidocs.cloudfoundry.org/version/3.27.0/index.html#upload-package-bits"> Upload Package</a> request
     *
     * @param request the Upload Package request
     * @return the response from the Upload Package request
     */
    Mono<UploadPackageResponse> upload(UploadPackageRequest request);

}
