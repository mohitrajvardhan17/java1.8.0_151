package com.sun.corba.se.impl.corba;

import org.omg.CORBA.Principal;

public class PrincipalImpl
  extends Principal
{
  private byte[] value;
  
  public PrincipalImpl() {}
  
  public void name(byte[] paramArrayOfByte)
  {
    value = paramArrayOfByte;
  }
  
  public byte[] name()
  {
    return value;
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\corba\se\impl\corba\PrincipalImpl.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */