package com.sun.xml.internal.ws.client.sei;

import javax.xml.ws.Holder;

 enum ValueGetter
{
  PLAIN,  HOLDER;
  
  private ValueGetter() {}
  
  abstract Object get(Object paramObject);
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\xml\internal\ws\client\sei\ValueGetter.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */