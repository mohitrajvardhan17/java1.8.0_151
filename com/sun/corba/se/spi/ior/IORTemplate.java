package com.sun.corba.se.spi.ior;

import java.util.Iterator;
import java.util.List;

public abstract interface IORTemplate
  extends List, IORFactory, MakeImmutable
{
  public abstract Iterator iteratorById(int paramInt);
  
  public abstract ObjectKeyTemplate getObjectKeyTemplate();
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\corba\se\spi\ior\IORTemplate.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */