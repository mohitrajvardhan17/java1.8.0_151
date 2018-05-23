package com.sun.corba.se.impl.naming.namingutil;

import java.io.PrintStream;

public class IIOPEndpointInfo
{
  private int major = 1;
  private int minor = 0;
  private String host = "localhost";
  private int port = 2089;
  
  IIOPEndpointInfo() {}
  
  public void setHost(String paramString)
  {
    host = paramString;
  }
  
  public String getHost()
  {
    return host;
  }
  
  public void setPort(int paramInt)
  {
    port = paramInt;
  }
  
  public int getPort()
  {
    return port;
  }
  
  public void setVersion(int paramInt1, int paramInt2)
  {
    major = paramInt1;
    minor = paramInt2;
  }
  
  public int getMajor()
  {
    return major;
  }
  
  public int getMinor()
  {
    return minor;
  }
  
  public void dump()
  {
    System.out.println(" Major -> " + major + " Minor -> " + minor);
    System.out.println("host -> " + host);
    System.out.println("port -> " + port);
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\corba\se\impl\naming\namingutil\IIOPEndpointInfo.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */