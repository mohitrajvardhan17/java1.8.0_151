package sun.security.action;

import java.security.PrivilegedAction;

public class GetBooleanAction
  implements PrivilegedAction<Boolean>
{
  private String theProp;
  
  public GetBooleanAction(String paramString)
  {
    theProp = paramString;
  }
  
  public Boolean run()
  {
    return Boolean.valueOf(Boolean.getBoolean(theProp));
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\sun\security\action\GetBooleanAction.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */