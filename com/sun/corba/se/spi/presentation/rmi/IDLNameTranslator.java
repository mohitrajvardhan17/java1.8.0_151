package com.sun.corba.se.spi.presentation.rmi;

import java.lang.reflect.Method;

public abstract interface IDLNameTranslator
{
  public abstract Class[] getInterfaces();
  
  public abstract Method[] getMethods();
  
  public abstract Method getMethod(String paramString);
  
  public abstract String getIDLName(Method paramMethod);
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\corba\se\spi\presentation\rmi\IDLNameTranslator.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */