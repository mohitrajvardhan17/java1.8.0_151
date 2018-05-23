package com.sun.security.jgss;

import javax.security.auth.Subject;
import jdk.Exported;
import org.ietf.jgss.GSSCredential;
import org.ietf.jgss.GSSName;

@Exported
public class GSSUtil
{
  public GSSUtil() {}
  
  public static Subject createSubject(GSSName paramGSSName, GSSCredential paramGSSCredential)
  {
    return sun.security.jgss.GSSUtil.getSubject(paramGSSName, paramGSSCredential);
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\security\jgss\GSSUtil.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */