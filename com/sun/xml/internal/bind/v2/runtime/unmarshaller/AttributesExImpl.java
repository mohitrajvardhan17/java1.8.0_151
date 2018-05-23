package com.sun.xml.internal.bind.v2.runtime.unmarshaller;

import com.sun.xml.internal.bind.util.AttributesImpl;

public final class AttributesExImpl
  extends AttributesImpl
  implements AttributesEx
{
  public AttributesExImpl() {}
  
  public CharSequence getData(int paramInt)
  {
    return getValue(paramInt);
  }
  
  public CharSequence getData(String paramString1, String paramString2)
  {
    return getValue(paramString1, paramString2);
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\xml\internal\bind\v2\runtime\unmarshaller\AttributesExImpl.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */