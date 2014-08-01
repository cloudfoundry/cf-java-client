package com.mattstine.cf.haash.controller;

import com.mattstine.cf.haash.haash.HaashService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
public class HaashController {

    @Autowired
    HaashService service;


    @RequestMapping(value = "/HaaSh/{instanceId}/{key}", method = RequestMethod.PUT)
    public ResponseEntity<String> put(@PathVariable("instanceId") String instanceId,
                                      @PathVariable("key") String key,
                                      @RequestBody String value) {
        service.put(instanceId, key, value);
        return new ResponseEntity<>("{}", HttpStatus.CREATED);
    }

    @RequestMapping(value = "/HaaSh/{instanceId}/{key}", method = RequestMethod.GET)
    public ResponseEntity<Object> get(@PathVariable("instanceId") String instanceId,
                                      @PathVariable("key") String key) {
        Object result = service.get(instanceId, key);
        if (result != null) {
            return new ResponseEntity<>(result, HttpStatus.OK);
        } else {
            return new ResponseEntity<Object>("{}", HttpStatus.NOT_FOUND);
        }
    }

    @RequestMapping(value = "/HaaSh/{instanceId}/{key}", method = RequestMethod.DELETE)
    public ResponseEntity<String> delete(@PathVariable("instanceId") String instanceId,
                                         @PathVariable("key") String key) {
        Object result = service.get(instanceId, key);
        if (result != null) {
            service.delete(instanceId, key);
            return new ResponseEntity<>("{}", HttpStatus.OK);
        } else {
            return new ResponseEntity<>("{}", HttpStatus.GONE);
        }
    }
}
