package com.sun.corba.se.spi.transport;

public abstract interface SocketInfo
{
  public static final String IIOP_CLEAR_TEXT = "IIOP_CLEAR_TEXT";
  
  public abstract String getType();
  
  public abstract String getHost();
  
  public abstract int getPort();
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\corba\se\spi\transport\SocketInfo.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */