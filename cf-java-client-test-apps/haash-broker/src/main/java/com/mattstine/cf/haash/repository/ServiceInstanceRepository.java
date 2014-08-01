package com.mattstine.cf.haash.repository;

import com.mattstine.cf.haash.model.ServiceInstance;
import org.springframework.data.repository.CrudRepository;

/**
 * Created by pivotal on 6/26/14.
 */
public interface ServiceInstanceRepository extends CrudRepository<ServiceInstance, String> {
}
