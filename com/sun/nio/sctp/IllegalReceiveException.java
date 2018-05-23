package com.sun.nio.sctp;

import jdk.Exported;

@Exported
public class IllegalReceiveException
  extends IllegalStateException
{
  private static final long serialVersionUID = 2296619040988576224L;
  
  public IllegalReceiveException() {}
  
  public IllegalReceiveException(String paramString)
  {
    super(paramString);
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\nio\sctp\IllegalReceiveException.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */