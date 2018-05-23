package com.sun.corba.se.impl.legacy.connection;

public class USLPort
{
  private String type;
  private int port;
  
  public USLPort(String paramString, int paramInt)
  {
    type = paramString;
    port = paramInt;
  }
  
  public String getType()
  {
    return type;
  }
  
  public int getPort()
  {
    return port;
  }
  
  public String toString()
  {
    return type + ":" + port;
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\corba\se\impl\legacy\connection\USLPort.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */