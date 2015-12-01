package org.cloudfoundry.client.lib.domain;

/**
 * @author Mark Seidenstricker
 */

import org.codehaus.jackson.annotate.JsonAutoDetect;
import org.codehaus.jackson.annotate.JsonAutoDetect.Visibility;

import java.util.Date;
import java.util.UUID;

@JsonAutoDetect(fieldVisibility = Visibility.ANY, getterVisibility = Visibility.NONE, creatorVisibility = Visibility
        .NONE)
public class CloudEvent extends CloudEntity {

    private String actee;

    private String actee_name;

    private String actee_type;

    private String actor;

    private String actor_name;

    private String actor_type;

    private Date timestamp;

    private String type;

    public CloudEvent(Meta meta, String name) {
        super(meta, name);
    }

    public String getActee() {
        return actee;
    }

    public void setActee(String actee) {
        this.actee = actee;
    }

    public UUID getActeeGuid() {
        try {
            return UUID.fromString(actee);
        } catch (IllegalArgumentException e) {
            return null;
        }
    }

    public String getActeeName() {
        return actee_name;
    }

    public void setActeeName(String acteeName) {
        this.actee_name = acteeName;
    }

    public String getActeeType() {
        return actee_type;
    }

    public void setActeeType(String acteeType) {
        this.actee_type = acteeType;
    }

    public String getActor() {
        return actor;
    }

    public void setActor(String actor) {
        this.actor = actor;
    }

    public UUID getActorGuid() {
        try {
            return UUID.fromString(actor);
        } catch (IllegalArgumentException e) {
            return null;
        }
    }

    public String getActorName() {
        return actor_name;
    }

    public void setActorName(String actorName) {
        this.actor_name = actorName;
    }

    public String getActorType() {
        return actor_type;
    }

    public void setActorType(String actorType) {
        this.actor_type = actorType;
    }

    public Date getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Date timestamp) {
        this.timestamp = timestamp;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

}
