package com.sun.corba.se.impl.corba;

public abstract interface TypeCodeFactory
{
  public abstract void setTypeCode(String paramString, TypeCodeImpl paramTypeCodeImpl);
  
  public abstract TypeCodeImpl getTypeCode(String paramString);
  
  public abstract void setTypeCodeForClass(Class paramClass, TypeCodeImpl paramTypeCodeImpl);
  
  public abstract TypeCodeImpl getTypeCodeForClass(Class paramClass);
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\corba\se\impl\corba\TypeCodeFactory.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */