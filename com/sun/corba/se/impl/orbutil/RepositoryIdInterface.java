package com.sun.corba.se.impl.orbutil;

import java.net.MalformedURLException;

public abstract interface RepositoryIdInterface
{
  public abstract Class getClassFromType()
    throws ClassNotFoundException;
  
  public abstract Class getClassFromType(String paramString)
    throws ClassNotFoundException, MalformedURLException;
  
  public abstract Class getClassFromType(Class paramClass, String paramString)
    throws ClassNotFoundException, MalformedURLException;
  
  public abstract String getClassName();
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\corba\se\impl\orbutil\RepositoryIdInterface.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */