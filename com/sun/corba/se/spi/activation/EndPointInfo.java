package com.sun.corba.se.spi.activation;

import org.omg.CORBA.portable.IDLEntity;

public final class EndPointInfo
  implements IDLEntity
{
  public String endpointType = null;
  public int port = 0;
  
  public EndPointInfo() {}
  
  public EndPointInfo(String paramString, int paramInt)
  {
    endpointType = paramString;
    port = paramInt;
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\corba\se\spi\activation\EndPointInfo.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */