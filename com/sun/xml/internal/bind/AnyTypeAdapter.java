package com.sun.xml.internal.bind;

import javax.xml.bind.annotation.adapters.XmlAdapter;

public final class AnyTypeAdapter
  extends XmlAdapter<Object, Object>
{
  public AnyTypeAdapter() {}
  
  public Object unmarshal(Object paramObject)
  {
    return paramObject;
  }
  
  public Object marshal(Object paramObject)
  {
    return paramObject;
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\xml\internal\bind\AnyTypeAdapter.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */