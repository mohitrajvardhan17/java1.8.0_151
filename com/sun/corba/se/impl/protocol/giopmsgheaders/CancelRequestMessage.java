package com.sun.corba.se.impl.protocol.giopmsgheaders;

public abstract interface CancelRequestMessage
  extends Message
{
  public static final int CANCEL_REQ_MSG_SIZE = 4;
  
  public abstract int getRequestId();
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\corba\se\impl\protocol\giopmsgheaders\CancelRequestMessage.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */