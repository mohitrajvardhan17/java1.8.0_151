package com.sun.corba.se.impl.protocol.giopmsgheaders;

public abstract interface FragmentMessage
  extends Message
{
  public abstract int getRequestId();
  
  public abstract int getHeaderLength();
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\corba\se\impl\protocol\giopmsgheaders\FragmentMessage.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */