package org.cloudfoundry.client.lib.domain;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by eoz on 27-9-16.
 */
/*
 https://apidocs.cloudfoundry.org/242/spaces/list_all_service_instances_for_the_space.html

 resources -> last_operation

 */
public class LastOperation {

    private Date createdAt;
    private String description;
    private String state;
    private Date updatedAt;
    private String type;
    private SimpleDateFormat simpleDateFormat;

    public LastOperation(){
        this.simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'");
    }

    public Date getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(String createdAt) throws ParseException {
        if (createdAt != null) {
            this.createdAt = simpleDateFormat.parse(createdAt);
        }
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public Date getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(String updatedAt) throws ParseException {
        if (updatedAt != null) {
            this.updatedAt = simpleDateFormat.parse(updatedAt);
        }
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }




}
