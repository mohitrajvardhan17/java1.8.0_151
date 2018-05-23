package com.sun.corba.se.spi.ior.iiop;

import com.sun.corba.se.impl.encoding.CodeSetComponentInfo;
import com.sun.corba.se.spi.ior.TaggedComponent;

public abstract interface CodeSetsComponent
  extends TaggedComponent
{
  public abstract CodeSetComponentInfo getCodeSetComponentInfo();
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\corba\se\spi\ior\iiop\CodeSetsComponent.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */