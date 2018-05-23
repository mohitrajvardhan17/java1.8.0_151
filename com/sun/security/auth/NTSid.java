package com.sun.security.auth;

import java.io.Serializable;
import java.security.Principal;
import java.text.MessageFormat;
import jdk.Exported;
import sun.security.util.ResourcesMgr;

@Exported
public class NTSid
  implements Principal, Serializable
{
  private static final long serialVersionUID = 4412290580770249885L;
  private String sid;
  
  public NTSid(String paramString)
  {
    if (paramString == null)
    {
      MessageFormat localMessageFormat = new MessageFormat(ResourcesMgr.getString("invalid.null.input.value", "sun.security.util.AuthResources"));
      Object[] arrayOfObject = { "stringSid" };
      throw new NullPointerException(localMessageFormat.format(arrayOfObject));
    }
    if (paramString.length() == 0) {
      throw new IllegalArgumentException(ResourcesMgr.getString("Invalid.NTSid.value", "sun.security.util.AuthResources"));
    }
    sid = new String(paramString);
  }
  
  public String getName()
  {
    return sid;
  }
  
  public String toString()
  {
    MessageFormat localMessageFormat = new MessageFormat(ResourcesMgr.getString("NTSid.name", "sun.security.util.AuthResources"));
    Object[] arrayOfObject = { sid };
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
    if (!(paramObject instanceof NTSid)) {
      return false;
    }
    NTSid localNTSid = (NTSid)paramObject;
    return sid.equals(sid);
  }
  
  public int hashCode()
  {
    return sid.hashCode();
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\security\auth\NTSid.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */