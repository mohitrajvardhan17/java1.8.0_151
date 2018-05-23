package com.sun.corba.se.impl.protocol.giopmsgheaders;

import com.sun.corba.se.spi.ior.IOR;
import org.omg.CORBA.SystemException;

public abstract interface LocateReplyOrReplyMessage
  extends Message
{
  public abstract int getRequestId();
  
  public abstract int getReplyStatus();
  
  public abstract SystemException getSystemException(String paramString);
  
  public abstract IOR getIOR();
  
  public abstract short getAddrDisposition();
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\corba\se\impl\protocol\giopmsgheaders\LocateReplyOrReplyMessage.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */