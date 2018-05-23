package com.sun.corba.se.impl.naming.pcosnaming;

import java.io.Serializable;
import org.omg.CosNaming.NameComponent;

public class InternalBindingKey
  implements Serializable
{
  private static final long serialVersionUID = -5410796631793704055L;
  public String id;
  public String kind;
  
  public InternalBindingKey() {}
  
  public InternalBindingKey(NameComponent paramNameComponent)
  {
    setup(paramNameComponent);
  }
  
  protected void setup(NameComponent paramNameComponent)
  {
    id = id;
    kind = kind;
  }
  
  public boolean equals(Object paramObject)
  {
    if (paramObject == null) {
      return false;
    }
    if ((paramObject instanceof InternalBindingKey))
    {
      InternalBindingKey localInternalBindingKey = (InternalBindingKey)paramObject;
      if ((id != null) && (id != null))
      {
        if (id.length() != id.length()) {
          return false;
        }
        if ((id.length() > 0) && (!id.equals(id))) {
          return false;
        }
      }
      else if (((id == null) && (id != null)) || ((id != null) && (id == null)))
      {
        return false;
      }
      if ((kind != null) && (kind != null))
      {
        if (kind.length() != kind.length()) {
          return false;
        }
        if ((kind.length() > 0) && (!kind.equals(kind))) {
          return false;
        }
      }
      else if (((kind == null) && (kind != null)) || ((kind != null) && (kind == null)))
      {
        return false;
      }
      return true;
    }
    return false;
  }
  
  public int hashCode()
  {
    int i = 0;
    if (id.length() > 0) {
      i += id.hashCode();
    }
    if (kind.length() > 0) {
      i += kind.hashCode();
    }
    return i;
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\corba\se\impl\naming\pcosnaming\InternalBindingKey.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */