package com.sun.xml.internal.bind.api;

public abstract class RawAccessor<B, V>
{
  public RawAccessor() {}
  
  public abstract V get(B paramB)
    throws AccessorException;
  
  public abstract void set(B paramB, V paramV)
    throws AccessorException;
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\xml\internal\bind\api\RawAccessor.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */