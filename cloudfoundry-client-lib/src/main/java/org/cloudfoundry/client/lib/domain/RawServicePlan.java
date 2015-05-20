package org.cloudfoundry.client.lib.domain;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class RawServicePlan extends CloudEntity {

    public RawServicePlan(Meta meta, String servicePlanName) {
        super(meta, servicePlanName);
    }

    public UUID getServiceGuid() {
        return serviceGuid;
    }

    public void setServiceGuid(UUID serviceGuid) {
        this.serviceGuid = serviceGuid;
    }

    public String getServiceName() {
        return serviceName;
    }

    public void setServiceName(String serviceName) {
        this.serviceName = serviceName;
    }

    public List<RawServiceInstance> getInstances() {
        return instances;
    }

    public void setInstances(List<RawServiceInstance> instances) {
        this.instances = instances;
    }

    UUID serviceGuid;
    String serviceName;
    private List<RawServiceInstance> instances = new ArrayList<>();


}
