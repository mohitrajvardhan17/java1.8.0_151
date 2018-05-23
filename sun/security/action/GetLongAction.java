package sun.security.action;

import java.security.PrivilegedAction;

public class GetLongAction
  implements PrivilegedAction<Long>
{
  private String theProp;
  private long defaultVal;
  private boolean defaultSet = false;
  
  public GetLongAction(String paramString)
  {
    theProp = paramString;
  }
  
  public GetLongAction(String paramString, long paramLong)
  {
    theProp = paramString;
    defaultVal = paramLong;
    defaultSet = true;
  }
  
  public Long run()
  {
    Long localLong = Long.getLong(theProp);
    if ((localLong == null) && (defaultSet)) {
      return new Long(defaultVal);
    }
    return localLong;
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\sun\security\action\GetLongAction.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */