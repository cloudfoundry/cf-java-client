package org.cloudfoundry.test.haash.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "services")
public class Service {

    @Column(nullable = false)
    private boolean bindable;

    @Column(nullable = false)
    private String description;

    @Id
    private String id;

    @Column(nullable = false)
    private String name;

    @OneToMany(orphanRemoval = true)
    @JoinColumn(name = "service_id")
    private Set<Plan> plans = new HashSet<>();

    public void addPlan(Plan plan) {
        this.plans.add(plan);
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Set<Plan> getPlans() {
        return plans;
    }

    public void setPlans(Set<Plan> plans) {
        this.plans = plans;
    }

    public boolean isBindable() {
        return bindable;
    }

    public void setBindable(boolean bindable) {
        this.bindable = bindable;
    }
}
