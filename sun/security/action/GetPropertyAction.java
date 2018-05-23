package sun.security.action;

import java.security.AccessController;
import java.security.PrivilegedAction;

public class GetPropertyAction
  implements PrivilegedAction<String>
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
  
  public String run()
  {
    String str = System.getProperty(theProp);
    return str == null ? defaultVal : str;
  }
  
  public static String privilegedGetProperty(String paramString)
  {
    if (System.getSecurityManager() == null) {
      return System.getProperty(paramString);
    }
    return (String)AccessController.doPrivileged(new GetPropertyAction(paramString));
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\sun\security\action\GetPropertyAction.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */