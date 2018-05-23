package com.sun.corba.se.spi.activation;

import org.omg.CORBA.portable.IDLEntity;

public final class ORBPortInfo
  implements IDLEntity
{
  public String orbId = null;
  public int port = 0;
  
  public ORBPortInfo() {}
  
  public ORBPortInfo(String paramString, int paramInt)
  {
    orbId = paramString;
    port = paramInt;
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\corba\se\spi\activation\ORBPortInfo.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */