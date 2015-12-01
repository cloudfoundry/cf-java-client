package org.cloudfoundry.test.haash.repository;

import org.cloudfoundry.test.haash.model.Service;
import org.springframework.data.repository.CrudRepository;

public interface ServiceRepository extends CrudRepository<Service, String> {

}
