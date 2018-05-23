package com.sun.corba.se.impl.legacy.connection;

import com.sun.corba.se.spi.legacy.connection.LegacyServerSocketEndPointInfo;
import com.sun.corba.se.spi.transport.SocketInfo;

public class EndPointInfoImpl
  implements SocketInfo, LegacyServerSocketEndPointInfo
{
  protected String type;
  protected String hostname;
  protected int port;
  protected int locatorPort;
  protected String name;
  
  public EndPointInfoImpl(String paramString1, int paramInt, String paramString2)
  {
    type = paramString1;
    port = paramInt;
    hostname = paramString2;
    locatorPort = -1;
    name = "NO_NAME";
  }
  
  public String getType()
  {
    return type;
  }
  
  public String getHost()
  {
    return hostname;
  }
  
  public String getHostName()
  {
    return hostname;
  }
  
  public int getPort()
  {
    return port;
  }
  
  public int getLocatorPort()
  {
    return locatorPort;
  }
  
  public void setLocatorPort(int paramInt)
  {
    locatorPort = paramInt;
  }
  
  public String getName()
  {
    return name;
  }
  
  public int hashCode()
  {
    return type.hashCode() ^ hostname.hashCode() ^ port;
  }
  
  public boolean equals(Object paramObject)
  {
    if (!(paramObject instanceof EndPointInfoImpl)) {
      return false;
    }
    EndPointInfoImpl localEndPointInfoImpl = (EndPointInfoImpl)paramObject;
    if (type == null)
    {
      if (type != null) {
        return false;
      }
    }
    else if (!type.equals(type)) {
      return false;
    }
    if (port != port) {
      return false;
    }
    return hostname.equals(hostname);
  }
  
  public String toString()
  {
    return type + " " + name + " " + hostname + " " + port;
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\corba\se\impl\legacy\connection\EndPointInfoImpl.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */