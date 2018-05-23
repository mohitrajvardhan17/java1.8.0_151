package com.sun.corba.se.spi.activation;

import org.omg.CORBA.UserException;

public final class ORBAlreadyRegistered
  extends UserException
{
  public String orbId = null;
  
  public ORBAlreadyRegistered()
  {
    super(ORBAlreadyRegisteredHelper.id());
  }
  
  public ORBAlreadyRegistered(String paramString)
  {
    super(ORBAlreadyRegisteredHelper.id());
    orbId = paramString;
  }
  
  public ORBAlreadyRegistered(String paramString1, String paramString2)
  {
    super(ORBAlreadyRegisteredHelper.id() + "  " + paramString1);
    orbId = paramString2;
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\corba\se\spi\activation\ORBAlreadyRegistered.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */