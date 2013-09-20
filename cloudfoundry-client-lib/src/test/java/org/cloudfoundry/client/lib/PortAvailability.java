package org.cloudfoundry.client.lib;


import java.io.IOException;
import java.net.DatagramSocket;
import java.net.ServerSocket;
import java.util.logging.Level;
import java.util.logging.Logger;


/**
 * Utility class used within the InJvm http proxy tests
 * copied from https://github.com/TheLadders/embedded-test-jetty */
public final class PortAvailability
{
  private static final Logger LOGGER = Logger.getLogger(PortAvailability.class.getName());


  /**
   * Checks to see if a specific port is available.
   * 
   * @param port
   *          the port to check for availability
   */
  /*
   * Slightly adapted from Apache MINA (took out min/max port check)
   */
  public static boolean available(int port)
  {
    ServerSocket ss = null;
    DatagramSocket ds = null;
    try
    {
      ss = new ServerSocket(port);
      ds = new DatagramSocket(port);
      ss.setReuseAddress(true);
      ds.setReuseAddress(true);
      return true;
    }
    catch (IOException e)
    {
      LOGGER.log(Level.FINE, "Error checking port availability", e);
    }
    finally
    {
      close(ss);
      close(ds);
    }

    return false;
  }


  private static void close(DatagramSocket ds)
  {
    if (ds != null)
    {
      ds.close();
    }
  }


  private static void close(ServerSocket ss)
  {
    if (ss != null)
    {
      try
      {
        ss.close();
      }
      catch (IOException e)
      {
        LOGGER.log(Level.WARNING, "Error closing socket", e);
      }
    }
  }
}
