package com.sun.corba.se.spi.activation;

import com.sun.corba.se.spi.activation.InitialNameServicePackage.NameAlreadyBound;

public abstract interface InitialNameServiceOperations
{
  public abstract void bind(String paramString, org.omg.CORBA.Object paramObject, boolean paramBoolean)
    throws NameAlreadyBound;
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\corba\se\spi\activation\InitialNameServiceOperations.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */