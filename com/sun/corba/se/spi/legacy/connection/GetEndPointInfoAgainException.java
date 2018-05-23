package com.sun.corba.se.spi.legacy.connection;

import com.sun.corba.se.spi.transport.SocketInfo;

public class GetEndPointInfoAgainException
  extends Exception
{
  private SocketInfo socketInfo;
  
  public GetEndPointInfoAgainException(SocketInfo paramSocketInfo)
  {
    socketInfo = paramSocketInfo;
  }
  
  public SocketInfo getEndPointInfo()
  {
    return socketInfo;
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\corba\se\spi\legacy\connection\GetEndPointInfoAgainException.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */