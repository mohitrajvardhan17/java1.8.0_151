package com.sun.xml.internal.ws.spi.db;

public abstract interface PropertySetter
{
  public abstract Class getType();
  
  public abstract <A> A getAnnotation(Class<A> paramClass);
  
  public abstract void set(Object paramObject1, Object paramObject2);
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\xml\internal\ws\spi\db\PropertySetter.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */