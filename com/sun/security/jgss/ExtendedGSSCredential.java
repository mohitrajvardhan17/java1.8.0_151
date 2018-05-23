package com.sun.security.jgss;

import jdk.Exported;
import org.ietf.jgss.GSSCredential;
import org.ietf.jgss.GSSException;
import org.ietf.jgss.GSSName;

@Exported
public abstract interface ExtendedGSSCredential
  extends GSSCredential
{
  public abstract GSSCredential impersonate(GSSName paramGSSName)
    throws GSSException;
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\security\jgss\ExtendedGSSCredential.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */