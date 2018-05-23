package com.sun.corba.se.spi.activation;

import org.omg.CORBA.UserException;

public final class NoSuchEndPoint
  extends UserException
{
  public NoSuchEndPoint()
  {
    super(NoSuchEndPointHelper.id());
  }
  
  public NoSuchEndPoint(String paramString)
  {
    super(NoSuchEndPointHelper.id() + "  " + paramString);
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\corba\se\spi\activation\NoSuchEndPoint.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */