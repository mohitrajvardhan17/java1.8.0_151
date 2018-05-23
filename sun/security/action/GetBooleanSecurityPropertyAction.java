package sun.security.action;

import java.security.PrivilegedAction;
import java.security.Security;

public class GetBooleanSecurityPropertyAction
  implements PrivilegedAction<Boolean>
{
  private String theProp;
  
  public GetBooleanSecurityPropertyAction(String paramString)
  {
    theProp = paramString;
  }
  
  public Boolean run()
  {
    boolean bool = false;
    try
    {
      String str = Security.getProperty(theProp);
      bool = (str != null) && (str.equalsIgnoreCase("true"));
    }
    catch (NullPointerException localNullPointerException) {}
    return Boolean.valueOf(bool);
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\sun\security\action\GetBooleanSecurityPropertyAction.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */