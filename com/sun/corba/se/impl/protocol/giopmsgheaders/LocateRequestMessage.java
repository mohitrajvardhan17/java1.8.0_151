package com.sun.corba.se.impl.protocol.giopmsgheaders;

import com.sun.corba.se.spi.ior.ObjectKey;

public abstract interface LocateRequestMessage
  extends Message
{
  public abstract int getRequestId();
  
  public abstract ObjectKey getObjectKey();
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\corba\se\impl\protocol\giopmsgheaders\LocateRequestMessage.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */