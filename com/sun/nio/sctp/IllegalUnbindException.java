package com.sun.nio.sctp;

import jdk.Exported;

@Exported
public class IllegalUnbindException
  extends IllegalStateException
{
  private static final long serialVersionUID = -310540883995532224L;
  
  public IllegalUnbindException() {}
  
  public IllegalUnbindException(String paramString)
  {
    super(paramString);
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\nio\sctp\IllegalUnbindException.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */