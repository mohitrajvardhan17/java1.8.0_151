package com.sun.corba.se.impl.orbutil;

import java.security.PrivilegedAction;

public class GetPropertyAction
  implements PrivilegedAction
{
  private String theProp;
  private String defaultVal;
  
  public GetPropertyAction(String paramString)
  {
    theProp = paramString;
  }
  
  public GetPropertyAction(String paramString1, String paramString2)
  {
    theProp = paramString1;
    defaultVal = paramString2;
  }
  
  public Object run()
  {
    String str = System.getProperty(theProp);
    return str == null ? defaultVal : str;
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\corba\se\impl\orbutil\GetPropertyAction.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */