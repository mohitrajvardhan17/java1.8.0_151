package com.sun.jmx.mbeanserver;

import java.security.PrivilegedAction;

public class GetPropertyAction
  implements PrivilegedAction<String>
{
  private final String key;
  
  public GetPropertyAction(String paramString)
  {
    key = paramString;
  }
  
  public String run()
  {
    return System.getProperty(key);
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\jmx\mbeanserver\GetPropertyAction.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */