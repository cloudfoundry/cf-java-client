package org.cloudfoundry.client.v3.tasks;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.Singular;
import lombok.ToString;
import org.cloudfoundry.client.v3.Link;

import java.util.Map;

/**
 * The Resource response payload for the List Tasks operation
 */
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public final class TaskResource extends Task {

    @Builder
    TaskResource(@JsonProperty("command") String command,
                 @JsonProperty("created_at") String createdAt,
                 @JsonProperty("environment_variables") @Singular Map<String, String> environmentVariables,
                 @JsonProperty("guid") String id,
                 @JsonProperty("links") @Singular Map<String, Link> links,
                 @JsonProperty("memory_in_mb") Integer memoryInMb,
                 @JsonProperty("name") String name,
                 @JsonProperty("result") @Singular Map<String, Object> results,
                 @JsonProperty("state") String state,
                 @JsonProperty("updated_at") String updatedAt) {

        super(command, createdAt, environmentVariables, id, links, memoryInMb, name, results, state, updatedAt);
    }

}
