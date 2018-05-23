package com.sun.security.auth;

import java.text.MessageFormat;
import jdk.Exported;
import sun.security.util.ResourcesMgr;

@Exported
public class NTSidGroupPrincipal
  extends NTSid
{
  private static final long serialVersionUID = -1373347438636198229L;
  
  public NTSidGroupPrincipal(String paramString)
  {
    super(paramString);
  }
  
  public String toString()
  {
    MessageFormat localMessageFormat = new MessageFormat(ResourcesMgr.getString("NTSidGroupPrincipal.name", "sun.security.util.AuthResources"));
    Object[] arrayOfObject = { getName() };
    return localMessageFormat.format(arrayOfObject);
  }
  
  public boolean equals(Object paramObject)
  {
    if (paramObject == null) {
      return false;
    }
    if (this == paramObject) {
      return true;
    }
    if (!(paramObject instanceof NTSidGroupPrincipal)) {
      return false;
    }
    return super.equals(paramObject);
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\security\auth\NTSidGroupPrincipal.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */