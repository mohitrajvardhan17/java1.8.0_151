package com.sun.org.apache.xerces.internal.util;

public enum Status
{
  SET((short)-3, false),  UNKNOWN((short)-2, false),  RECOGNIZED((short)-1, false),  NOT_SUPPORTED((short)0, true),  NOT_RECOGNIZED((short)1, true),  NOT_ALLOWED((short)2, true);
  
  private final short type;
  private boolean isExceptional;
  
  private Status(short paramShort, boolean paramBoolean)
  {
    type = paramShort;
    isExceptional = paramBoolean;
  }
  
  public short getType()
  {
    return type;
  }
  
  public boolean isExceptional()
  {
    return isExceptional;
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\org\apache\xerces\internal\util\Status.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */