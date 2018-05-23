package sun.security.action;

import java.security.PrivilegedAction;

public class GetIntegerAction
  implements PrivilegedAction<Integer>
{
  private String theProp;
  private int defaultVal;
  private boolean defaultSet = false;
  
  public GetIntegerAction(String paramString)
  {
    theProp = paramString;
  }
  
  public GetIntegerAction(String paramString, int paramInt)
  {
    theProp = paramString;
    defaultVal = paramInt;
    defaultSet = true;
  }
  
  public Integer run()
  {
    Integer localInteger = Integer.getInteger(theProp);
    if ((localInteger == null) && (defaultSet)) {
      return new Integer(defaultVal);
    }
    return localInteger;
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\sun\security\action\GetIntegerAction.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */