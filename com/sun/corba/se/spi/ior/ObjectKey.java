package com.sun.corba.se.spi.ior;

import com.sun.corba.se.spi.protocol.CorbaServerRequestDispatcher;

public abstract interface ObjectKey
  extends Writeable
{
  public abstract ObjectId getId();
  
  public abstract ObjectKeyTemplate getTemplate();
  
  public abstract byte[] getBytes(org.omg.CORBA.ORB paramORB);
  
  public abstract CorbaServerRequestDispatcher getServerRequestDispatcher(com.sun.corba.se.spi.orb.ORB paramORB);
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\corba\se\spi\ior\ObjectKey.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */