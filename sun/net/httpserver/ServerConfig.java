package sun.net.httpserver;

import java.security.AccessController;
import java.security.PrivilegedAction;
import java.util.logging.Logger;

class ServerConfig
{
  private static final int DEFAULT_CLOCK_TICK = 10000;
  private static final long DEFAULT_IDLE_INTERVAL = 30L;
  private static final int DEFAULT_MAX_IDLE_CONNECTIONS = 200;
  private static final long DEFAULT_MAX_REQ_TIME = -1L;
  private static final long DEFAULT_MAX_RSP_TIME = -1L;
  private static final long DEFAULT_TIMER_MILLIS = 1000L;
  private static final int DEFAULT_MAX_REQ_HEADERS = 200;
  private static final long DEFAULT_DRAIN_AMOUNT = 65536L;
  private static int clockTick;
  private static long idleInterval;
  private static long drainAmount;
  private static int maxIdleConnections;
  private static int maxReqHeaders;
  private static long maxReqTime;
  private static long maxRspTime;
  private static long timerMillis;
  private static boolean debug;
  private static boolean noDelay;
  
  ServerConfig() {}
  
  static void checkLegacyProperties(Logger paramLogger)
  {
    AccessController.doPrivileged(new PrivilegedAction()
    {
      public Void run()
      {
        if (System.getProperty("sun.net.httpserver.readTimeout") != null) {
          val$logger.warning("sun.net.httpserver.readTimeout property is no longer used. Use sun.net.httpserver.maxReqTime instead.");
        }
        if (System.getProperty("sun.net.httpserver.writeTimeout") != null) {
          val$logger.warning("sun.net.httpserver.writeTimeout property is no longer used. Use sun.net.httpserver.maxRspTime instead.");
        }
        if (System.getProperty("sun.net.httpserver.selCacheTimeout") != null) {
          val$logger.warning("sun.net.httpserver.selCacheTimeout property is no longer used.");
        }
        return null;
      }
    });
  }
  
  static boolean debugEnabled()
  {
    return debug;
  }
  
  static long getIdleInterval()
  {
    return idleInterval;
  }
  
  static int getClockTick()
  {
    return clockTick;
  }
  
  static int getMaxIdleConnections()
  {
    return maxIdleConnections;
  }
  
  static long getDrainAmount()
  {
    return drainAmount;
  }
  
  static int getMaxReqHeaders()
  {
    return maxReqHeaders;
  }
  
  static long getMaxReqTime()
  {
    return maxReqTime;
  }
  
  static long getMaxRspTime()
  {
    return maxRspTime;
  }
  
  static long getTimerMillis()
  {
    return timerMillis;
  }
  
  static boolean noDelay()
  {
    return noDelay;
  }
  
  static
  {
    AccessController.doPrivileged(new PrivilegedAction()
    {
      public Void run()
      {
        ServerConfig.access$002(Long.getLong("sun.net.httpserver.idleInterval", 30L).longValue() * 1000L);
        ServerConfig.access$102(Integer.getInteger("sun.net.httpserver.clockTick", 10000).intValue());
        ServerConfig.access$202(Integer.getInteger("sun.net.httpserver.maxIdleConnections", 200).intValue());
        ServerConfig.access$302(Long.getLong("sun.net.httpserver.drainAmount", 65536L).longValue());
        ServerConfig.access$402(Integer.getInteger("sun.net.httpserver.maxReqHeaders", 200).intValue());
        ServerConfig.access$502(Long.getLong("sun.net.httpserver.maxReqTime", -1L).longValue());
        ServerConfig.access$602(Long.getLong("sun.net.httpserver.maxRspTime", -1L).longValue());
        ServerConfig.access$702(Long.getLong("sun.net.httpserver.timerMillis", 1000L).longValue());
        ServerConfig.access$802(Boolean.getBoolean("sun.net.httpserver.debug"));
        ServerConfig.access$902(Boolean.getBoolean("sun.net.httpserver.nodelay"));
        return null;
      }
    });
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\sun\net\httpserver\ServerConfig.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */