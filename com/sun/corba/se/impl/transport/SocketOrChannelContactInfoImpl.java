package com.sun.corba.se.impl.transport;

import com.sun.corba.se.impl.orbutil.ORBUtility;
import com.sun.corba.se.pept.transport.Connection;
import com.sun.corba.se.spi.ior.IOR;
import com.sun.corba.se.spi.orb.ORB;
import com.sun.corba.se.spi.transport.CorbaContactInfoList;
import com.sun.corba.se.spi.transport.SocketInfo;

public class SocketOrChannelContactInfoImpl
  extends CorbaContactInfoBase
  implements SocketInfo
{
  protected boolean isHashCodeCached = false;
  protected int cachedHashCode;
  protected String socketType;
  protected String hostname;
  protected int port;
  
  protected SocketOrChannelContactInfoImpl() {}
  
  protected SocketOrChannelContactInfoImpl(ORB paramORB, CorbaContactInfoList paramCorbaContactInfoList)
  {
    orb = paramORB;
    contactInfoList = paramCorbaContactInfoList;
  }
  
  public SocketOrChannelContactInfoImpl(ORB paramORB, CorbaContactInfoList paramCorbaContactInfoList, String paramString1, String paramString2, int paramInt)
  {
    this(paramORB, paramCorbaContactInfoList);
    socketType = paramString1;
    hostname = paramString2;
    port = paramInt;
  }
  
  public SocketOrChannelContactInfoImpl(ORB paramORB, CorbaContactInfoList paramCorbaContactInfoList, IOR paramIOR, short paramShort, String paramString1, String paramString2, int paramInt)
  {
    this(paramORB, paramCorbaContactInfoList, paramString1, paramString2, paramInt);
    effectiveTargetIOR = paramIOR;
    addressingDisposition = paramShort;
  }
  
  public boolean isConnectionBased()
  {
    return true;
  }
  
  public boolean shouldCacheConnection()
  {
    return true;
  }
  
  public String getConnectionCacheType()
  {
    return "SocketOrChannelConnectionCache";
  }
  
  public Connection createConnection()
  {
    SocketOrChannelConnectionImpl localSocketOrChannelConnectionImpl = new SocketOrChannelConnectionImpl(orb, this, socketType, hostname, port);
    return localSocketOrChannelConnectionImpl;
  }
  
  public String getMonitoringName()
  {
    return "SocketConnections";
  }
  
  public String getType()
  {
    return socketType;
  }
  
  public String getHost()
  {
    return hostname;
  }
  
  public int getPort()
  {
    return port;
  }
  
  public int hashCode()
  {
    if (!isHashCodeCached)
    {
      cachedHashCode = (socketType.hashCode() ^ hostname.hashCode() ^ port);
      isHashCodeCached = true;
    }
    return cachedHashCode;
  }
  
  public boolean equals(Object paramObject)
  {
    if (paramObject == null) {
      return false;
    }
    if (!(paramObject instanceof SocketOrChannelContactInfoImpl)) {
      return false;
    }
    SocketOrChannelContactInfoImpl localSocketOrChannelContactInfoImpl = (SocketOrChannelContactInfoImpl)paramObject;
    if (port != port) {
      return false;
    }
    if (!hostname.equals(hostname)) {
      return false;
    }
    if (socketType == null)
    {
      if (socketType != null) {
        return false;
      }
    }
    else if (!socketType.equals(socketType)) {
      return false;
    }
    return true;
  }
  
  public String toString()
  {
    return "SocketOrChannelContactInfoImpl[" + socketType + " " + hostname + " " + port + "]";
  }
  
  protected void dprint(String paramString)
  {
    ORBUtility.dprint("SocketOrChannelContactInfoImpl", paramString);
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\corba\se\impl\transport\SocketOrChannelContactInfoImpl.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */