package com.sun.corba.se.impl.naming.cosnaming;

import org.omg.CosNaming.NameComponent;

public class InternalBindingKey
{
  public NameComponent name;
  private int idLen;
  private int kindLen;
  private int hashVal;
  
  public InternalBindingKey() {}
  
  public InternalBindingKey(NameComponent paramNameComponent)
  {
    idLen = 0;
    kindLen = 0;
    setup(paramNameComponent);
  }
  
  protected void setup(NameComponent paramNameComponent)
  {
    name = paramNameComponent;
    if (name.id != null) {
      idLen = name.id.length();
    }
    if (name.kind != null) {
      kindLen = name.kind.length();
    }
    hashVal = 0;
    if (idLen > 0) {
      hashVal += name.id.hashCode();
    }
    if (kindLen > 0) {
      hashVal += name.kind.hashCode();
    }
  }
  
  public boolean equals(Object paramObject)
  {
    if (paramObject == null) {
      return false;
    }
    if ((paramObject instanceof InternalBindingKey))
    {
      InternalBindingKey localInternalBindingKey = (InternalBindingKey)paramObject;
      if ((idLen != idLen) || (kindLen != kindLen)) {
        return false;
      }
      if ((idLen > 0) && (!name.id.equals(name.id))) {
        return false;
      }
      return (kindLen <= 0) || (name.kind.equals(name.kind));
    }
    return false;
  }
  
  public int hashCode()
  {
    return hashVal;
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\corba\se\impl\naming\cosnaming\InternalBindingKey.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */