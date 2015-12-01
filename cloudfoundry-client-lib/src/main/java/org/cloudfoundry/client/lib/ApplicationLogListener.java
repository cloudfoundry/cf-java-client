package org.cloudfoundry.client.lib;

import org.cloudfoundry.client.lib.domain.ApplicationLog;

public interface ApplicationLogListener {

    void onComplete();

    void onError(Throwable exception);

    void onMessage(ApplicationLog log);

}
