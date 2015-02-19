package org.cloudfoundry.client.lib.rest;

import java.io.IOException;
import java.util.Timer;
import java.util.TimerTask;

import javax.websocket.Session;

import org.cloudfoundry.client.lib.StreamingLogToken;

public class StreamingLogTokenImpl implements StreamingLogToken {
    private static long keepAliveTime = 25000; // 25 seconds to match the go client
    
    private Timer keepAliveTimer = new Timer(true);

    private Session session;

    public StreamingLogTokenImpl(Session session) {
        this.session = session;
                
        keepAliveTimer.scheduleAtFixedRate(new KeepAliveTimerTask(), keepAliveTime, keepAliveTime);
    }
    
    public void cancel() {
        keepAliveTimer.cancel();
        try {
            session.close();
        } catch (IOException e) {
            // Ignore
        }
    }

    private class KeepAliveTimerTask extends TimerTask {
        @Override
        public void run() {
            if (session.isOpen()) {
                session.getAsyncRemote().sendText("keep alive");
            } else {
                keepAliveTimer.cancel();
            }
        }
    }
}
