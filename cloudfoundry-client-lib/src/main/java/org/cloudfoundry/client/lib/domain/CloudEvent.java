package org.cloudfoundry.client.lib.domain;

/**
 * @author Mark Seidenstricker
 */

import java.util.*;

import org.codehaus.jackson.annotate.JsonAutoDetect;
import org.codehaus.jackson.annotate.JsonAutoDetect.Visibility;

@JsonAutoDetect(fieldVisibility = Visibility.ANY, getterVisibility = Visibility.NONE, creatorVisibility = Visibility.NONE)
public class CloudEvent extends CloudEntity {

	private String type;
	private String actor;
	private String actor_type;
	private String actor_name;
	private String actee;
	private String actee_type;
	private String actee_name;
	private Date timestamp;

	public CloudEvent(Meta meta, String name) {
		super(meta, name);
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getActor() {
		return actor;
	}

	public UUID getActorGuid() {
		try {
			return UUID.fromString(actor);
		} catch (IllegalArgumentException e) {
			return null;
		}
	}

	public void setActor(String actor) {
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

	public String getActee() {
		return actee;
	}

	public UUID getActeeGuid() {
		try {
			return UUID.fromString(actee);
		} catch (IllegalArgumentException e) {
			return null;
		}
	}

	public void setActee(String actee) {
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

}
