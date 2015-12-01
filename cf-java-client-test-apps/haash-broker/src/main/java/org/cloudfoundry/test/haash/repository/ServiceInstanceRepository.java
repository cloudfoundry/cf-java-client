package org.cloudfoundry.test.haash.repository;

import org.cloudfoundry.test.haash.model.ServiceInstance;
import org.springframework.data.repository.CrudRepository;

/**
 * Created by pivotal on 6/26/14.
 */
public interface ServiceInstanceRepository extends CrudRepository<ServiceInstance, String> {

}
