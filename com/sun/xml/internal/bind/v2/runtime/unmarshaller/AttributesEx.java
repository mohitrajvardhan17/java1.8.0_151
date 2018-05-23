package com.sun.xml.internal.bind.v2.runtime.unmarshaller;

import org.xml.sax.Attributes;

public abstract interface AttributesEx
  extends Attributes
{
  public abstract CharSequence getData(int paramInt);
  
  public abstract CharSequence getData(String paramString1, String paramString2);
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\xml\internal\bind\v2\runtime\unmarshaller\AttributesEx.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */