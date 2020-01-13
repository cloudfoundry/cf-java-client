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

package org.cloudfoundry.servicebroker.binding;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
final class BindingController {

    @PutMapping("/v2/service_instances/{instanceId}/service_bindings/{bindingId}")
    ResponseEntity<BindResponse> bind() {
        return ResponseEntity.status(HttpStatus.CREATED)
            .body(BindResponse.builder()
                .build());
    }

    @DeleteMapping("/v2/service_instances/{instanceId}/service_bindings/{bindingId}")
    ResponseEntity<UnbindResponse> unbind() {
        return ResponseEntity.ok()
            .body(UnbindResponse.builder()
                .build());
    }

}
