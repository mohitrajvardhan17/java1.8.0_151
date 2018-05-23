package com.sun.xml.internal.ws.spi.db;

public abstract interface PropertyAccessor<B, V>
{
  public abstract V get(B paramB)
    throws DatabindingException;
  
  public abstract void set(B paramB, V paramV)
    throws DatabindingException;
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\xml\internal\ws\spi\db\PropertyAccessor.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */