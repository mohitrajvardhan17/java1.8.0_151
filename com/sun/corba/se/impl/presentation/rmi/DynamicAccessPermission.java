package com.sun.corba.se.impl.presentation.rmi;

import java.security.BasicPermission;

public final class DynamicAccessPermission
  extends BasicPermission
{
  public DynamicAccessPermission(String paramString)
  {
    super(paramString);
  }
  
  public DynamicAccessPermission(String paramString1, String paramString2)
  {
    super(paramString1, paramString2);
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\corba\se\impl\presentation\rmi\DynamicAccessPermission.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */