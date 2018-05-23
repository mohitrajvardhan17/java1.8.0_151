package com.sun.corba.se.spi.activation.LocatorPackage;

import com.sun.corba.se.spi.activation.ORBPortInfo;
import org.omg.CORBA.portable.IDLEntity;

public final class ServerLocation
  implements IDLEntity
{
  public String hostname = null;
  public ORBPortInfo[] ports = null;
  
  public ServerLocation() {}
  
  public ServerLocation(String paramString, ORBPortInfo[] paramArrayOfORBPortInfo)
  {
    hostname = paramString;
    ports = paramArrayOfORBPortInfo;
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\corba\se\spi\activation\LocatorPackage\ServerLocation.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */