package org.cloudfoundry.test.haash.repository;

import org.cloudfoundry.test.haash.model.ServiceBinding;
import org.springframework.data.repository.CrudRepository;

public interface ServiceBindingRepository extends CrudRepository<ServiceBinding,String> {
}
