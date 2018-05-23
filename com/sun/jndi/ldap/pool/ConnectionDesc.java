package com.sun.jndi.ldap.pool;

import java.io.PrintStream;

final class ConnectionDesc
{
  private static final boolean debug = Pool.debug;
  static final byte BUSY = 0;
  static final byte IDLE = 1;
  static final byte EXPIRED = 2;
  private final PooledConnection conn;
  private byte state = 1;
  private long idleSince;
  private long useCount = 0L;
  
  ConnectionDesc(PooledConnection paramPooledConnection)
  {
    conn = paramPooledConnection;
  }
  
  ConnectionDesc(PooledConnection paramPooledConnection, boolean paramBoolean)
  {
    conn = paramPooledConnection;
    if (paramBoolean)
    {
      state = 0;
      useCount += 1L;
    }
  }
  
  public boolean equals(Object paramObject)
  {
    return (paramObject != null) && ((paramObject instanceof ConnectionDesc)) && (conn == conn);
  }
  
  public int hashCode()
  {
    return conn.hashCode();
  }
  
  synchronized boolean release()
  {
    d("release()");
    if (state == 0)
    {
      state = 1;
      idleSince = System.currentTimeMillis();
      return true;
    }
    return false;
  }
  
  synchronized PooledConnection tryUse()
  {
    d("tryUse()");
    if (state == 1)
    {
      state = 0;
      useCount += 1L;
      return conn;
    }
    return null;
  }
  
  synchronized boolean expire(long paramLong)
  {
    if ((state == 1) && (idleSince < paramLong))
    {
      d("expire(): expired");
      state = 2;
      conn.closeConnection();
      return true;
    }
    d("expire(): not expired");
    return false;
  }
  
  public String toString()
  {
    return conn.toString() + " " + (state == 1 ? "idle" : state == 0 ? "busy" : "expired");
  }
  
  int getState()
  {
    return state;
  }
  
  long getUseCount()
  {
    return useCount;
  }
  
  private void d(String paramString)
  {
    if (debug) {
      System.err.println("ConnectionDesc." + paramString + " " + toString());
    }
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\jndi\ldap\pool\ConnectionDesc.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */