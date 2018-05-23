package com.sun.xml.internal.ws.spi.db;

public abstract interface PropertyGetter
{
  public abstract Class getType();
  
  public abstract <A> A getAnnotation(Class<A> paramClass);
  
  public abstract Object get(Object paramObject);
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\xml\internal\ws\spi\db\PropertyGetter.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */