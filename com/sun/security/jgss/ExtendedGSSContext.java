package com.sun.security.jgss;

import jdk.Exported;
import org.ietf.jgss.GSSContext;
import org.ietf.jgss.GSSException;

@Exported
public abstract interface ExtendedGSSContext
  extends GSSContext
{
  public abstract Object inquireSecContext(InquireType paramInquireType)
    throws GSSException;
  
  public abstract void requestDelegPolicy(boolean paramBoolean)
    throws GSSException;
  
  public abstract boolean getDelegPolicyState();
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\security\jgss\ExtendedGSSContext.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */