package com.sun.security.auth;

import java.text.MessageFormat;
import jdk.Exported;
import sun.security.util.ResourcesMgr;

@Exported
public class NTNumericCredential
{
  private long impersonationToken;
  
  public NTNumericCredential(long paramLong)
  {
    impersonationToken = paramLong;
  }
  
  public long getToken()
  {
    return impersonationToken;
  }
  
  public String toString()
  {
    MessageFormat localMessageFormat = new MessageFormat(ResourcesMgr.getString("NTNumericCredential.name", "sun.security.util.AuthResources"));
    Object[] arrayOfObject = { Long.toString(impersonationToken) };
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
    if (!(paramObject instanceof NTNumericCredential)) {
      return false;
    }
    NTNumericCredential localNTNumericCredential = (NTNumericCredential)paramObject;
    return impersonationToken == localNTNumericCredential.getToken();
  }
  
  public int hashCode()
  {
    return (int)impersonationToken;
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\security\auth\NTNumericCredential.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */