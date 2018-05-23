package com.sun.corba.se.impl.orbutil;

import com.sun.corba.se.impl.io.TypeMismatchException;
import java.io.Serializable;

public abstract interface RepositoryIdStrings
{
  public abstract String createForAnyType(Class paramClass);
  
  public abstract String createForJavaType(Serializable paramSerializable)
    throws TypeMismatchException;
  
  public abstract String createForJavaType(Class paramClass)
    throws TypeMismatchException;
  
  public abstract String createSequenceRepID(Object paramObject);
  
  public abstract String createSequenceRepID(Class paramClass);
  
  public abstract RepositoryIdInterface getFromString(String paramString);
  
  public abstract String getClassDescValueRepId();
  
  public abstract String getWStringValueRepId();
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\corba\se\impl\orbutil\RepositoryIdStrings.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */