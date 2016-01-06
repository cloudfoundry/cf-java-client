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

package org.cloudfoundry.client.v3.packages;

import org.reactivestreams.Publisher;
import reactor.Mono;

/**
 * Main entry point to the Cloud Foundry Packages Client API
 */
public interface Packages {

    /**
     * Makes the <a href="http://apidocs.cloudfoundry.org/214/packages_%28experimental%29/copy_a_package.html">Copy Package</a> request
     *
     * @param request the Copy Package request
     * @return the response from the Copy Package request
     */
    Mono<CopyPackageResponse> copy(CopyPackageRequest request);

    /**
     * Makes the <a href="http://apidocs.cloudfoundry.org/215/packages_(experimental)/create_a_package.html">Create Package</a> request
     *
     * @param request the Create Package request
     * @return the response from the Create Package request
     */
    Mono<CreatePackageResponse> create(CreatePackageRequest request);

    /**
     * Makes the <a href="http://apidocs.cloudfoundry.org/214/packages_%28experimental%29/delete_a_package.html">Delete Package</a> request
     *
     * @param request the Delete Package request
     * @return the response from the Delete Package request
     */
    Mono<Void> delete(DeletePackageRequest request);

    /**
     * Makes the <a href="http://apidocs.cloudfoundry.org/214/packages_(experimental)/download_the_bits_for_a_package.html">Download the bits for a package</a> request
     *
     * @param request the Download Package request
     * @return the response from the Download Package request
     */
    Publisher<byte[]> download(DownloadPackageRequest request);

    /**
     * Makes the <a href="http://apidocs.cloudfoundry.org/214/packages_(experimental)/get_a_package.html">Get Package</a> request
     *
     * @param request the Get Package request
     * @return the response from the Get Package request
     */
    Mono<GetPackageResponse> get(GetPackageRequest request);

    /**
     * Makes the <a href="http://apidocs.cloudfoundry.org/214/packages_%28experimental%29/list_all_packages.html">List Packages</a> request
     *
     * @param request the List Packages request
     * @return the response from the List Packages request
     */
    Mono<ListPackagesResponse> list(ListPackagesRequest request);

    /**
     * Makes the <a href="http://apidocs.cloudfoundry.org/214/packages_(experimental)/stage_a_package.html">Stage Package</a> request
     *
     * @param request the Stage Package request
     * @return the response from the StagePackage request
     */
    Mono<StagePackageResponse> stage(StagePackageRequest request);

    /**
     * Makes the <a href="http://apidocs.cloudfoundry.org/215/packages_(experimental)/upload_bits_for_a_package_of_type_bits.html"> Upload Package</a> request
     *
     * @param request the Upload Package request
     * @return the response from the Upload Package request
     */
    Mono<UploadPackageResponse> upload(UploadPackageRequest request);

}
