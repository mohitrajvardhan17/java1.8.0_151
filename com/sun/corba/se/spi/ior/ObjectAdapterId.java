package com.sun.corba.se.spi.ior;

import java.util.Iterator;

public abstract interface ObjectAdapterId
  extends Writeable
{
  public abstract int getNumLevels();
  
  public abstract Iterator iterator();
  
  public abstract String[] getAdapterName();
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\corba\se\spi\ior\ObjectAdapterId.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */