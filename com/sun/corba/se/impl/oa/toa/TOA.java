package com.sun.corba.se.impl.oa.toa;

import com.sun.corba.se.spi.oa.ObjectAdapter;

public abstract interface TOA
  extends ObjectAdapter
{
  public abstract void connect(org.omg.CORBA.Object paramObject);
  
  public abstract void disconnect(org.omg.CORBA.Object paramObject);
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\corba\se\impl\oa\toa\TOA.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */