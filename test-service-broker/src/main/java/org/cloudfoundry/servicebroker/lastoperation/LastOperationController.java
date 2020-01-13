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

package org.cloudfoundry.servicebroker.lastoperation;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@RestController
final class LastOperationController implements LastOperationRepository {

    private final Map<String, OperationType> operations = new ConcurrentHashMap<>();

    @Override
    public void register(String instanceId, OperationType operationType) {
        this.operations.put(instanceId, operationType);
    }

    @GetMapping("/v2/service_instances/{instanceId}/last_operation")
    ResponseEntity<?> lastOperation(@PathVariable String instanceId) {
        OperationType operationType = this.operations.get(instanceId);

        if (OperationType.DEPROVISION == operationType) {
            return ResponseEntity.status(HttpStatus.GONE)
                .body(LastOperationDeprovisionResponse.builder()
                    .build());
        }

        return ResponseEntity.ok()
            .body(LastOperationProvisionUpdateResponse.builder()
                .state(State.SUCCEEDED)
                .build());
    }

}
