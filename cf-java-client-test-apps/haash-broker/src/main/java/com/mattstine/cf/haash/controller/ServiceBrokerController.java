package com.mattstine.cf.haash.controller;

import com.mattstine.cf.haash.haash.HaashService;
import com.mattstine.cf.haash.model.Credentials;
import com.mattstine.cf.haash.model.Service;
import com.mattstine.cf.haash.model.ServiceBinding;
import com.mattstine.cf.haash.model.ServiceInstance;
import com.mattstine.cf.haash.repository.ServiceBindingRepository;
import com.mattstine.cf.haash.repository.ServiceInstanceRepository;
import com.mattstine.cf.haash.repository.ServiceRepository;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.Cloud;
import org.springframework.cloud.app.ApplicationInstanceInfo;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@RestController
public class ServiceBrokerController {

    Log log = LogFactory.getLog(ServiceBrokerController.class);

    @Autowired
    ServiceRepository serviceRepository;

    @Autowired
    ServiceInstanceRepository serviceInstanceRepository;

    @Autowired
    ServiceBindingRepository serviceBindingRepository;

    @Autowired
    HaashService haashService;

    @Autowired
    Cloud cloud;

    @RequestMapping("/v2/catalog")
    public Map<String, Iterable<Service>> catalog() {
        Map<String, Iterable<Service>> wrapper = new HashMap<>();
        wrapper.put("services", serviceRepository.findAll());
        return wrapper;
    }

    @RequestMapping(value = "/v2/service_instances/{id}", method = RequestMethod.PUT)
    public ResponseEntity<String> create(@PathVariable("id") String id, @RequestBody ServiceInstance serviceInstance) {
        serviceInstance.setId(id);

        boolean exists = serviceInstanceRepository.exists(id);

        if (exists) {
            ServiceInstance existing = serviceInstanceRepository.findOne(id);
            if (existing.equals(serviceInstance)) {
                return new ResponseEntity<>("{}", HttpStatus.OK);
            } else {
                return new ResponseEntity<>("{}", HttpStatus.CONFLICT);
            }
        } else {
            serviceInstanceRepository.save(serviceInstance);
            haashService.create(id);
            return new ResponseEntity<>("{}", HttpStatus.CREATED);
        }
    }

    @RequestMapping(value = "/v2/service_instances/{instanceId}/service_bindings/{id}", method = RequestMethod.PUT)
    public ResponseEntity<Object> createBinding(@PathVariable("instanceId") String instanceId,
                                                @PathVariable("id") String id,
                                                @RequestBody ServiceBinding serviceBinding) {
        if (!serviceInstanceRepository.exists(instanceId)) {
            return new ResponseEntity<Object>("{\"description\":\"Service instance " + instanceId + " does not exist!\"", HttpStatus.BAD_REQUEST);
        }

        serviceBinding.setId(id);
        serviceBinding.setInstanceId(instanceId);

        boolean exists = serviceBindingRepository.exists(id);

        if (exists) {
            ServiceBinding existing = serviceBindingRepository.findOne(id);
            if (existing.equals(serviceBinding)) {
                return new ResponseEntity<Object>(wrapCredentials(existing.getCredentials()), HttpStatus.OK);
            } else {
                return new ResponseEntity<Object>("{}", HttpStatus.CONFLICT);
            }
        } else {
            Credentials credentials = new Credentials();
            credentials.setId(UUID.randomUUID().toString());
            credentials.setUri("http://" + myUri() + "/HaaSh/" + instanceId);
            credentials.setUsername("warreng");
            credentials.setPassword("natedogg");
            serviceBinding.setCredentials(credentials);
            serviceBindingRepository.save(serviceBinding);
            return new ResponseEntity<Object>(wrapCredentials(credentials), HttpStatus.CREATED);
        }
    }

    @RequestMapping(value = "/v2/service_instances/{instanceId}/service_bindings/{id}", method = RequestMethod.DELETE)
    public ResponseEntity<String> deleteBinding(@PathVariable("instanceId") String instanceId,
                                                @PathVariable("id") String id,
                                                @RequestParam("service_id") String serviceId,
                                                @RequestParam("plan_id") String planId) {
        boolean exists = serviceBindingRepository.exists(id);

        if (exists) {
            serviceBindingRepository.delete(id);
            return new ResponseEntity<>("{}", HttpStatus.OK);
        } else {
            return new ResponseEntity<>("{}", HttpStatus.GONE);
        }
    }

    @RequestMapping(value = "/v2/service_instances/{id}", method = RequestMethod.DELETE)
    public ResponseEntity<String> delete(@PathVariable("id") String id,
                                         @RequestParam("service_id") String serviceId,
                                         @RequestParam("plan_id") String planId) {
        boolean exists = serviceRepository.exists(id);

        if (exists) {
            serviceRepository.delete(id);
            haashService.delete(id);
            return new ResponseEntity<>("{}", HttpStatus.OK);
        } else {
            return new ResponseEntity<>("{}", HttpStatus.GONE);
        }
    }

    private String myUri() {
        ApplicationInstanceInfo applicationInstanceInfo = cloud.getApplicationInstanceInfo();
        List<Object> uris = (List<Object>) applicationInstanceInfo.getProperties().get("uris");
        return uris.get(0).toString();
    }

    private Map<String, Object> wrapCredentials(Credentials credentials) {
        Map<String, Object> wrapper = new HashMap<>();
        wrapper.put("credentials", credentials);
        return wrapper;
    }

    public void setServiceRepository(ServiceRepository serviceRepository) {
        this.serviceRepository = serviceRepository;
    }
}
