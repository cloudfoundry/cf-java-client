package org.cloudfoundry.client.lib;

import org.cloudfoundry.client.lib.domain.ApplicationLog;

public interface ApplicationLogListener {
	void onMessage(ApplicationLog log);

	void onComplete();

	void onError(Throwable exception);

}
