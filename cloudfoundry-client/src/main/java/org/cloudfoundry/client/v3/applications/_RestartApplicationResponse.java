package org.cloudfoundry.client.v3.applications;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import org.immutables.value.Value;

/**
 * The response payload for the Start Application operation
 */
@JsonDeserialize
@Value.Immutable
abstract class _RestartApplicationResponse extends Application {

}