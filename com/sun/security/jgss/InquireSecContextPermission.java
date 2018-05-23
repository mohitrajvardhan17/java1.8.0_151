package com.sun.security.jgss;

import java.security.BasicPermission;
import jdk.Exported;

@Exported
public final class InquireSecContextPermission
  extends BasicPermission
{
  private static final long serialVersionUID = -7131173349668647297L;
  
  public InquireSecContextPermission(String paramString)
  {
    super(paramString);
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\security\jgss\InquireSecContextPermission.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */