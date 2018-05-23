package com.sun.nio.sctp;

import jdk.Exported;

@Exported
public class InvalidStreamException
  extends IllegalArgumentException
{
  private static final long serialVersionUID = -9172703378046665558L;
  
  public InvalidStreamException() {}
  
  public InvalidStreamException(String paramString)
  {
    super(paramString);
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\nio\sctp\InvalidStreamException.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */