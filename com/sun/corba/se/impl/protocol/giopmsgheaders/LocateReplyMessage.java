package com.sun.corba.se.impl.protocol.giopmsgheaders;

public abstract interface LocateReplyMessage
  extends Message, LocateReplyOrReplyMessage
{
  public static final int UNKNOWN_OBJECT = 0;
  public static final int OBJECT_HERE = 1;
  public static final int OBJECT_FORWARD = 2;
  public static final int OBJECT_FORWARD_PERM = 3;
  public static final int LOC_SYSTEM_EXCEPTION = 4;
  public static final int LOC_NEEDS_ADDRESSING_MODE = 5;
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\corba\se\impl\protocol\giopmsgheaders\LocateReplyMessage.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */