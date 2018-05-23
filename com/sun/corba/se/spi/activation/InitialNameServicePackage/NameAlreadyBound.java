package com.sun.corba.se.spi.activation.InitialNameServicePackage;

import org.omg.CORBA.UserException;

public final class NameAlreadyBound
  extends UserException
{
  public NameAlreadyBound()
  {
    super(NameAlreadyBoundHelper.id());
  }
  
  public NameAlreadyBound(String paramString)
  {
    super(NameAlreadyBoundHelper.id() + "  " + paramString);
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\corba\se\spi\activation\InitialNameServicePackage\NameAlreadyBound.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */