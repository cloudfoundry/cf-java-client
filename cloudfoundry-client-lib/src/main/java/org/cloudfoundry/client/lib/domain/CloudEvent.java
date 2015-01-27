package org.cloudfoundry.client.lib.domain;

/**
 * @author Mark Seidenstricker
 */

import static org.cloudfoundry.client.lib.util.CloudUtil.parse;

import java.util.*;

import org.codehaus.jackson.annotate.JsonAutoDetect;
import org.codehaus.jackson.annotate.JsonAutoDetect.Visibility;

@JsonAutoDetect(fieldVisibility = Visibility.ANY, getterVisibility = Visibility.NONE, creatorVisibility = Visibility.NONE)
public class CloudEvent extends CloudEntity {

    private static String name = "name";

    private String type;
    private UUID actor;
    private String actor_type;
    private String actor_name;
    private UUID actee;
    private String actee_type;
    private String actee_name;
    private Date timestamp;
//    private String instance;
//    private int index;
//    private int exit_status;
//    private String exit_description;
//    private String reason;
//    private String space_guid;
//    private String organization_guid;


    public CloudEvent(Meta meta, String name) {
        super(meta, name);
    }

//    public CloudEvent(String type, UUID actor, String actor_type, String actor_name,
//                      String actee, String actee_type, String actee_name, String timestamp,
//                      String instance, int index, int exit_status, String exit_description,
//                      String reason) {
//        super(CloudEntity.Meta.defaultMeta(), name);
//        this.type = type;
//        this.actor = actor;
//        this.actor_type = actor_type;
//        this.actor_name = actor_name;
//        this.actee = actee;
//        this.actee_type = actee_type;
//        this.actee_name = actee_name;
//        this.timestamp = timestamp;
//        this.instance = instance;
//        this.index = index;
//        this.exit_status = exit_status;
//        this.exit_description = exit_description;
//        this.reason = reason;
//    }

    @SuppressWarnings("unchecked")
    public CloudEvent(Map<String, Object> attributes) {
        super(CloudEntity.Meta.defaultMeta(), parse(attributes.get("name")));
        type = (String)attributes.get("type");
        actor = (UUID)attributes.get("actor");
        actor_type = (String) attributes.get("actor_type");
        actor_name = (String) attributes.get("actor_name");
        actee = (UUID)attributes.get("actee");
        actee_type = (String) attributes.get("actee_type");
        actee_name = (String) attributes.get("actee_name");
        timestamp = (Date)attributes.get("timestamp");
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public UUID getActor() {
        return actor;
    }

    public void setActor(UUID actor) {
        this.actor = actor;
    }

    public String getActorType() {
        return actor_type;
    }

    public void setActorType(String actorType) {
        this.actor_type = actorType;
    }

    public String getActorName() {
        return actor_name;
    }

    public void setActorName(String actorName) {
        this.actor_name = actorName;
    }

    public UUID getActee() {
        return actee;
    }

    public void setActee(UUID actee) {
        this.actee = actee;
    }

    public String getActeeType() {
        return actee_type;
    }

    public void setActeeType(String acteeType) {
        this.actee_type = acteeType;
    }

    public String getActeeName() {
        return actee_name;
    }

    public void setActeeName(String acteeName) {
        this.actee_name = acteeName;
    }

    public Date getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Date timestamp) {
        this.timestamp = timestamp;
    }
//
//    public String getInstance() {
//        return instance;
//    }
//
//    public int getIndex() {
//        return index;
//    }
//
//    public int getExitStatus() {
//        return exit_status;
//    }
//
//    public String getExitDescription() {
//        return exit_description;
//    }
//
//    public String getReason() {
//        return reason;
//    }

}
