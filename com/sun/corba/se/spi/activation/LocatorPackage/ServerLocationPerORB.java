package com.sun.corba.se.spi.activation.LocatorPackage;

import com.sun.corba.se.spi.activation.EndPointInfo;
import org.omg.CORBA.portable.IDLEntity;

public final class ServerLocationPerORB
  implements IDLEntity
{
  public String hostname = null;
  public EndPointInfo[] ports = null;
  
  public ServerLocationPerORB() {}
  
  public ServerLocationPerORB(String paramString, EndPointInfo[] paramArrayOfEndPointInfo)
  {
    hostname = paramString;
    ports = paramArrayOfEndPointInfo;
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\corba\se\spi\activation\LocatorPackage\ServerLocationPerORB.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */