package com.sun.xml.internal.bind.v2.runtime.property;

import com.sun.xml.internal.bind.v2.runtime.JAXBContextImpl;

public final class UnmarshallerChain
{
  private int offset = 0;
  public final JAXBContextImpl context;
  
  public UnmarshallerChain(JAXBContextImpl paramJAXBContextImpl)
  {
    context = paramJAXBContextImpl;
  }
  
  public int allocateOffset()
  {
    return offset++;
  }
  
  public int getScopeSize()
  {
    return offset;
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\xml\internal\bind\v2\runtime\property\UnmarshallerChain.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */