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

package org.cloudfoundry.servicebroker.instance;

import org.cloudfoundry.servicebroker.lastoperation.LastOperationRepository;
import org.cloudfoundry.servicebroker.lastoperation.OperationType;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;
import java.util.Optional;

@RestController
final class InstanceController {

    private final LastOperationRepository lastOperationRepository;

    InstanceController(LastOperationRepository lastOperationRepository) {
        this.lastOperationRepository = lastOperationRepository;
    }

    @DeleteMapping("/v2/service_instances/{instanceId}")
    ResponseEntity<?> deprovision(@RequestParam("accepts_incomplete") Optional<Boolean> acceptsIncomplete, @PathVariable String instanceId) {
        if (acceptsIncomplete.orElse(false)) {
            this.lastOperationRepository.register(instanceId, OperationType.DEPROVISION);

            return ResponseEntity.accepted()
                .body(DeprovisionAsyncResponse.builder()
                    .operation("test-operation")
                    .build());
        }

        return ResponseEntity.ok()
            .body(DeprovisionSyncResponse.builder()
                .build());
    }

    @PutMapping("/v2/service_instances/{instanceId}")
    ResponseEntity<?> provision(@PathVariable String instanceId, @RequestBody Map<String, Object> payload) {
        boolean acceptsIncomplete = (boolean) payload.getOrDefault("accepts_incomplete", false);

        if (acceptsIncomplete) {
            this.lastOperationRepository.register(instanceId, OperationType.PROVISION);

            return ResponseEntity.accepted()
                .body(ProvisionAsyncResponse.builder()
                    .build());
        }

        return ResponseEntity.status(HttpStatus.CREATED)
            .body(ProvisionSyncResponse.builder()
                .dashboardUrl(String.format("http://test-dashboard-host/%s", instanceId))
                .build());
    }

    @PatchMapping("/v2/service_instances/{instanceId}")
    ResponseEntity<?> update(@PathVariable String instanceId, @RequestBody Map<String, Object> payload) {
        boolean acceptsIncomplete = (boolean) payload.getOrDefault("accepts_incomplete", false);

        if (acceptsIncomplete) {
            this.lastOperationRepository.register(instanceId, OperationType.UPDATE);

            return ResponseEntity.accepted()
                .body(UpdateAsyncResponse.builder()
                    .build());
        }

        return ResponseEntity.ok()
            .body(UpdateSyncResponse.builder()
                .build());
    }

}
