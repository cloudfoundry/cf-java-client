package com.mattstine.cf.haash.model;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name="service_instances")
@JsonAutoDetect(getterVisibility = JsonAutoDetect.Visibility.NONE)
public class ServiceInstance {
    @Id
    private String id;

    @JsonSerialize
    @JsonProperty("service_id")
    @Column(nullable = false)
    private String serviceId;

    @JsonSerialize
    @JsonProperty("plan_id")
    @Column(nullable = false)
    private String planId;

    @JsonSerialize
    @JsonProperty("organization_guid")
    @Column(nullable = false)
    private String organizationGuid;

    @JsonSerialize
    @JsonProperty("space_guid")
    @Column(nullable = false)
    private String spaceGuid;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getServiceId() {
        return serviceId;
    }

    public void setServiceId(String serviceId) {
        this.serviceId = serviceId;
    }

    public String getPlanId() {
        return planId;
    }

    public void setPlanId(String planId) {
        this.planId = planId;
    }

    public String getOrganizationGuid() {
        return organizationGuid;
    }

    public void setOrganizationGuid(String organizationGuid) {
        this.organizationGuid = organizationGuid;
    }

    public String getSpaceGuid() {
        return spaceGuid;
    }

    public void setSpaceGuid(String spaceGuid) {
        this.spaceGuid = spaceGuid;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        ServiceInstance that = (ServiceInstance) o;

        if (!id.equals(that.id)) return false;
        if (!organizationGuid.equals(that.organizationGuid)) return false;
        if (!planId.equals(that.planId)) return false;
        if (!serviceId.equals(that.serviceId)) return false;
        if (!spaceGuid.equals(that.spaceGuid)) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = id.hashCode();
        result = 31 * result + serviceId.hashCode();
        result = 31 * result + planId.hashCode();
        result = 31 * result + organizationGuid.hashCode();
        result = 31 * result + spaceGuid.hashCode();
        return result;
    }
}
